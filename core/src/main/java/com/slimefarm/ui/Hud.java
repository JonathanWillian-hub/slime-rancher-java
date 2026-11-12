package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slimefarm.assets.Assets;
import com.slimefarm.economy.Market;
import com.slimefarm.economy.UpgradeShop;
import com.slimefarm.economy.Wallet;
import com.slimefarm.entities.Player;
import com.slimefarm.progression.Slimepedia;
import com.slimefarm.systems.BuildingSystem;
import com.slimefarm.util.Constants;
import com.slimefarm.world.BiomeType;

/**
 * Cabeçalho de interface (HUD) desenhado com câmera ortográfica fixa de tela:
 * <ul>
 * <li>Canto superior esquerdo: barra de vida vermelha, barra de energia azul e
 * contador de moedas;</li>
 * <li>Topo central: indicador colorido do {@link BiomeType} onde o jogador está
 * explorando;</li>
 * <li>Canto superior direito: botão clicável da {@link SlimepediaWindow}
 * ("Pokédex de Slimes");</li>
 * <li>Base da tela: {@link Hotbar} de 8 slots;</li>
 * <li>Janelas modais: {@link MarketWindow}, {@link ShopWindow},
 * {@link SlimepediaWindow} e {@link PauseMenu}.</li>
 * </ul>
 */
public class Hud implements Disposable {

