package com.slimefarm.world;

import com.badlogic.gdx.math.MathUtils;
import com.slimefarm.economy.Market;
import com.slimefarm.entities.items.CrystalItem;
import com.slimefarm.entities.items.FoodItem;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.entities.props.Bush;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.entities.props.Fence;
import com.slimefarm.entities.props.Flower;
import com.slimefarm.entities.props.Gate;
import com.slimefarm.entities.props.MarketTerminal;
import com.slimefarm.entities.props.Prop;
import com.slimefarm.entities.props.Rock;
import com.slimefarm.entities.props.ShopTerminal;
import com.slimefarm.entities.props.Tree;
import com.slimefarm.entities.slimes.CatSlime;
import com.slimefarm.entities.slimes.PinkSlime;
import com.slimefarm.entities.slimes.RockSlime;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.systems.SpawnSystem;
import com.slimefarm.systems.SpawnZone;
import com.slimefarm.util.Constants;

/**
 * Monta o mundo expandido (80×80 tiles) dividido em 4 biomas exploráveis:
 * <ul>
 *   <li><b>Rancho Central ({@link BiomeType#RANCH})</b>: quadrante noroeste (0..39, 0..39), fiel à referência;</li>
 *   <li><b>Pradaria Florida ({@link BiomeType#MEADOW})</b>: quadrante nordeste (40..79, 0..39);</li>
 *   <li><b>Floresta de Musgo ({@link BiomeType#MOSS_FOREST})</b>: quadrante sudoeste (0..39, 40..79);</li>
 *   <li><b>Pedreira Cristalina ({@link BiomeType#CRYSTAL_QUARRY})</b>: quadrante sudeste (40..79, 40..79).</li>
 * </ul>
 */
public final class MapBuilder {

    /** Posição inicial do jogador no Rancho Central. */
    public static final float PLAYER_START_X = 17.5f;
    public static final float PLAYER_START_Y = 21.5f;
    /** Canto (mínimo) do curral e tamanho em tiles. */
    public static final int CORRAL_X = 21;
    public static final int CORRAL_Y = 15;
    public static final int CORRAL_SIZE = 6;
    /** Posição do terminal do Mercado de Cristais. */
    public static final float MARKET_X = 17.5f;
    public static final float MARKET_Y = 9.5f;
    /** Posição do terminal da Loja de Upgrades. */
    public static final float SHOP_X = 13.0f;
    public static final float SHOP_Y = 9.5f;

    /** Largura do planalto nas bordas externas do mapa. */
    private static final int PLATEAU = 4;
    /** Linha do riacho que separa o norte do sul. */
    private static final int CREEK_Y = 38;

    private MapBuilder() {
    }

    /**
     * Cria o mundo completo (chão + 4 biomas + objetos de cenário).
     *
     * @return mundo pronto
     */
    public static World build() {
        MathUtils.random.setSeed(7);
        TileMap map = new TileMap(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        buildGround(map);
        World world = new World(map);
        placeRanchProps(world);
        placeBiomeProps(world);
        MathUtils.random.setSeed(System.nanoTime());
        return world;
    }

    // ------------------------------------------------------------ chão e biomas

    private static void buildGround(TileMap map) {
        int w = map.getWidth();
        int h = map.getHeight();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                BiomeType biome = determineBiome(x, y);
                map.setBiome(x, y, biome);

                // Bordas externas do mundo como planalto elevado
                if (x <= PLATEAU || y <= PLATEAU || x >= w - PLATEAU || y >= h - PLATEAU) {
                    map.set(x, y, TileType.GRASS_DARK, 1);
                    continue;
                }

                switch (biome) {
                    case RANCH -> {
                        if (MathUtils.randomBoolean(0.15f)) {
                            map.setType(x, y, TileType.GRASS_DARK);
                        }
                    }
                    case MEADOW -> {
                        if (MathUtils.randomBoolean(0.22f)) {
                            map.setType(x, y, TileType.GRASS_DARK);
                        }
                    }
                    case MOSS_FOREST -> map.setType(x, y,
                            MathUtils.randomBoolean(0.75f) ? TileType.MOSS : TileType.GRASS_DARK);
                    case CRYSTAL_QUARRY -> map.setType(x, y,
                            MathUtils.randomBoolean(0.82f) ? TileType.QUARRY_ROCK : TileType.STONE_PATH);
                }
            }
        }

