package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;

/**
 * Gera as texturas (originais, pixel-art via código) dos objetos de cenário:
 * árvores, arbustos, pedras, flores, cercas e portão.
 */
public final class PropTextureFactory implements Assets.LoadTask {

    private static final Color OUTLINE = Color.valueOf("1E2A1A");
    private static final Color WOOD = Color.valueOf("8A5A32");
    private static final Color WOOD_DARK = Color.valueOf("5E3A1F");
    private static final Color WOOD_LIGHT = Color.valueOf("B07A48");

    @Override
    public void load(Assets assets) {
        MathUtils.random.setSeed(42);
        Color[] leaves = {Color.valueOf("3E9B3A"), Color.valueOf("2F8A3A"), Color.valueOf("4FAE3C")};
        for (int i = 0; i < 3; i++) {
            assets.register("tree_" + i, TextureFactory.toTexture(tree(leaves[i], i)));
        }
        assets.register("bush_0", TextureFactory.toTexture(bush(Color.valueOf("3C9A3A"), false)));
        assets.register("bush_1", TextureFactory.toTexture(bush(Color.valueOf("348C36"), true)));
        assets.register("rock_large", TextureFactory.toTexture(rock(48, 36)));
        assets.register("rock_small", TextureFactory.toTexture(rock(26, 18)));
        Color[] petals = {Color.valueOf("FF7A8A"), Color.valueOf("FFD84A"), Color.valueOf("FFFFFF")};
        for (int i = 0; i < 3; i++) {
            assets.register("flower_" + i, TextureFactory.toTexture(flower(petals[i])));
        }
        Pixmap fx = fence();
        assets.register("fence_y", TextureFactory.toTexture(mirror(fx)));
        assets.register("fence_x", TextureFactory.toTexture(fx));
        assets.register("gate", TextureFactory.toTexture(gate()));
        assets.register("market_terminal", TextureFactory.toTexture(marketTerminal()));
        assets.register("shop_terminal", TextureFactory.toTexture(shopTerminal()));
        MathUtils.random.setSeed(System.nanoTime());
    }

    /** Árvore: tronco marrom e copa formada por várias bolas de folhas. */
    private static Pixmap tree(Color leaf, int variant) {
        int w = 80;
        int h = 104;
        Pixmap p = TextureFactory.newPixmap(w, h);
        int cx = w / 2;
        // tronco com raízes
        TextureFactory.rect(p, cx - 6, h - 34, 12, 30, OUTLINE);
        TextureFactory.rect(p, cx - 5, h - 34, 10, 29, WOOD);
        TextureFactory.rect(p, cx + 1, h - 34, 4, 29, WOOD_DARK);
        TextureFactory.triangle(p, cx - 11, h - 3, cx - 4, h - 14, cx - 2, h - 3, WOOD_DARK);
        TextureFactory.triangle(p, cx + 11, h - 3, cx + 4, h - 14, cx + 2, h - 3, WOOD_DARK);
        // copa: bolas escuras (sombra) e depois claras
        Color dark = TextureFactory.darker(leaf, 0.3f);
        Color light = TextureFactory.lighter(leaf, 0.25f);
        int top = 8 + variant * 2;
        int[][] blobs = {{cx - 18, top + 30, 18}, {cx + 18, top + 30, 18}, {cx, top + 20, 22}, {cx - 8, top + 38, 16}, {cx + 10, top + 40, 16}};
        for (int[] b : blobs) {
            TextureFactory.ellipse(p, b[0], b[1], b[2] + 1, b[2], OUTLINE);
        }
        for (int[] b : blobs) {
            TextureFactory.ellipse(p, b[0], b[1], b[2], b[2] - 1, dark);
        }
        for (int[] b : blobs) {
            TextureFactory.ellipse(p, b[0] - 2, b[1] - 3, b[2] - 4, b[2] - 5, leaf);
        }
        for (int[] b : blobs) {
            TextureFactory.ellipse(p, b[0] - 5, b[1] - 7, b[2] / 3, b[2] / 4, light);
        }
        return p;
    }

