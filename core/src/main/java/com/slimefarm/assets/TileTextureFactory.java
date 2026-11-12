package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.slimefarm.util.Constants;
import com.slimefarm.world.Tile;

/**
 * Gera as texturas (originais) dos tiles do chão: grama, grama escura, terra,
 * laje de pedra, ponte, água e a face lateral dos penhascos.
 * <p>
 * Cada tile tem {@value Tile#VARIANTS} variações para o chão não parecer repetitivo.
 */
public final class TileTextureFactory implements Assets.LoadTask {

    private static final int W = Constants.TILE_W;
    private static final int H = Constants.TILE_H;
    private static final int CX = W / 2;
    private static final int CY = H / 2;

    @Override
    public void load(Assets assets) {
        for (int v = 0; v < Tile.VARIANTS; v++) {
            MathUtils.random.setSeed(1000 + v);
            assets.register("tile_grass_" + v, TextureFactory.toTexture(grass(Constants.GRASS, v)));
            assets.register("tile_grass_dark_" + v, TextureFactory.toTexture(grass(Constants.GRASS_DARK, v)));
            assets.register("tile_dirt_" + v, TextureFactory.toTexture(dirt(v)));
            assets.register("tile_stone_" + v, TextureFactory.toTexture(stone(v)));
            assets.register("tile_bridge_" + v, TextureFactory.toTexture(bridge()));
            assets.register("tile_water_" + v, TextureFactory.toTexture(water(v)));
            assets.register("tile_moss_" + v, TextureFactory.toTexture(grass(Color.valueOf("2C7D60"), v)));
            assets.register("tile_quarry_" + v, TextureFactory.toTexture(quarryRock(v)));
        }
        assets.register("cliff_side", TextureFactory.toTexture(cliffSide()));
        MathUtils.random.setSeed(System.nanoTime());
    }

    /** @return true se (x, y) do pixmap está dentro do losango do tile. */
    private static boolean inside(int x, int y) {
        return Math.abs(x + 0.5f - CX) / (W / 2f) + Math.abs(y + 0.5f - CY) / (H / 2f) <= 1f;
    }

    /** Espalha pontinhos de uma cor dentro do losango. */
    private static void speckle(Pixmap p, Color c, int count, int size) {
        p.setColor(c);
        for (int i = 0; i < count; i++) {
            int x = MathUtils.random(4, W - 5);
            int y = MathUtils.random(2, H - 3);
            if (inside(x, y) && inside(x + size, y + size)) {
                p.fillRectangle(x, y, size, size);
            }
        }
    }

    private static Pixmap grass(Color base, int v) {
        Pixmap p = TextureFactory.newPixmap(W, H);
        TextureFactory.diamond(p, CX, CY, W / 2, H / 2, base);
        speckle(p, TextureFactory.darker(base, 0.15f), 30, 1);
        speckle(p, TextureFactory.lighter(base, 0.2f), 18, 1);
        // tufos de grama (pequenos "v")
        Color tuft = TextureFactory.lighter(base, 0.3f);
        for (int i = 0; i < 4 + v; i++) {
            int x = MathUtils.random(10, W - 12);
            int y = MathUtils.random(6, H - 6);
            if (inside(x - 2, y) && inside(x + 2, y - 3)) {
                TextureFactory.pixel(p, x - 1, y - 1, tuft);
                TextureFactory.pixel(p, x, y - 2, tuft);
                TextureFactory.pixel(p, x + 1, y - 1, tuft);
            }
        }
        return p;
    }

    private static Pixmap dirt(int v) {
        Pixmap p = TextureFactory.newPixmap(W, H);
        TextureFactory.diamond(p, CX, CY, W / 2, H / 2, Constants.DIRT);
        speckle(p, Constants.DIRT_DARK, 40, 1);
        speckle(p, TextureFactory.lighter(Constants.DIRT, 0.15f), 25, 1);
        speckle(p, TextureFactory.darker(Constants.DIRT, 0.3f), 3 + v, 2);
        return p;
    }