        // Praça central de terra no Rancho
        for (int x = 6; x < 36; x++) {
            for (int y = 6; y < 35; y++) {
                float dx = (x - 20) / 10f;
                float dy = (y - 19) / 9f;
                float d = dx * dx + dy * dy;
                float noise = MathUtils.sin(x * 1.3f) * 0.08f + MathUtils.cos(y * 1.7f) * 0.08f;
                if (d < 1f + noise && map.get(x, y).getElevation() == 0) {
                    map.setType(x, y, TileType.DIRT);
                }
            }
        }

        // Trilhas de terra conectando Rancho -> Pradaria (leste), Floresta (sul) e Pedreira (sudeste)
        dirtRoad(map, 28, 19, 58, 19, 2);
        dirtRoad(map, 18, 26, 18, 58, 2);
        dirtRoad(map, 18, 56, 58, 56, 2);
        dirtRoad(map, 56, 19, 56, 58, 2);

        // Lajes de pedra no Rancho
        stonePatch(map, 7, 19, 4, 2);
        stonePatch(map, 11, 18, 3, 2);
        stonePatch(map, 12, 11, 7, 2);
        stonePatch(map, 18, 13, 2, 2);
        stonePatch(map, 13, 27, 3, 2);
        stonePatch(map, 16, 29, 2, 2);
        stonePatch(map, 27, 18, 3, 3);