    /** Arbusto baixo com folhas; opcionalmente com frutinhas vermelhas. */
    private static Pixmap bush(Color leaf, boolean berries) {
        int w = 44;
        int h = 30;
        Pixmap p = TextureFactory.newPixmap(w, h);
        int[][] blobs = {{12, 18, 10}, {30, 18, 10}, {21, 12, 11}};
        for (int[] b : blobs) {
            TextureFactory.ellipse(p, b[0], b[1], b[2] + 1, b[2], OUTLINE);
        }
        for (int[] b : blobs) {
            TextureFactory.ellipse(p, b[0], b[1], b[2], b[2] - 1, TextureFactory.darker(leaf, 0.2f));
            TextureFactory.ellipse(p, b[0] - 2, b[1] - 2, b[2] - 4, b[2] - 5, leaf);
            TextureFactory.ellipse(p, b[0] - 4, b[1] - 4, 2, 2, TextureFactory.lighter(leaf, 0.3f));
        }
        if (berries) {
            Color red = Color.valueOf("E0304A");
            int[][] pos = {{10, 16}, {24, 10}, {32, 20}, {18, 22}};
            for (int[] b : pos) {
                TextureFactory.ellipse(p, b[0], b[1], 2, 2, red);
                TextureFactory.pixel(p, b[0] - 1, b[1] - 1, Color.WHITE);
            }
        }
        return p;
    }

    /** Pedra cinza arredondada com face clara e sombra. */
    private static Pixmap rock(int w, int h) {
        Pixmap p = TextureFactory.newPixmap(w, h);
        Color base = Color.valueOf("8E9196");
        int cx = w / 2;
        int cy = h / 2 + 2;
        TextureFactory.ellipse(p, cx, cy, w / 2 - 1, h / 2 - 2, OUTLINE);
        TextureFactory.ellipse(p, cx, cy, w / 2 - 2, h / 2 - 3, TextureFactory.darker(base, 0.25f));
        TextureFactory.ellipse(p, cx - 3, cy - 3, w / 2 - 6, h / 2 - 6, base);
        TextureFactory.ellipse(p, cx - w / 6, cy - h / 5, w / 6, h / 8, TextureFactory.lighter(base, 0.35f));
        // rachadura
        p.setColor(TextureFactory.darker(base, 0.45f));
        p.drawLine(cx + 2, cy - h / 4, cx + 5, cy);
        return p;
    }

    /** Flor: caule verde e quatro pétalas. */
    private static Pixmap flower(Color petal) {
        Pixmap p = TextureFactory.newPixmap(9, 12);
        TextureFactory.rect(p, 4, 5, 1, 7, Color.valueOf("2E7A2A"));
        TextureFactory.rect(p, 3, 1, 3, 3, petal);
        TextureFactory.rect(p, 2, 2, 5, 1, petal);
        TextureFactory.rect(p, 4, 0, 1, 5, petal);
        TextureFactory.pixel(p, 4, 2, Color.valueOf("F2B630"));
        return p;
    }

    /**
     * Pedaço de cerca ao longo do eixo X (na tela, desce para a direita).
     * Base: ponto do meio do tile fica 8px acima da borda inferior.
     */
    private static Pixmap fence() {
        int w = 36;
        int h = 34;
        Pixmap p = TextureFactory.newPixmap(w, h);
        // linha do chão vai de (2,17) até (34,33)
        int postH = 16;
        // trilhos (duas tábuas inclinadas)
        for (int k = 0; k < 2; k++) {
            int lift = 5 + k * 6;
            for (int x = 2; x <= 34; x++) {
                int gy = 17 + (x - 2) / 2;
                TextureFactory.pixel(p, x, gy - lift - 1, OUTLINE);
                TextureFactory.pixel(p, x, gy - lift, WOOD_LIGHT);
                TextureFactory.pixel(p, x, gy - lift + 1, WOOD);
                TextureFactory.pixel(p, x, gy - lift + 2, OUTLINE);
            }
        }
        // postes nas pontas
        post(p, 2, 17, postH);
        post(p, 31, 32, postH);
        return p;
    }

    /** Desenha um poste de madeira com base em (x, groundY). */
    private static void post(Pixmap p, int x, int groundY, int height) {
        TextureFactory.rect(p, x - 1, groundY - height - 1, 5, height + 2, OUTLINE);
        TextureFactory.rect(p, x, groundY - height, 3, height, WOOD);
        TextureFactory.rect(p, x, groundY - height, 1, height, WOOD_LIGHT);
        TextureFactory.rect(p, x, groundY - height, 3, 1, WOOD_LIGHT);
    }

