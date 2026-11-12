package com.slimefarm;

import com.badlogic.gdx.math.Vector2;
import com.slimefarm.economy.Market;
import com.slimefarm.economy.UpgradeShop;
import com.slimefarm.economy.UpgradeType;
import com.slimefarm.economy.Wallet;
import com.slimefarm.entities.Player;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.entities.slimes.PinkSlime;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.inventory.Inventory;
import com.slimefarm.progression.Slimepedia;
import com.slimefarm.systems.CorralSystem;
import com.slimefarm.systems.SpawnSystem;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.world.BiomeType;
import com.slimefarm.world.TileMap;
import com.slimefarm.world.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários da matemática isométrica ({@link IsoUtils}),
 * inventário ({@link Inventory}), carteira/mercado ({@link Wallet}, {@link Market}),
 * barreira seletiva do curral ({@link CorralSystem}), Slimepedia ({@link Slimepedia}),
 * Loja de Upgrades ({@link UpgradeShop}) e sorteio de slimes por bioma ({@link SpawnSystem}).
 */
class CoreSystemsTest {

    @Test
    void isoWorldScreenRoundTripIsConsistent() {
        Vector2 screen = new Vector2();
        Vector2 backToWorld = new Vector2();

        IsoUtils.worldToScreen(14.25f, 19.75f, 0f, screen);
        IsoUtils.screenToWorld(screen.x, screen.y, backToWorld);

        assertEquals(14.25f, backToWorld.x, 1e-4f);
        assertEquals(19.75f, backToWorld.y, 1e-4f);
    }

    @Test
    void inventoryStacksSameTypeAndLimitsCapacity() {
        Inventory inv = new Inventory();
        for (int i = 0; i < Constants.SLOT_MAX; i++) {
            assertTrue(inv.add(ItemType.STRAWBERRY));
        }
        assertEquals(Constants.SLOT_MAX, inv.getSlot(0).getCount());
        assertTrue(inv.getSlot(1).isEmpty());

        assertTrue(inv.add(ItemType.STRAWBERRY));
        assertEquals(1, inv.getSlot(1).getCount());
        assertEquals(Constants.SLOT_MAX + 1, inv.count(ItemType.STRAWBERRY));
    }

    @Test
    void walletAndMarketSellCrystalsCorrectly() {
        Wallet wallet = new Wallet();
        Market market = new Market(wallet);
        Inventory inv = new Inventory();

        int initial = wallet.getCoins();
        inv.add(ItemType.CRYSTAL_PINK);
        inv.add(ItemType.CRYSTAL_GOLD);

        assertTrue(market.sellFromInventory(inv, ItemType.CRYSTAL_PINK));
        assertFalse(market.sellFromInventory(inv, ItemType.STRAWBERRY));

        int earned = market.sellAllFromInventory(inv);
        assertTrue(earned > 0);
        assertTrue(wallet.getCoins() > initial);
        assertEquals(0, inv.count(ItemType.CRYSTAL_PINK));
        assertEquals(0, inv.count(ItemType.CRYSTAL_GOLD));
    }

    @Test
    void slimepediaTracksCapturedSpeciesAndUpgradeShopAppliesBonuses() {
        Slimepedia dex = new Slimepedia();
        assertFalse(dex.isCaptured(SlimeType.HONEY));
        assertTrue(dex.registerCapture(SlimeType.HONEY));
        assertTrue(dex.isCaptured(SlimeType.HONEY));
        assertEquals(1, dex.getCapturedCount());

        Wallet wallet = new Wallet();
        UpgradeShop shop = new UpgradeShop(wallet);
        Player player = new Player(10f, 10f);
        assertTrue(shop.buy(UpgradeType.JET_BOOTS, player));
        assertEquals(1, shop.getLevel(UpgradeType.JET_BOOTS));
        assertTrue(player.getStats().getSpeedMultiplier() > 1.0f);
    }

