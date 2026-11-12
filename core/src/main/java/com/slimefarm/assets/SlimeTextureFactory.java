package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.slimefarm.entities.slimes.SlimeType;

/**
 * Gera os sprites originais dos 6 tipos de slime (Rosa, Gatinho, Pedra, Mel, Fósforo e Dourado)
 * em 3 expressões faciais: {@code _happy}, {@code _neutral} e {@code _angry}.
 */
public final class SlimeTextureFactory implements Assets.LoadTask {

    private static final Color OUTLINE = Color.valueOf("1F1624");
    private static final Color CHEEK = Color.valueOf("FF9DB8");
    private static final Color MOUTH = Color.valueOf("7A1C3A");

    @Override
    public void load(Assets assets) {
        String[] moods = {"_happy", "_neutral", "_angry"};
        for (int m = 0; m < moods.length; m++) {
            for (SlimeType type : SlimeType.values()) {
                assets.register(type.texturePrefix + moods[m],
                        TextureFactory.toTexture(drawSlime(type, m)));
            }
        }
    }

    private static Pixmap drawSlime(SlimeType type, int mood) {
        int w = 36;
        int h = 34;
        Pixmap p = TextureFactory.newPixmap(w, h);
        int cx = w / 2;
        int cy = 20;
        Color base = type.color;
        Color dark = TextureFactory.darker(base, 0.22f);
        Color light = TextureFactory.lighter(base, 0.35f);

        // Orelhinhas de gato (Slime Gatinho)
        if (type == SlimeType.CAT) {
            TextureFactory.triangle(p, cx - 12, cy - 5, cx - 9, 4, cx - 3, cy - 8, OUTLINE);
            TextureFactory.triangle(p, cx - 11, cy - 5, cx - 9, 6, cx - 4, cy - 8, base);
            TextureFactory.triangle(p, cx - 10, cy - 5, cx - 9, 8, cx - 6, cy - 7, CHEEK);

            TextureFactory.triangle(p, cx + 12, cy - 5, cx + 9, 4, cx + 3, cy - 8, OUTLINE);
            TextureFactory.triangle(p, cx + 11, cy - 5, cx + 9, 6, cx + 4, cy - 8, base);
            TextureFactory.triangle(p, cx + 10, cy - 5, cx + 9, 8, cx + 6, cy - 7, CHEEK);
        }

        // Espinhos minerais (Slime Pedra e coroa de cristal no Dourado)
        if (type == SlimeType.ROCK || type == SlimeType.GOLD) {
            Color spike = (type == SlimeType.GOLD)
                    ? Color.valueOf("FFF380")
                    : TextureFactory.darker(base, 0.35f);
            Color spikeLight = TextureFactory.lighter(spike, 0.25f);
            int[][] spikes = {{cx - 8, 6}, {cx, 3}, {cx + 8, 6}};
            for (int[] s : spikes) {
                TextureFactory.triangle(p, s[0] - 5, cy - 6, s[0], s[1], s[0] + 5, cy - 6, OUTLINE);
                TextureFactory.triangle(p, s[0] - 4, cy - 6, s[0], s[1] + 2, s[0] + 4, cy - 6, spike);
                TextureFactory.triangle(p, s[0] - 3, cy - 6, s[0], s[1] + 2, s[0], cy - 6, spikeLight);
            }
        }

        // Anteninhas luminosas e asinhas laterais (Slime Fósforo)
        if (type == SlimeType.PHOSPHOR) {
            Color glow = Color.valueOf("D8FFFF");
            p.setColor(OUTLINE);
            p.drawLine(cx - 4, cy - 9, cx - 7, 4);
            p.drawLine(cx + 4, cy - 9, cx + 7, 4);
            TextureFactory.ellipseOutlined(p, cx - 7, 4, 2, 2, glow, OUTLINE);
            TextureFactory.ellipseOutlined(p, cx + 7, 4, 2, 2, glow, OUTLINE);
            // Asinhas translúcidas
            TextureFactory.ellipseOutlined(p, cx - 14, cy - 3, 3, 4, glow, OUTLINE);
            TextureFactory.ellipseOutlined(p, cx + 14, cy - 3, 3, 4, glow, OUTLINE);
        }

        // Corpo gelatinoso arredondado
        TextureFactory.ellipseOutlined(p, cx, cy, 13, 10, dark, OUTLINE);
        TextureFactory.ellipse(p, cx, cy - 1, 12, 9, base);
        TextureFactory.ellipse(p, cx - 4, cy - 4, 5, 3, light);
        TextureFactory.pixel(p, cx - 6, cy - 5, Color.WHITE);

        // Emblema hexagonal de mel na testa (Slime de Mel)
        if (type == SlimeType.HONEY) {
            TextureFactory.diamond(p, cx, cy - 6, 4, 3, Color.valueOf("FFE57F"));
            TextureFactory.diamond(p, cx, cy - 6, 3, 2, Color.valueOf("C76B0E"));
        }

        // Bochechas
        if (type != SlimeType.ROCK) {
            TextureFactory.ellipse(p, cx - 7, cy + 1, 2, 1, CHEEK);
            TextureFactory.ellipse(p, cx + 7, cy + 1, 2, 1, CHEEK);
        }

        drawFace(p, cx, cy, mood);
        return p;
    }

    private static void drawFace(Pixmap p, int cx, int cy, int mood) {
        if (mood == 0) {
            TextureFactory.rect(p, cx - 6, cy - 2, 3, 2, OUTLINE);
            TextureFactory.rect(p, cx + 4, cy - 2, 3, 2, OUTLINE);
            TextureFactory.pixel(p, cx - 5, cy - 2, Color.WHITE);
            TextureFactory.pixel(p, cx + 4, cy - 2, Color.WHITE);
            TextureFactory.ellipse(p, cx, cy + 3, 3, 2, MOUTH);
            TextureFactory.pixel(p, cx, cy + 4, CHEEK);
        } else if (mood == 1) {
            TextureFactory.rect(p, cx - 6, cy - 2, 2, 3, OUTLINE);
            TextureFactory.rect(p, cx + 4, cy - 2, 2, 3, OUTLINE);
            TextureFactory.rect(p, cx - 2, cy + 3, 4, 1, OUTLINE);
        } else {
            TextureFactory.rect(p, cx - 6, cy - 1, 2, 2, OUTLINE);
            TextureFactory.rect(p, cx + 4, cy - 1, 2, 2, OUTLINE);
            p.setColor(OUTLINE);
            p.drawLine(cx - 7, cy - 4, cx - 3, cy - 2);
            p.drawLine(cx + 7, cy - 4, cx + 3, cy - 2);
            TextureFactory.rect(p, cx - 2, cy + 3, 5, 2, MOUTH);
        }
    }
}