    private static Pixmap stone(int v) {
        Pixmap p = TextureFactory.newPixmap(W, H);
        Color gap = TextureFactory.darker(Constants.STONE, 0.35f);
        TextureFactory.diamond(p, CX, CY, W / 2, H / 2, gap);
        // 4 lajes (quatro losangos menores)
        Color slab = Constants.STONE;
        int[][] centers = {{CX, CY - 8}, {CX - 16, CY}, {CX + 16, CY}, {CX, CY + 8}};
        for (int i = 0; i < centers.length; i++) {
            Color c = (i + v) % 3 == 0 ? TextureFactory.lighter(slab, 0.1f) : slab;
            TextureFactory.diamond(p, centers[i][0], centers[i][1], 14, 7, c);
            TextureFactory.pixel(p, centers[i][0] - 3, centers[i][1] - 2, TextureFactory.lighter(c, 0.3f));
        }
        return p;
    }

    private static Pixmap bridge() {
        Pixmap p = TextureFactory.newPixmap(W, H);
        Color wood = Color.valueOf("A0693A");
        TextureFactory.diamond(p, CX, CY, W / 2, H / 2, wood);
        // tábuas: linhas paralelas na direção isométrica
        p.setColor(TextureFactory.darker(wood, 0.35f));
        for (int k = -W; k < W; k += 8) {
            for (int x = 0; x < W; x++) {
                int y = (x + k) / 2;
                if (y >= 0 && y < H && inside(x, y)) {
                    p.drawPixel(x, y);
                }
            }
        }
        return p;
    }

    private static Pixmap water(int v) {
        Pixmap p = TextureFactory.newPixmap(W, H);
        Color water = Color.valueOf("3D8FD1");
        TextureFactory.diamond(p, CX, CY, W / 2, H / 2, water);
        speckle(p, TextureFactory.lighter(water, 0.35f), 6 + v, 2);
        return p;
    }

    /** Piso rochoso arroxeado/acinzentado da Pedreira Cristalina, com veios minerais brilhantes. */
    private static Pixmap quarryRock(int v) {
        Pixmap p = TextureFactory.newPixmap(W, H);
        Color base = Color.valueOf("564E68");
        TextureFactory.diamond(p, CX, CY, W / 2, H / 2, base);
        speckle(p, TextureFactory.darker(base, 0.25f), 32, 1);
        speckle(p, TextureFactory.lighter(base, 0.20f), 16, 1);
        speckle(p, Color.valueOf("9C88FF"), 3 + v, 2);
        return p;
    }

    /**
     * Face lateral do penhasco: as duas faces visíveis (esquerda e direita)
     * de um bloco com altura {@link Constants#Z_PIXELS}. A imagem tem o mesmo
     * topo do tile; desenha-se no mesmo ponto do tile elevado.
     */
    private static Pixmap cliffSide() {
        int depth = (int) Constants.Z_PIXELS;
        Pixmap p = TextureFactory.newPixmap(W, H + depth);
        Color left = Constants.CLIFF;
        Color right = TextureFactory.darker(Constants.CLIFF, 0.25f);
        for (int x = 0; x < W; x++) {
            // borda inferior do losango na coluna x
            int edge = x < CX ? CY + x / 2 : CY + (W - 1 - x) / 2;
            p.setColor(x < CX ? left : right);
            p.drawLine(x, edge, x, edge + depth);
        }
        // camadas de rocha (linhas horizontais escuras) e grama na borda
        Color strata = TextureFactory.darker(Constants.CLIFF, 0.4f);
        Color grassEdge = Constants.GRASS_DARK;
        for (int x = 0; x < W; x++) {
            int edge = x < CX ? CY + x / 2 : CY + (W - 1 - x) / 2;
            TextureFactory.pixel(p, x, edge, grassEdge);
            TextureFactory.pixel(p, x, edge + 1, grassEdge);
            if ((x / 3) % 2 == 0) {
                TextureFactory.pixel(p, x, edge + 2, grassEdge);
            }
            TextureFactory.pixel(p, x, edge + 9, strata);
            TextureFactory.pixel(p, x, edge + 17, strata);
        }
        return p;
    }
}