    /** Portão: dois postes altos com arames, um deles caído. */
    private static Pixmap gate() {
        int w = 48;
        int h = 48;
        Pixmap p = TextureFactory.newPixmap(w, h);
        Color metal = Color.valueOf("C9CED6");
        post(p, 6, 30, 26);
        post(p, 40, 46, 26);
        p.setColor(metal);
        for (int i = 0; i < 4; i++) {
            p.drawLine(8, 8 + i * 5, 40, 24 + i * 5 + (i == 3 ? 8 : 0));
        }
        return p;
    }

    /** Terminal do Mercado de Cristais: gabinete metálico com painel luminoso e ícones de cristais. */
    private static Pixmap marketTerminal() {
        int w = 56;
        int h = 68;
        Pixmap p = TextureFactory.newPixmap(w, h);
        Color body = Color.valueOf("9CA8B8");
        Color bodyDark = Color.valueOf("6B7787");
        Color screenBg = Color.valueOf("1F3B5C");
        Color screenGlow = Color.valueOf("49C6E5");
        // Base e pedestal
        TextureFactory.rect(p, 8, 36, 40, 28, OUTLINE);
        TextureFactory.rect(p, 9, 37, 38, 26, body);
        TextureFactory.rect(p, 9, 50, 38, 13, bodyDark);
        // Grade de depósito e botões
        TextureFactory.rect(p, 13, 40, 20, 8, OUTLINE);
        TextureFactory.rect(p, 36, 41, 4, 3, Color.valueOf("3CE065"));
        TextureFactory.rect(p, 36, 46, 4, 3, Color.valueOf("E04040"));
        // Monitor superior
        TextureFactory.rect(p, 6, 4, 44, 34, OUTLINE);
        TextureFactory.rect(p, 7, 5, 42, 32, body);
        TextureFactory.rect(p, 10, 8, 36, 26, screenBg);
        TextureFactory.rect(p, 11, 9, 34, 4, screenGlow);
        // 3 pequenos cartões de cristal na tela
        Color[] cols = {Color.valueOf("FF6699"), Color.valueOf("C468FF"), Color.valueOf("4CE0B3")};
        for (int i = 0; i < 3; i++) {
            int cx = 16 + i * 11;
            TextureFactory.rect(p, cx - 4, 16, 9, 14, Color.valueOf("2C527D"));
            TextureFactory.diamond(p, cx, 22, 3, 4, cols[i]);
        }
        return p;
    }

    /** Terminal da Loja de Upgrades: quiosque dourado/laranja com ícone de engrenagem/raio. */
    private static Pixmap shopTerminal() {
        int w = 52;
        int h = 64;
        Pixmap p = TextureFactory.newPixmap(w, h);
        Color body = Color.valueOf("D98A2B");
        Color bodyDark = Color.valueOf("9E5D14");
        Color screenBg = Color.valueOf("241B38");
        Color gold = Color.valueOf("FFE566");
        TextureFactory.rect(p, 8, 34, 36, 26, OUTLINE);
        TextureFactory.rect(p, 9, 35, 34, 24, body);
        TextureFactory.rect(p, 9, 47, 34, 12, bodyDark);
        TextureFactory.rect(p, 6, 4, 40, 32, OUTLINE);
        TextureFactory.rect(p, 7, 5, 38, 30, body);
        TextureFactory.rect(p, 10, 8, 32, 24, screenBg);
        TextureFactory.rect(p, 11, 9, 30, 4, gold);
        TextureFactory.diamond(p, 26, 21, 7, 7, gold);
        TextureFactory.diamond(p, 26, 21, 3, 3, screenBg);
        return p;
    }

    /** @return cópia espelhada horizontalmente do pixmap (o original continua válido). */
    private static Pixmap mirror(Pixmap src) {
        Pixmap out = TextureFactory.newPixmap(src.getWidth(), src.getHeight());
        out.setBlending(Pixmap.Blending.None);
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                out.drawPixel(src.getWidth() - 1 - x, y, src.getPixel(x, y));
            }
        }
        out.setBlending(Pixmap.Blending.SourceOver);
        return out;
    }
}

