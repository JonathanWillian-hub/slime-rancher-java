package com.slimefarm.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.assets.Assets;
import com.slimefarm.entities.Entity;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.world.Tile;
import com.slimefarm.world.TileMap;
import com.slimefarm.world.World;

/**
 * Desenha o mundo em ordem isométrica:
 * <ol>
 *   <li>tiles do chão (de trás para a frente) + faces dos penhascos;</li>
 *   <li>sombras;</li>
 *   <li>entidades ordenadas por {@link DepthSorter}.</li>
 * </ol>
 */
public class WorldRenderer {

    private final Assets assets;
    private final DepthSorter sorter = new DepthSorter();
    private ShadowRenderer shadowRenderer;
    private CorralRenderer corralRenderer;
    private final Array<Corral> corrals = new Array<>();

    /** @param assets texturas já carregadas */
    public WorldRenderer(Assets assets) {
        this.assets = assets;
    }

    /** @param shadowRenderer desenhador de sombras (opcional) */
    public void setShadowRenderer(ShadowRenderer shadowRenderer) {
        this.shadowRenderer = shadowRenderer;
    }

    /** @param corralRenderer desenhador do curral (opcional) */
    public void setCorralRenderer(CorralRenderer corralRenderer, Corral initialCorral) {
        this.corralRenderer = corralRenderer;
        if (initialCorral != null && !this.corrals.contains(initialCorral, true)) {
            this.corrals.add(initialCorral);
        }
    }

    /** Adiciona um novo cercado para ser desenhado no mundo. */
    public void addCorral(Corral c) {
        if (c != null && !corrals.contains(c, true)) {
            corrals.add(c);
        }
    }

    /** Remove um cercado da lista de desenho. */
    public void removeCorral(Corral c) {
        if (c != null) {
            corrals.removeValue(c, true);
        }
    }

    /**
     * Desenha chão e entidades visíveis pela câmera.
     *
     * @param batch  batch já iniciado
     * @param world  mundo
     * @param camera câmera atual
     */
    public void render(SpriteBatch batch, World world, OrthographicCamera camera) {
        renderTiles(batch, world.getTileMap(), camera);
        Array<Entity> sorted = sorter.sort(world.getEntities());
        if (shadowRenderer != null) {
            shadowRenderer.render(batch, sorted);
        }
        if (corralRenderer != null) {
            for (int i = 0; i < corrals.size; i++) {
                corralRenderer.renderBack(batch, corrals.get(i));
            }
        }
        for (Entity e : sorted) {
            e.render(batch, assets);
        }
        if (corralRenderer != null) {
            for (int i = 0; i < corrals.size; i++) {
                corralRenderer.renderFront(batch, corrals.get(i));
            }
        }
    }

    /** Desenha todos os tiles visíveis na câmera, de trás (x+y menor) para a frente. */
    private void renderTiles(SpriteBatch batch, TileMap map, OrthographicCamera camera) {
        float halfW = camera.viewportWidth * camera.zoom / 2f + Constants.TILE_W * 2;
        float halfH = camera.viewportHeight * camera.zoom / 2f + Constants.TILE_H * 3;
        float minX = camera.position.x - halfW;
        float maxX = camera.position.x + halfW;
        float minY = camera.position.y - halfH;
        float maxY = camera.position.y + halfH;

        TextureRegion cliff = assets.get("cliff_side");
        int w = map.getWidth();
        int h = map.getHeight();
        // percorre por soma (x + y) crescente = ordem de profundidade exata
        for (int sum = 0; sum <= w + h - 2; sum++) {
            int xStart = Math.max(0, sum - (h - 1));
            int xEnd = Math.min(w - 1, sum);
            for (int x = xStart; x <= xEnd; x++) {
                int y = sum - x;
                Tile t = map.get(x, y);
                float cx = IsoUtils.toScreenX(x + 0.5f, y + 0.5f);
                float cy = IsoUtils.toScreenY(x + 0.5f, y + 0.5f, t.getElevation());
                if (cx < minX || cx > maxX || cy < minY || cy > maxY) {
                    continue;
                }
                if (t.getElevation() > 0) {
                    batch.draw(cliff, cx - Constants.HALF_W, cy - Constants.HALF_H - Constants.Z_PIXELS);
                }
                TextureRegion reg = assets.get(t.getTextureName());
                batch.draw(reg, cx - Constants.HALF_W, cy - Constants.HALF_H);
            }
        }
    }
}
