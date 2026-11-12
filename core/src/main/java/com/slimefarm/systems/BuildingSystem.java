package com.slimefarm.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slimefarm.audio.SoundManager;
import com.slimefarm.entities.Entity;
import com.slimefarm.entities.Player;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.inventory.InventorySlot;
import com.slimefarm.render.CorralRenderer;
import com.slimefarm.render.ParticleSystem;
import com.slimefarm.render.WorldRenderer;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.Tile;
import com.slimefarm.world.TileMap;
import com.slimefarm.world.World;

/**
 * Sistema de Construção e Remoção de Cercados estilo Fortnite (Papel de
 * Construção / [B]):
 * <ul>
 * <li><b>Posicionamento manual dos 4 Pilares:</b>
 * <ul>
 * <li><b>Passo 1 (Pilar 1):</b> Clique no chão para fixar o primeiro
 * pilar;</li>
 * <li><b>Passo 2 (Pilar 2):</b> Mova o mouse e clique para fixar o segundo
 * pilar (largura);</li>
 * <li><b>Passo 3 (Pilar 3):</b> Mova o mouse e clique para fixar o terceiro
 * pilar (profundidade);</li>
 * <li><b>Passo 4 (Pilar 4):</b> Clique para fechar o quarto pilar, selando o
 * cercado de vidro!</li>
 * </ul>
 * </li>
 * <li><b>Desmontar / Tirar cercado:</b>
 * <ul>
 * <li>Pressione <b>[X]</b> ou clique com o <b>[Botão Direito]</b> sobre
 * qualquer cercado existente
 * para desmontá-lo e removê-lo do jogo, liberando os slimes que estavam
 * dentro!</li>
 * </ul>
 * </li>
 * <li><b>[Backspace]</b> ou <b>[Botão Direito]</b> durante a colocação desfaz o
 * último pilar posicionado.</li>
 * </ul>
 */
public class BuildingSystem {

    public static final int MIN_SIZE = 3;
    public static final int MAX_SIZE = 10;

    private boolean active;
    private int step = 0; // 0 = Pilar 1, 1 = Pilar 2, 2 = Pilar 3, 3 = Pilar 4

    private final GridPoint2 p1 = new GridPoint2(-1, -1);
    private final GridPoint2 p2 = new GridPoint2(-1, -1);
    private final GridPoint2 p3 = new GridPoint2(-1, -1);
    private final GridPoint2 p4 = new GridPoint2(-1, -1);

    private final GridPoint2 currentHover = new GridPoint2();
    private final Vector3 tmpMouse = new Vector3();
    private final Vector2 tmpWorld = new Vector2();
    private float pulseTime;
    private String lastStatusMessage;

    private Corral hoveredCorral;

    /**
     * Atualiza o estado do sistema a cada quadro.
     */
    public void update(Player player, OrthographicCamera camera, ExtendViewport viewport, CorralSystem corralSystem,
            float dt) {
        pulseTime += dt * 4f;

        // Ativa se estiver segurando o papel de construção (slot 7)
        InventorySlot selectedSlot = player.getInventory().getSelectedSlot();
        boolean holdingBlueprint = (selectedSlot != null && selectedSlot.getType() == ItemType.BLUEPRINT_CORRAL);

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            int bpIndex = findBlueprintSlot(player);
            if (bpIndex >= 0) {
                if (player.getInventory().getSelectedIndex() == bpIndex) {
                    player.getInventory().select(7);
                    active = false;
                } else {
                    player.getInventory().select(bpIndex);
                    active = true;
                }
            } else {
                active = !active;
            }
            resetPillars();
        } else {
            active = holdingBlueprint;
        }

        if (!active) {
            resetPillars();
            hoveredCorral = null;
            return;
        }

        // Converte coordenadas do mouse para tiles do chão isométrico
        tmpMouse.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        viewport.unproject(tmpMouse);
        IsoUtils.screenToWorld(tmpMouse.x, tmpMouse.y, tmpWorld);

        int tx = Math.round(tmpWorld.x);
        int ty = Math.round(tmpWorld.y);
        currentHover.set(tx, ty);

        // Atualiza cercado sob a mira do mouse para possível demolição
        hoveredCorral = findCorralAt(tmpWorld.x, tmpWorld.y, corralSystem);