    private final Assets assets;
    private final OrthographicCamera uiCamera = new OrthographicCamera();
    private final ExtendViewport uiViewport = new ExtendViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, uiCamera);
    private final ShapeRenderer shapes = new ShapeRenderer();

    private final StatBar healthBar = new StatBar(Constants.HEALTH, "ui_heart");
    private final StatBar energyBar = new StatBar(Constants.ENERGY, "ui_bolt");
    private final CoinCounter coinCounter = new CoinCounter();
    private final Hotbar hotbar = new Hotbar();

    private final MarketWindow marketWindow = new MarketWindow();
    private final ShopWindow shopWindow = new ShopWindow();
    private final SlimepediaWindow slimepediaWindow = new SlimepediaWindow();
    private final PauseMenu pauseMenu = new PauseMenu();

    private final Rectangle slimepediaButtonBounds = new Rectangle();
    private final Vector3 tmpClick = new Vector3();

    /** @param assets repositório de texturas e fonte */
    public Hud(Assets assets) {
        this.assets = assets;
    }

    /**
     * Verifica se um clique de mouse na tela acertou o botão da Slimepedia no canto
     * superior direito.
     *
     * @param screenX x do mouse na janela
     * @param screenY y do mouse na janela
     * @return true se o botão foi clicado e alternou a Slimepedia
     */
    public boolean handleMouseClick(int screenX, int screenY) {
        tmpClick.set(screenX, screenY, 0f);
        uiViewport.unproject(tmpClick);
        if (slimepediaButtonBounds.contains(tmpClick.x, tmpClick.y)) {
            slimepediaWindow.toggle();
            return true;
        }
        return false;
    }

    /**
     * Desenha toda a camada de interface sobre o mundo.
     *
     * @param batch          batch compartilhado (não deve estar em begin())
     * @param player         jogador
     * @param wallet         carteira de moedas
     * @param market         mercado de cristais
     * @param shop           loja de upgrades
     * @param slimepedia     registro da Slimepedia
     * @param currentBiome   bioma atual onde o jogador está
     * @param nearMarketHint true se o jogador está perto do terminal do mercado
     * @param nearShopHint   true se o jogador está perto do terminal da loja
     * @param dt             tempo do quadro (s)
     */
    public void render(SpriteBatch batch, Player player, Wallet wallet, Market market,
                       UpgradeShop shop, Slimepedia slimepedia, BiomeType currentBiome,
                       boolean nearMarketHint, boolean nearShopHint, BuildingSystem buildingSystem, float dt) {
        uiViewport.apply();
        float w = uiViewport.getWorldWidth();
        float h = uiViewport.getWorldHeight();
        uiCamera.position.set(w / 2f, h / 2f, 0f);
        uiCamera.update();

        shapes.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);

        float barX = 28f;
        float topY = h - 32f;

        // Botão da Slimepedia no canto superior direito
        float btnW = 190f;
        float btnH = 30f;
        float btnX = w - btnW - 16f;
        float btnY = h - btnH - 14f;
        slimepediaButtonBounds.set(btnX, btnY, btnW, btnH);

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        healthBar.renderShapes(shapes, barX, topY, 152f, 18f, player.getStats().getHealthRatio());
        energyBar.renderShapes(shapes, barX, topY - 24f, 152f, 18f, player.getStats().getEnergyRatio());

        // Placa do Bioma Atual (topo central)
        shapes.setColor(0.09f, 0.14f, 0.22f, 0.88f);
        shapes.rect(w / 2f - 115f, h - 38f, 230f, 26f);

        // Botão clicável da Slimepedia
        shapes.setColor(0.12f, 0.18f, 0.28f, 0.95f);
        shapes.rect(btnX - 2f, btnY - 2f, btnW + 4f, btnH + 4f);
        shapes.setColor(slimepediaWindow.isOpen() ? Color.valueOf("F0507A") : Color.valueOf("2E4A6E"));
        shapes.rect(btnX, btnY, btnW, btnH);
        shapes.end();

        batch.begin();
        healthBar.renderIcon(batch, assets.get(healthBar.getIconName()), barX, topY, 18f);
        energyBar.renderIcon(batch, assets.get(energyBar.getIconName()), barX, topY - 24f, 18f);
        coinCounter.render(batch, assets.getFont(), assets.get("ui_coin"), wallet, barX - 8f, topY - 36f);

        // Texto do Bioma Atual
        assets.getFont().setColor(currentBiome.themeColor);
        assets.getFont().draw(batch, "Bioma: " + currentBiome.displayName, w / 2f - 98f, h - 20f);

        // Texto e ícone do botão da Slimepedia
        batch.draw(assets.get("slime_pink_happy"), btnX + 6f, btnY + 3f, 24f, 23f);
        assets.getFont().setColor(Color.valueOf("FFE580"));
        assets.getFont().draw(batch, "[P] Slimepedia (" + slimepedia.getCapturedCount()
                + "/" + slimepedia.getTotalSpecies() + ")", btnX + 35f, btnY + 20f);

        if (!anyModalOpen() && (buildingSystem == null || !buildingSystem.isActive())) {
            if (nearMarketHint) {
                assets.getFont().setColor(Color.valueOf("FFE580"));
                assets.getFont().draw(batch, "Pressione [E] para abrir o Mercado de Cristais",
                        w / 2f - 145f, 92f);
            } else if (nearShopHint) {
                assets.getFont().setColor(Color.valueOf("7DFF7A"));
                assets.getFont().draw(batch, "Pressione [E] ou [U] para abrir a Loja de Upgrades",
                        w / 2f - 155f, 92f);
            }
        }
        batch.end();

        // Banner informativo do Modo Construção (estilo Fortnite)
        if (buildingSystem != null && buildingSystem.isActive()) {
            boolean isDemolishTarget = (buildingSystem.getStep() == 0 && buildingSystem.getHoveredCorral() != null);

            shapes.begin(ShapeRenderer.ShapeType.Filled);
            shapes.setColor(0.08f, 0.20f, 0.38f, 0.90f);
            shapes.rect(w / 2f - 290f, 70f, 580f, 40f);
            if (isDemolishTarget) {
                shapes.setColor(0.95f, 0.25f, 0.25f, 1f);
            } else {
                shapes.setColor(0.22f, 0.60f, 0.95f, 1f);
            }
            shapes.rect(w / 2f - 288f, 72f, 576f, 36f);
            shapes.setColor(0.06f, 0.12f, 0.24f, 0.95f);
            shapes.rect(w / 2f - 286f, 74f, 572f, 32f);
            shapes.end();

            batch.begin();
            if (isDemolishTarget) {
                assets.getFont().setColor(Color.valueOf("FF6B6B"));
                assets.getFont().draw(batch, "MIRA NO CERCADO: Pressione [X] ou [Botao Direito] para DEMOLIR/TIRAR!",
                        w / 2f - 265f, 95f);
            } else {
                int step = buildingSystem.getStep();
                if (step == 0) {
                    assets.getFont().setColor(Color.valueOf("FFE580"));
                    assets.getFont().draw(batch, "Passo 1/4: Clique no chao para posicionar o Pilar 1 ([B] Sair)",
                            w / 2f - 240f, 95f);
                } else if (step == 1) {
                    assets.getFont().setColor(Color.valueOf("72F2FF"));
                    assets.getFont().draw(batch, "Passo 2/4: Clique para posicionar o Pilar 2 (largura) | [Dir.] Desfaz",
                            w / 2f - 260f, 95f);
                } else if (step == 2) {
                    assets.getFont().setColor(Color.valueOf("72F2FF"));
                    assets.getFont().draw(batch, "Passo 3/4: Clique para posicionar o Pilar 3 (profundidade) | [Dir.] Desfaz",
                            w / 2f - 275f, 95f);
                } else if (step == 3) {
                    assets.getFont().setColor(Color.valueOf("7DFF7A"));
                    assets.getFont().draw(batch, "Passo 4/4: Clique para fechar o Pilar 4 e SELAR o cercado! | [Dir.] Desfaz",
                            w / 2f - 275f, 95f);
                }
            }
            batch.end();
        }

        // Hotbar de 8 slots na base
        hotbar.render(shapes, batch, assets, player.getInventory(), w);

        // Janelas modais (Mercado, Loja de Upgrades, Slimepedia e Pausa)
        marketWindow.render(shapes, batch, assets, market, player.getInventory(), w, h);
        shopWindow.render(shapes, batch, assets, shop, wallet, w, h);
        slimepediaWindow.render(shapes, batch, assets, slimepedia, w, h);
        pauseMenu.render(shapes, batch, assets, w, h, dt);
        assets.getFont().setColor(Color.WHITE);
    }

    /** @return true se alguma janela sobreposta está aberta */
    public boolean anyModalOpen() {
        return marketWindow.isOpen() || shopWindow.isOpen() || slimepediaWindow.isOpen() || pauseMenu.isPaused();
    }

    /** Fecha qualquer janela sobreposta (Mercado, Loja ou Slimepedia). */
    public boolean closeTopModal() {
        if (marketWindow.isOpen()) {
            marketWindow.setOpen(false);
            return true;
        }
        if (shopWindow.isOpen()) {
            shopWindow.setOpen(false);
            return true;
        }
        if (slimepediaWindow.isOpen()) {
            slimepediaWindow.setOpen(false);
            return true;
        }
        return false;
    }

    /** Atualiza o tamanho da viewport da UI ao redimensionar a janela. */
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    /** @return janela do mercado */
    public MarketWindow getMarketWindow() {
        return marketWindow;
    }

    /** @return janela da loja de upgrades */
    public ShopWindow getShopWindow() {
        return shopWindow;
    }

    /** @return janela da Slimepedia */
    public SlimepediaWindow getSlimepediaWindow() {
        return slimepediaWindow;
    }

    /** @return menu de pausa */
    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}