    @Test
    void corralBlocksWildSlimesUnlessLaunchedByPlayerAndBiomeSpawnRespectsRules() {
        World world = new World(new TileMap(40, 40));
        Corral corral = new Corral(10, 10, 6);
        CorralSystem sys = new CorralSystem(corral);

        // Slime selvagem tentando entrar sem ter sido jogado pelo player é empurrado para fora
        PinkSlime wild = new PinkSlime(10.1f, 13f);
        world.addNow(wild);
        sys.update(world);
        assertFalse(wild.isCorralled());
        assertFalse(corral.getBounds().contains(wild.getPos()));

        // Slime jogado pelo player para dentro do curral passa a ficar preso (corralled = true)
        PinkSlime thrown = new PinkSlime(12.5f, 12.5f);
        thrown.launch(1f, 1f, 3f);
        world.addNow(thrown);
        sys.update(world);
        assertTrue(thrown.isCorralled());

        // Sorteio por bioma respeita os biomas permitidos da espécie
        for (int i = 0; i < 30; i++) {
            SlimeType t = SpawnSystem.rollSlimeForBiome(BiomeType.MOSS_FOREST);
            assertTrue(t.canSpawnIn(BiomeType.MOSS_FOREST));
        }
    }

    @Test
    void mapBuilderBuildsExpandedWorldAndPopulatesScene() {
        World world = com.slimefarm.world.MapBuilder.build();
        org.junit.jupiter.api.Assertions.assertNotNull(world);
        assertEquals(com.slimefarm.util.Constants.MAP_WIDTH, world.getTileMap().getWidth());
        assertEquals(com.slimefarm.util.Constants.MAP_HEIGHT, world.getTileMap().getHeight());

        Wallet wallet = new Wallet();
        Market market = new Market(wallet);
        SpawnSystem spawnSystem = new SpawnSystem();
        com.slimefarm.entities.props.Corral corral = com.slimefarm.world.MapBuilder.populateInitialScene(world, market, spawnSystem);
        org.junit.jupiter.api.Assertions.assertNotNull(corral);
        assertTrue(world.getEntities().size > 10);
    }

    @Test
    void buildingSystemValidatesAndPlacesCorralsWithFourPillars() {
        World world = com.slimefarm.world.MapBuilder.build();
        Corral initialCorral = new Corral(21, 15, 6);
        com.slimefarm.systems.CorralSystem sys = new com.slimefarm.systems.CorralSystem(initialCorral);
        com.slimefarm.systems.BuildingSystem buildSys = new com.slimefarm.systems.BuildingSystem();

        // Área plana válida longe de obstáculos (Pradaria aberta)
        assertTrue(buildSys.isValidPlacement(world, 54, 17, 4, 4, sys));

        // Área sobreposta ao curral existente é rejeitada
        assertFalse(buildSys.isValidPlacement(world, 20, 14, 6, 6, sys));

        // Área nas bordas do planalto elevado (elevação > 0) é rejeitada
        assertFalse(buildSys.isValidPlacement(world, 1, 1, 5, 5, sys));

        // Criação de um segundo cercado retangular de 4 pilares
        Corral newCorral = new Corral(54, 17, 5, 4);
        sys.addCorral(newCorral);
        assertEquals(2, sys.getCorrals().size);

        // Slime dentro do segundo cercado fica devidamente contido
        PinkSlime s2 = new PinkSlime(56f, 19f);
        s2.setCorralled(true);
        world.addNow(s2);
        sys.update(world);
        assertTrue(newCorral.getBounds().contains(s2.getPos()));

        // Remoção / Demolição do cercado: retira do sistema e liberta o slime
        assertTrue(buildSys.demolishCorral(newCorral, world, sys, null, null, null));
        assertEquals(1, sys.getCorrals().size);
        assertFalse(s2.isCorralled());
        assertTrue(newCorral.isRemoved());
    }
}