        // Tecla de desfazer pilar anterior
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            undoPillar();
        }
    }

    /**
     * Trata o clique esquerdo para posicionar sequencialmente os 4 pilares.
     */
    public boolean handleLeftClick(World world, CorralSystem corralSystem, WorldRenderer worldRenderer,
            CorralRenderer corralRenderer, ParticleSystem particles, SoundManager sounds) {
        if (!active) {
            return false;
        }

        if (step == 0) {
            // Passo 1: Posiciona o Pilar 1
            p1.set(currentHover.x, currentHover.y);
            step = 1;
            sounds.play("pickup");
            lastStatusMessage = "Pilar 1/4 fixado! Agora clique para posicionar o Pilar 2 (largura).";
            return true;
        } else if (step == 1) {
            // Passo 2: Posiciona o Pilar 2 (alinhado na horizontal com p1)
            int dist = clampDist(currentHover.x - p1.x);
            p2.set(p1.x + dist, p1.y);
            step = 2;
            sounds.play("pickup");
            lastStatusMessage = "Pilar 2/4 fixado! Agora clique para posicionar o Pilar 3 (profundidade).";
            return true;
        } else if (step == 2) {
            // Passo 3: Posiciona o Pilar 3 (alinhado na vertical com p2)
            int dist = clampDist(currentHover.y - p2.y);
            p3.set(p2.x, p2.y + dist);
            step = 3;
            sounds.play("pickup");
            lastStatusMessage = "Pilar 3/4 fixado! Clique para posicionar o Pilar 4 e fechar o cercado!";
            return true;
        } else if (step == 3) {
            // Passo 4: Posiciona o Pilar 4 (fecha o retângulo conectando de volta a p1)
            p4.set(p1.x, p3.y);

            int minX = Math.min(p1.x, p2.x);
            int maxX = Math.max(p1.x, p2.x);
            int minY = Math.min(p1.y, p3.y);
            int maxY = Math.max(p1.y, p3.y);
            int w = Math.max(MIN_SIZE, maxX - minX);
            int h = Math.max(MIN_SIZE, maxY - minY);

            if (!isValidPlacement(world, minX, minY, w, h, corralSystem)) {
                sounds.play("bonk");
                lastStatusMessage = "Area invalida para os 4 pilares! Escolha terreno plano e desobstruido.";
                return true;
            }

            // Cria e fecha o novo cercado
            Corral newCorral = new Corral(minX, minY, w, h);
            world.add(newCorral);
            corralSystem.addCorral(newCorral);
            worldRenderer.addCorral(newCorral);

            spawnBuildParticles(particles, minX, minY, w, h);
            sounds.play("upgrade");
            lastStatusMessage = "Cercado fechado com sucesso com os 4 Pilares alinhados!";

            resetPillars();
            return true;
        }
        return false;
    }

    /**
     * Trata o clique direito:
     * - Se nenhum pilar foi colocado e está apontando para um cercado:
     * desmonta/remove o cercado.
     * - Se algum pilar já foi colocado: desfaz o último pilar posicionado.
     */
    public boolean handleRightClick(World world, CorralSystem corralSystem, WorldRenderer worldRenderer,
            ParticleSystem particles, SoundManager sounds) {
        if (!active) {
            return false;
        }

        if (step > 0) {
            undoPillar();
            sounds.play("pickup");
            return true;
        }

        // Se step == 0 e está sobre um cercado: desmonta o cercado
        if (hoveredCorral != null) {
            return demolishCorral(hoveredCorral, world, corralSystem, worldRenderer, particles, sounds);
        }

        return false;
    }

    /**
     * Ação da tecla [X]: desmonta o cercado sob a mira do mouse ou mais próximo do
     * jogador.
     */
    public boolean handleDemolishKey(World world, CorralSystem corralSystem, WorldRenderer worldRenderer,
            Player player, ParticleSystem particles, SoundManager sounds) {
        if (!active) {
            return false;
        }
        Corral target = hoveredCorral;
        if (target == null && corralSystem != null) {
            // Se o mouse não está diretamente em cima, busca cercado a até 3 tiles do
            // player
            for (Corral c : corralSystem.getCorrals()) {
                if (c.getBounds().contains(player.getPos().x, player.getPos().y)
                        || player.getPos().dst(c.getBounds().x + c.getBounds().width / 2f,
                                c.getBounds().y + c.getBounds().height / 2f) <= 4.5f) {
                    target = c;
                    break;
                }
            }
        }
        if (target != null) {
            return demolishCorral(target, world, corralSystem, worldRenderer, particles, sounds);
        }
        return false;
    }

    /**
     * Desmonta e remove completamente um cercado, liberando os slimes de dentro.
     */
    public boolean demolishCorral(Corral corral, World world, CorralSystem corralSystem,
            WorldRenderer worldRenderer, ParticleSystem particles, SoundManager sounds) {
        if (corral == null) {
            return false;
        }

        Rectangle b = corral.getBounds();
        // Libera qualquer slime que estivesse contido dentro deste cercado
        for (Entity e : world.getEntities()) {
            if (e instanceof Slime) {
                Slime s = (Slime) e;
                if (s.isCorralled() && b.contains(s.getPos())) {
                    s.setCorralled(false);
                }
            }
        }

        // Partículas de desmontagem nos 4 cantos
        spawnDemolishParticles(particles, corral);
        if (sounds != null) {
            sounds.play("bonk");
        }

        // Remove do mundo e dos renderizadores
        corral.remove();
        if (corralSystem != null) {
            corralSystem.removeCorral(corral);
        }
        if (worldRenderer != null) {
            worldRenderer.removeCorral(corral);
        }

        lastStatusMessage = "Cercado desmontado e removido com sucesso!";
        resetPillars();
        return true;
    }

    private void undoPillar() {
        if (step > 0) {
            step--;
            if (step == 0) {
                p1.set(-1, -1);
                lastStatusMessage = "Pilar 1 cancelado. Clique para escolher novo local.";
            } else if (step == 1) {
                p2.set(-1, -1);
                lastStatusMessage = "Pilar 2 desfeito. Reposicione o Pilar 2.";
            } else if (step == 2) {
                p3.set(-1, -1);
                lastStatusMessage = "Pilar 3 desfeito. Reposicione o Pilar 3.";
            }
        }
    }

    private static int clampDist(int raw) {
        if (raw == 0) {
            return MIN_SIZE;
        }
        int sign = raw < 0 ? -1 : 1;
        int abs = Math.abs(raw);
        abs = Math.max(MIN_SIZE, Math.min(MAX_SIZE, abs));
        return abs * sign;
    }

    /**
     * Desenha a pré-visualização holográfica dinâmica dos pilares e vidros.
     */
    public void renderHologram(SpriteBatch batch, World world, CorralRenderer corralRenderer,
            CorralSystem corralSystem) {
        if (!active || corralRenderer == null) {
            return;
        }

        float alpha = 0.65f + 0.20f * (float) Math.sin(pulseTime);
        Color cyanHolo = new Color(0.35f, 0.85f, 1f, alpha);
        Color redHolo = new Color(1f, 0.25f, 0.30f, alpha);
        Color greenHolo = new Color(0.35f, 1f, 0.45f, alpha);

        // Se está com o mouse sobre um cercado existente no modo de demolição
        if (step == 0 && hoveredCorral != null) {
            corralRenderer.renderDemolishHighlight(batch, hoveredCorral, pulseTime);
            return;
        }

        if (step == 0) {
            // Pilar 1 sob o cursor
            boolean ok = isPointValid(world, currentHover.x, currentHover.y);
            corralRenderer.drawHoloPost(batch, currentHover.x, currentHover.y, ok ? greenHolo : redHolo);
        } else if (step == 1) {
            // Pilar 1 fixo
            corralRenderer.drawHoloPost(batch, p1.x, p1.y, greenHolo);

            // Pilar 2 sob o cursor (alinhado no eixo X)
            int dist = clampDist(currentHover.x - p1.x);
            int p2x = p1.x + dist;
            int p2y = p1.y;
            corralRenderer.drawHoloPost(batch, p2x, p2y, cyanHolo);
            corralRenderer.drawHoloWall(batch, p1.x, p1.y, p2x, p2y, cyanHolo);
        } else if (step == 2) {
            // Pilares 1 e 2 fixos + parede entre eles
            corralRenderer.drawHoloPost(batch, p1.x, p1.y, greenHolo);
            corralRenderer.drawHoloPost(batch, p2.x, p2.y, greenHolo);
            corralRenderer.drawHoloWall(batch, p1.x, p1.y, p2.x, p2.y, greenHolo);

            // Pilar 3 sob o cursor (alinhado no eixo Y a partir de p2)
            int dist = clampDist(currentHover.y - p2.y);
            int p3x = p2.x;
            int p3y = p2.y + dist;
            corralRenderer.drawHoloPost(batch, p3x, p3y, cyanHolo);
            corralRenderer.drawHoloWall(batch, p2.x, p2.y, p3x, p3y, cyanHolo);
        } else if (step == 3) {
            // Pilares 1, 2 e 3 fixos
            corralRenderer.drawHoloPost(batch, p1.x, p1.y, greenHolo);
            corralRenderer.drawHoloPost(batch, p2.x, p2.y, greenHolo);
            corralRenderer.drawHoloPost(batch, p3.x, p3.y, greenHolo);
            corralRenderer.drawHoloWall(batch, p1.x, p1.y, p2.x, p2.y, greenHolo);
            corralRenderer.drawHoloWall(batch, p2.x, p2.y, p3.x, p3.y, greenHolo);

            // Pilar 4 fechando o cercado
            int p4x = p1.x;
            int p4y = p3.y;
            int minX = Math.min(p1.x, p2.x);
            int maxX = Math.max(p1.x, p2.x);
            int minY = Math.min(p1.y, p3.y);
            int maxY = Math.max(p1.y, p3.y);
            int w = Math.max(MIN_SIZE, maxX - minX);
            int h = Math.max(MIN_SIZE, maxY - minY);
            boolean valid = isValidPlacement(world, minX, minY, w, h, corralSystem);

            Color finalCol = valid ? cyanHolo : redHolo;
            corralRenderer.drawHoloPost(batch, p4x, p4y, finalCol);
            corralRenderer.drawHoloWall(batch, p3.x, p3.y, p4x, p4y, finalCol);
            corralRenderer.drawHoloWall(batch, p4x, p4y, p1.x, p1.y, finalCol);
        }
    }

    private boolean isPointValid(World world, int x, int y) {
        TileMap map = world.getTileMap();
        if (x < 4 || y < 4 || x >= map.getWidth() - 4 || y >= map.getHeight() - 4) {
            return false;
        }
        Tile t = map.get(x, y);
        return t != null && t.getElevation() == 0 && t.isWalkable();
    }

    public boolean isValidPlacement(World world, int x0, int y0, int w, int h, CorralSystem corralSystem) {
        TileMap map = world.getTileMap();
        if (x0 < 4 || y0 < 4 || x0 + w >= map.getWidth() - 4 || y0 + h >= map.getHeight() - 4) {
            return false;
        }
        for (int x = x0; x <= x0 + w; x++) {
            for (int y = y0; y <= y0 + h; y++) {
                Tile t = map.get(x, y);
                if (t == null || t.getElevation() > 0 || !t.isWalkable()) {
                    return false;
                }
            }
        }
        Rectangle candidate = new Rectangle(x0, y0, w, h);
        if (corralSystem != null) {
            for (Corral existing : corralSystem.getCorrals()) {
                if (existing.getBounds().overlaps(candidate)) {
                    return false;
                }
            }
        }
        for (Entity e : world.getEntities()) {
            if (e.isSolid() && candidate.contains(e.getPos().x, e.getPos().y)) {
                return false;
            }
        }
        return true;
    }

    public Corral findCorralAt(float worldX, float worldY, CorralSystem corralSystem) {
        if (corralSystem == null) {
            return null;
        }
        for (Corral c : corralSystem.getCorrals()) {
            if (c.getBounds().contains(worldX, worldY)) {
                return c;
            }
        }
        return null;
    }

    private void spawnBuildParticles(ParticleSystem particles, int x0, int y0, int w, int h) {
        if (particles == null)
            return;
        float[][] corners = { { x0, y0 }, { x0 + w, y0 }, { x0, y0 + h }, { x0 + w, y0 + h } };
        for (float[] pt : corners) {
            float sx = IsoUtils.toScreenX(pt[0], pt[1]);
            float sy = IsoUtils.toScreenY(pt[0], pt[1], 0f);
            for (int i = 0; i < 14; i++) {
                particles.spawn(sx + RandomUtils.range(-8f, 8f), sy + RandomUtils.range(-4f, 22f),
                        RandomUtils.range(-35f, 35f), RandomUtils.range(25f, 70f),
                        RandomUtils.range(3.5f, 5.5f), 0.55f, Constants.ENERGY);
            }
        }
    }

    private void spawnDemolishParticles(ParticleSystem particles, Corral corral) {
        if (particles == null || corral == null) return;
        Rectangle b = corral.getBounds();
        for (int i = 0; i < 28; i++) {
            float wx = RandomUtils.range(b.x, b.x + b.width);
            float wy = RandomUtils.range(b.y, b.y + b.height);
            float sx = IsoUtils.toScreenX(wx, wy);
            float sy = IsoUtils.toScreenY(wx, wy, 0f);
            particles.spawn(sx, sy, RandomUtils.range(-40f, 40f), RandomUtils.range(15f, 50f),
                    RandomUtils.range(4f, 6f), 0.6f, Color.valueOf("FF5555"));
        }
    }

    private int findBlueprintSlot(Player player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            InventorySlot s = player.getInventory().getSlot(i);
            if (!s.isEmpty() && s.getType() == ItemType.BLUEPRINT_CORRAL) {
                return i;
            }
        }
        return -1;
    }

    public void resetPillars() {
        step = 0;
        p1.set(-1, -1);
        p2.set(-1, -1);
        p3.set(-1, -1);
        p4.set(-1, -1);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            resetPillars();
        }
    }

    public int getStep() {
        return step;
    }

    public Corral getHoveredCorral() {
        return hoveredCorral;
    }

    public String getLastStatusMessage() {
        return lastStatusMessage;
    }
}