        // Riacho horizontal com duas pontes largas (em x=17..19 para a Floresta e x=55..57 para a Pedreira)
        for (int x = PLATEAU + 1; x < w - PLATEAU; x++) {
            for (int y = CREEK_Y; y <= CREEK_Y + 1; y++) {
                boolean isBridge = (x >= 17 && x <= 19) || (x >= 55 && x <= 57);
                map.set(x, y, isBridge ? TileType.BRIDGE : TileType.WATER, 0);
            }
        }
    }

    private static BiomeType determineBiome(int x, int y) {
        if (x < 40 && y < 38) {
            return BiomeType.RANCH;
        } else if (x >= 40 && y < 38) {
            return BiomeType.MEADOW;
        } else if (x < 40) {
            return BiomeType.MOSS_FOREST;
        } else {
            return BiomeType.CRYSTAL_QUARRY;
        }
    }

    private static void dirtRoad(TileMap map, int x0, int y0, int x1, int y1, int halfWidth) {
        int minX = Math.min(x0, x1) - halfWidth;
        int maxX = Math.max(x0, x1) + halfWidth;
        int minY = Math.min(y0, y1) - halfWidth;
        int maxY = Math.max(y0, y1) + halfWidth;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Tile t = map.get(x, y);
                if (t != null && t.getElevation() == 0) {
                    t.setType(TileType.DIRT);
                }
            }
        }
    }

    private static void stonePatch(TileMap map, int x0, int y0, int w, int h) {
        for (int x = x0; x < x0 + w; x++) {
            for (int y = y0; y < y0 + h; y++) {
                Tile t = map.get(x, y);
                if (t != null && t.getElevation() == 0) {
                    t.setType(TileType.STONE_PATH);
                }
            }
        }
    }

    // ------------------------------------------------------------ cenário do Rancho e Biomas

    private static void placeRanchProps(World world) {
        // Árvores nas bordas do Rancho
        float[][] trees = {{9, 9}, {11, 7.5f}, {24, 7.5f}, {31, 8}, {33, 12}, {8, 27}, {33, 25}, {28, 31}, {10, 31}};
        for (float[] t : trees) {
            add(world, new Tree(t[0], t[1], MathUtils.random(2)));
        }
        float[][] bushes = {{7.5f, 24}, {9, 25.5f}, {15, 7.5f}, {21, 8}, {30, 10}, {32, 23}, {26, 29}, {12, 30}, {7.5f, 12}};
        for (int i = 0; i < bushes.length; i++) {
            add(world, new Bush(bushes[i][0], bushes[i][1], i % 2));
        }
        add(world, new Rock(9.5f, 15.5f, true));
        add(world, new Rock(22.5f, 9.5f, true));
        add(world, new Rock(31.5f, 15.5f, true));
        add(world, new Rock(11, 13, false));
        add(world, new Rock(29, 27, false));

        // Cercas do Rancho (deixando saídas abertas para Leste e Sul)
        for (int y = 22; y <= 29; y++) {
            add(world, new Fence(7, y, false));
        }
        for (int x = 9; x <= 15; x++) {
            add(world, new Fence(x, 33, true));
        }
        for (int x = 21; x <= 28; x++) {
            add(world, new Fence(x, 33, true));
        }
        for (int y = 8; y <= 15; y++) {
            add(world, new Fence(30, y, false));
        }
        for (int x = 11; x <= 15; x++) {
            add(world, new Fence(x, 6, true));
        }
        add(world, new Gate(6, 19));
    }

    private static void placeBiomeProps(World world) {
        // Pradaria Florida (Nordeste: x 42..73, y 6..35) -> muitas flores, árvores frutíferas e arbustos
        for (int i = 0; i < 65; i++) {
            float x = MathUtils.random(42f, 73f);
            float y = MathUtils.random(6f, 35f);
            add(world, new Flower(x, y, MathUtils.random(2)));
        }
        for (int i = 0; i < 16; i++) {
            float x = MathUtils.random(43f, 72f);
            float y = MathUtils.random(7f, 34f);
            if (Math.abs(y - 19f) > 3.5f && Math.abs(x - 56f) > 3.5f) {
                add(world, (i % 2 == 0) ? new Tree(x, y, i % 3) : new Bush(x, y, i % 2));
            }
        }

        // Floresta de Musgo (Sudoeste: x 6..37, y 42..73) -> mata fechada de árvores e arbustos de frutas
        for (int i = 0; i < 28; i++) {
            float x = MathUtils.random(7f, 36f);
            float y = MathUtils.random(42f, 72f);
            if (Math.abs(x - 18f) > 3.5f && Math.abs(y - 56f) > 3.5f) {
                add(world, (i % 3 != 0) ? new Tree(x, y, i % 3) : new Bush(x, y, 1));
            }
        }
        for (int i = 0; i < 30; i++) {
            add(world, new Flower(MathUtils.random(7f, 36f), MathUtils.random(42f, 72f), 1));
        }

        // Pedreira Cristalina (Sudeste: x 42..73, y 42..73) -> formações rochosas grandes e pequenas
        for (int i = 0; i < 26; i++) {
            float x = MathUtils.random(43f, 72f);
            float y = MathUtils.random(42f, 72f);
            if (Math.abs(x - 56f) > 3.5f && Math.abs(y - 56f) > 3.5f) {
                add(world, new Rock(x, y, i % 2 == 0));
            }
        }
        for (int i = 0; i < 8; i++) {
            float x = MathUtils.random(44f, 71f);
            float y = MathUtils.random(43f, 71f);
            if (Math.abs(x - 56f) > 4f && Math.abs(y - 56f) > 4f) {
                add(world, new Bush(x, y, 0));
            }
        }
    }

    /**
     * Posiciona o curral de vidro, o terminal do Mercado, o terminal da Loja de Upgrades,
     * os slimes iniciais e registra as zonas de spawn por bioma no {@link SpawnSystem}.
     *
     * @param world       mundo
     * @param market      sistema de mercado
     * @param spawnSystem sistema de spawn por zonas e campo de visão
     * @return instância do {@link Corral} criado
     */
    public static Corral populateInitialScene(World world, Market market, SpawnSystem spawnSystem) {
        Corral corral = new Corral(CORRAL_X, CORRAL_Y, CORRAL_SIZE);
        add(world, corral);
        add(world, new MarketTerminal(MARKET_X, MARKET_Y, market));
        add(world, new ShopTerminal(SHOP_X, SHOP_Y));

        // Slimes dentro do curral (marcados com setCorralled(true) para ficarem presos no vidro)
        addCorralSlime(world, new PinkSlime(CORRAL_X + 1.4f, CORRAL_Y + 4.4f));
        addCorralSlime(world, new PinkSlime(CORRAL_X + 4.6f, CORRAL_Y + 2.2f));
        addCorralSlime(world, new CatSlime(CORRAL_X + 2.1f, CORRAL_Y + 2.5f));
        addCorralSlime(world, new CatSlime(CORRAL_X + 3.0f, CORRAL_Y + 1.8f));
        addCorralSlime(world, new CatSlime(CORRAL_X + 4.4f, CORRAL_Y + 4.6f));
        addCorralSlime(world, new RockSlime(CORRAL_X + 3.8f, CORRAL_Y + 1.5f));
        addCorralSlime(world, new RockSlime(CORRAL_X + 4.5f, CORRAL_Y + 1.7f));

        // Cristais e frutas iniciais dentro do curral
        world.addNow(new CrystalItem(CORRAL_X + 3.3f, CORRAL_Y + 3.1f, ItemType.CRYSTAL_CAT));
        world.addNow(new CrystalItem(CORRAL_X + 3.6f, CORRAL_Y + 3.8f, ItemType.CRYSTAL_PINK));
        world.addNow(new CrystalItem(CORRAL_X + 2.7f, CORRAL_Y + 2.9f, ItemType.CRYSTAL_ROCK));
        world.addNow(new FoodItem(CORRAL_X + 2.6f, CORRAL_Y + 4.0f, ItemType.STRAWBERRY));
        world.addNow(new FoodItem(CORRAL_X + 4.1f, CORRAL_Y + 2.8f, ItemType.APPLE));

        // Frutas iniciais pelo Rancho
        world.addNow(new FoodItem(13.5f, 21.2f, ItemType.APPLE));
        world.addNow(new FoodItem(18.8f, 13.5f, ItemType.STRAWBERRY));
        world.addNow(new FoodItem(14.5f, 11.2f, ItemType.CARROT));

        // Registra zonas de spawn distribuídas pelos 4 biomas
        if (spawnSystem != null) {
            spawnSystem.setCorralBounds(corral.getBounds());
            // Zonas no Rancho Central
            spawnSystem.addZone(new SpawnZone(15f, 18f, 4.5f, BiomeType.RANCH, 4));
            spawnSystem.addZone(new SpawnZone(18f, 28f, 4.5f, BiomeType.RANCH, 3));
            // Zonas na Pradaria Florida (Nordeste)
            spawnSystem.addZone(new SpawnZone(46f, 18f, 6f, BiomeType.MEADOW, 5));
            spawnSystem.addZone(new SpawnZone(60f, 14f, 6f, BiomeType.MEADOW, 5));
            spawnSystem.addZone(new SpawnZone(58f, 28f, 6f, BiomeType.MEADOW, 5));
            // Zonas na Floresta de Musgo (Sudoeste)
            spawnSystem.addZone(new SpawnZone(18f, 48f, 6f, BiomeType.MOSS_FOREST, 5));
            spawnSystem.addZone(new SpawnZone(14f, 62f, 6f, BiomeType.MOSS_FOREST, 5));
            spawnSystem.addZone(new SpawnZone(28f, 58f, 6f, BiomeType.MOSS_FOREST, 5));
            // Zonas na Pedreira Cristalina (Sudeste)
            spawnSystem.addZone(new SpawnZone(48f, 52f, 6f, BiomeType.CRYSTAL_QUARRY, 5));
            spawnSystem.addZone(new SpawnZone(62f, 48f, 6f, BiomeType.CRYSTAL_QUARRY, 5));
            spawnSystem.addZone(new SpawnZone(60f, 64f, 6f, BiomeType.CRYSTAL_QUARRY, 6));

            spawnSystem.populateInitialZones(world);
        }
        return corral;
    }

    private static void addCorralSlime(World world, Slime slime) {
        slime.setCorralled(true);
        world.addNow(slime);
    }

    /** Adiciona um prop e chama {@link Prop#onPlaced(World)}. */
    private static void add(World world, Prop prop) {
        world.addNow(prop);
        prop.onPlaced(world);
    }
}
