package com.slimefarm.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slimefarm.SlimeFarmGame;
import com.slimefarm.economy.Market;
import com.slimefarm.economy.UpgradeShop;
import com.slimefarm.economy.UpgradeType;
import com.slimefarm.economy.Wallet;
import com.slimefarm.entities.Player;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.input.GameInputProcessor;
import com.slimefarm.input.InputState;
import com.slimefarm.inventory.Inventory;
import com.slimefarm.player.PlayerController;
import com.slimefarm.player.VacuumGun;
import com.slimefarm.progression.Slimepedia;
import com.slimefarm.render.AmbientEffects;
import com.slimefarm.render.CorralRenderer;
import com.slimefarm.render.ParticleSystem;
import com.slimefarm.render.ShadowRenderer;
import com.slimefarm.render.VacuumBeamRenderer;
import com.slimefarm.render.WorldRenderer;
import com.slimefarm.save.SaveManager;
import com.slimefarm.systems.CorralSystem;
import com.slimefarm.systems.CrystalProductionSystem;
import com.slimefarm.systems.FoodSpawner;
import com.slimefarm.systems.SpawnSystem;
import com.slimefarm.systems.BuildingSystem;
import com.slimefarm.ui.Hud;
import com.slimefarm.ui.MarketWindow;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.world.BiomeType;
import com.slimefarm.world.MapBuilder;
import com.slimefarm.world.World;

/**
 * Tela principal de jogo. Coordena o loop completo:
 * entrada -> jogador/aspirador -> sistemas do mundo (IA, curral seletivo,
 * cristais, spawns por visão,
 * mercado e loja de upgrades) -> renderização isométrica -> feixe/partículas ->
 * HUD/UI/Slimepedia.
 */
public class GameScreen extends ScreenAdapter {

    /** Passo máximo de tempo para evitar "saltos" quando o jogo trava. */
    private static final float MAX_DELTA = 1f / 20f;

    private final SlimeFarmGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final ExtendViewport viewport = new ExtendViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
    private final SpriteBatch batch = new SpriteBatch();

    private final InputState input = new InputState();
    private final Wallet wallet = new Wallet();
    private final Market market = new Market(wallet);
    private final UpgradeShop upgradeShop = new UpgradeShop(wallet);
    private final Slimepedia slimepedia = new Slimepedia();

    private final World world;
    private final Corral corral;
    private final Player player;
    private final PlayerController playerController;
    private final VacuumGun vacuumGun;

    private final CorralSystem corralSystem;
    private final CrystalProductionSystem crystalSystem = new CrystalProductionSystem();
    private final FoodSpawner foodSpawner = new FoodSpawner();
    private final SpawnSystem spawnSystem = new SpawnSystem();

    private final WorldRenderer worldRenderer;
    private final CorralRenderer corralRenderer;
    private final BuildingSystem buildingSystem = new BuildingSystem();
    private final ParticleSystem particles;
    private final VacuumBeamRenderer beamRenderer = new VacuumBeamRenderer();
    private final AmbientEffects ambientEffects = new AmbientEffects();
    private final Hud hud;
    private final SaveManager saveManager = new SaveManager();

    /**
     * @param game     jogo principal
     * @param loadSave true para carregar o progresso salvo em disco
     */
    public GameScreen(SlimeFarmGame game, boolean loadSave) {
        this.game = game;
        this.world = MapBuilder.build();
        this.corral = MapBuilder.populateInitialScene(world, market, spawnSystem);
        this.player = world.addNow(new Player(MapBuilder.PLAYER_START_X, MapBuilder.PLAYER_START_Y));
        this.playerController = new PlayerController(player, input);
        this.vacuumGun = new VacuumGun(player, input, slimepedia);
        this.corralSystem = new CorralSystem(corral);

        this.corralRenderer = new CorralRenderer(game.getAssets());
        this.worldRenderer = new WorldRenderer(game.getAssets());
        this.worldRenderer.setShadowRenderer(new ShadowRenderer(game.getAssets()));
        this.worldRenderer.setCorralRenderer(corralRenderer, corral);
        this.particles = new ParticleSystem(game.getAssets());
        this.hud = new Hud(game.getAssets());

        populateReferenceHotbar(player.getInventory());
        // Slime Rosa já começa registrado como exemplo; os outros 5 aparecem em
        // silhueta preta até capturar!
        slimepedia.registerCapture(SlimeType.PINK);
        foodSpawner.spawnInitial(world, 12);

        if (loadSave) {
            saveManager.load(wallet, market, upgradeShop, slimepedia, player, world, corral);
        }

        player.setAimWorld(player.getPos().x + 2.5f, player.getPos().y - 1.5f);
        float startX = IsoUtils.toScreenX(player.getPos().x, player.getPos().y);
        float startY = IsoUtils.toScreenY(player.getPos().x, player.getPos().y, player.getGroundZ());
        camera.position.set(startX, startY, 0f);
    }

