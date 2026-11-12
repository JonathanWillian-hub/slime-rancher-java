package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.slimefarm.util.Constants;

/**
 * Gera os ícones originais da interface (coração de vida, raio de energia e moeda dourada).
 */
public final class UiTextureFactory implements Assets.LoadTask {

    private static final Color OUTLINE = Color.valueOf("1B1E26");

    @Override
    public void load(Assets assets) {
        assets.register("ui_heart", TextureFactory.toTexture(heart()));
        assets.register("ui_bolt", TextureFactory.toTexture(bolt()));
        assets.register("ui_coin", TextureFactory.toTexture(coin()));
    }

    private static Pixmap heart() {
        Pixmap p = TextureFactory.newPixmap(18, 18);
        Color red = Constants.HEALTH;
        TextureFactory.ellipseOutlined(p, 6, 7, 4, 4, red, OUTLINE);
        TextureFactory.ellipseOutlined(p, 12, 7, 4, 4, red, OUTLINE);
        TextureFactory.triangle(p, 2, 8, 16, 8, 9, 16, OUTLINE);
        TextureFactory.triangle(p, 3, 8, 15, 8, 9, 15, red);
        TextureFactory.pixel(p, 5, 6, Color.WHITE);
        return p;
    }

    private static Pixmap bolt() {
        Pixmap p = TextureFactory.newPixmap(18, 18);
        Color blue = Constants.ENERGY;
        TextureFactory.triangle(p, 11, 2, 4, 10, 10, 10, OUTLINE);
        TextureFactory.triangle(p, 8, 8, 14, 8, 7, 16, OUTLINE);
        TextureFactory.triangle(p, 11, 3, 5, 9, 10, 9, blue);
        TextureFactory.triangle(p, 8, 9, 13, 9, 7, 15, TextureFactory.lighter(blue, 0.25f));
        return p;
    }

    private static Pixmap coin() {
        Pixmap p = TextureFactory.newPixmap(18, 18);
        Color gold = Color.valueOf("FFC83B");
        TextureFactory.ellipseOutlined(p, 9, 9, 7, 7, gold, OUTLINE);
        TextureFactory.ellipse(p, 8, 8, 5, 5, TextureFactory.lighter(gold, 0.25f));
        TextureFactory.rect(p, 8, 6, 2, 6, Color.valueOf("B87C14"));
        return p;
    }
}