    private static void populateReferenceHotbar(Inventory inv) {
        inv.getSlot(0).set(ItemType.SLIME_PINK, 1);
        inv.getSlot(1).set(ItemType.SLIME_CAT, 3);
        inv.getSlot(2).set(ItemType.SLIME_ROCK, 1);
        inv.getSlot(3).set(ItemType.STRAWBERRY, 3);
        inv.getSlot(4).set(ItemType.CRYSTAL_ROCK, 2);
        inv.getSlot(5).set(ItemType.CRYSTAL_CAT, 3);
        inv.getSlot(6).set(ItemType.BLUEPRINT_CORRAL, 1);
        inv.select(7);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputProcessor(input));
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, MAX_DELTA);
        handleWindowShortcuts();
        if (game.getScreen() != this) {
            return;
        }
        if (!hud.getPauseMenu().isPaused()) {
            update(dt);
        }
        draw(dt);
    }

    /**
     * Trata cliques nos botões da UI, Slimepedia (P/Tab), Loja (U/E), Mercado (E),
     * Salvar (F5) e Pausa (Esc).
     */
    private void handleWindowShortcuts() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (hud.handleMouseClick(Gdx.input.getX(), Gdx.input.getY())) {
                input.vacuumHeld = false;
            } else if (buildingSystem.isActive() && !hud.anyModalOpen()) {
                if (buildingSystem.handleLeftClick(world, corralSystem, worldRenderer, corralRenderer, particles,
                        game.getSounds())) {
                    input.vacuumHeld = false;
                    if (buildingSystem.getLastStatusMessage() != null) {
                        hud.getPauseMenu().showMessage(buildingSystem.getLastStatusMessage());
                    }
                }
            }
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && buildingSystem.isActive() && !hud.anyModalOpen()) {
            if (buildingSystem.handleRightClick(world, corralSystem, worldRenderer, particles, game.getSounds())) {
                if (buildingSystem.getLastStatusMessage() != null) {
                    hud.getPauseMenu().showMessage(buildingSystem.getLastStatusMessage());
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && buildingSystem.isActive() && !hud.anyModalOpen()) {
            if (buildingSystem.handleDemolishKey(world, corralSystem, worldRenderer, player, particles,
                    game.getSounds())) {
                if (buildingSystem.getLastStatusMessage() != null) {
                    hud.getPauseMenu().showMessage(buildingSystem.getLastStatusMessage());
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            hud.getMarketWindow().setOpen(false);
            hud.getShopWindow().setOpen(false);
            hud.getSlimepediaWindow().toggle();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            hud.getMarketWindow().setOpen(false);
            hud.getSlimepediaWindow().setOpen(false);
            hud.getShopWindow().toggle();
        }
        if (input.consumePause()) {
            if (!hud.closeTopModal()) {
                hud.getPauseMenu().toggle();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            saveManager.save(wallet, market, upgradeShop, slimepedia, player.getInventory(), world, corral);
            hud.getPauseMenu().showMessage("Progresso salvo!");
        }
        if (hud.getPauseMenu().isPaused() && Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            saveManager.save(wallet, market, upgradeShop, slimepedia, player.getInventory(), world, corral);
            game.showMenu();
            return;
        }
        if (input.consumeInteract()) {
            if (hud.getMarketWindow().isOpen()) {
                int earned = market.sellAllFromInventory(player.getInventory());
                if (earned > 0) {
                    game.getSounds().play("sell");
                    hud.getPauseMenu().showMessage("Vendido por $" + earned + "!");
                }
            } else if (isNearMarket()) {
                hud.getShopWindow().setOpen(false);
                hud.getSlimepediaWindow().setOpen(false);
                hud.getMarketWindow().setOpen(true);
            } else if (isNearShop()) {
                hud.getMarketWindow().setOpen(false);
                hud.getSlimepediaWindow().setOpen(false);
                hud.getShopWindow().setOpen(true);
            }
        }

        // Atalhos numéricos dentro do Mercado de Cristais (1..6)
        if (hud.getMarketWindow().isOpen()) {
            ItemType[] crystals = MarketWindow.CRYSTALS;
            for (int i = 0; i < crystals.length; i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                    market.sellFromInventory(player.getInventory(), crystals[i]);
                }
            }
        }

        // Atalhos numéricos dentro da Loja de Upgrades (1..5)
        if (hud.getShopWindow().isOpen()) {
            UpgradeType[] upgrades = UpgradeType.values();
            for (int i = 0; i < upgrades.length; i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                    if (upgradeShop.buy(upgrades[i], player)) {
                        hud.getPauseMenu().showMessage("Upgrade comprado: " + upgrades[i].displayName + "!");
                    }
                }
            }
        }
    }

    /**
     * Atualiza a lógica do mundo, sistemas e câmera.
     *
     * @param dt tempo desde o último quadro (s)
     */
    private void update(float dt) {
        playerController.update(dt, viewport);
        buildingSystem.update(player, camera, viewport, corralSystem, dt);
        if (!hud.anyModalOpen() && !buildingSystem.isActive()) {
            vacuumGun.update(world, dt);
        } else {
            player.setVacuuming(false);
        }
        world.update(dt);
        corralSystem.update(world);
        crystalSystem.update(world);
        foodSpawner.update(world, dt);
        market.update(dt);

        if (player.getStats().isDead()) {
            wallet.loseFraction(0.10f);
            player.getStats().restoreFull();
            player.getPos().set(MapBuilder.PLAYER_START_X, MapBuilder.PLAYER_START_Y);
            hud.getPauseMenu().showMessage("Voce desmaiou! (-10% moedas)");
        }

        beamRenderer.update(player, particles, dt);
        ambientEffects.update(world, particles, dt);
        particles.update(dt);

        float targetX = IsoUtils.toScreenX(player.getPos().x, player.getPos().y);
        float targetY = IsoUtils.toScreenY(player.getPos().x, player.getPos().y, player.getGroundZ());
        float alpha = Math.min(1f, Constants.CAMERA_LERP * dt);
        camera.position.x = MathUtils.lerp(camera.position.x, targetX, alpha);
        camera.position.y = MathUtils.lerp(camera.position.y, targetY, alpha);
        camera.update();

        // Verifica quais zonas de spawn saíram do campo de visão da câmera para gerar
        // novos slimes
        spawnSystem.update(world, camera);
    }

    private boolean isNearMarket() {
        return player.getPos().dst(MapBuilder.MARKET_X, MapBuilder.MARKET_Y) <= 3.0f;
    }

    private boolean isNearShop() {
        return player.getPos().dst(MapBuilder.SHOP_X, MapBuilder.SHOP_Y) <= 3.0f;
    }

    /** Desenha o mundo, o feixe do aspirador, partículas e HUD. */
    private void draw(float dt) {
        Gdx.gl.glClearColor(Constants.BACKGROUND.r, Constants.BACKGROUND.g, Constants.BACKGROUND.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldRenderer.render(batch, world, camera);
        buildingSystem.renderHologram(batch, world, corralRenderer, corralSystem);
        batch.end();

        beamRenderer.renderBeam(camera, player);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        particles.render(batch);
        batch.end();

        BiomeType currentBiome = world.getTileMap().getBiomeAt(player.getPos().x, player.getPos().y);
        hud.render(batch, player, wallet, market, upgradeShop, slimepedia,
                currentBiome, isNearMarket(), isNearShop(), buildingSystem, dt);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        beamRenderer.dispose();
        hud.dispose();
    }

    /** @return jogo principal */
    public SlimeFarmGame getGame() {
        return game;
    }
}
