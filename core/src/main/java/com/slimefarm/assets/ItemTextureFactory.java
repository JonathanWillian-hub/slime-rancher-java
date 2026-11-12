package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.slimefarm.entities.items.ItemType;

/**
 * Gera texturas originais em pixel-art para frutas, vegetais e cristais facetados.
 */
public final class ItemTextureFactory implements Assets.LoadTask {

    private static final Color OUTLINE = Color.valueOf("1B1E26");
    private static final Color LEAF = Color.valueOf("3CA840");

    @Override
    public void load(Assets assets) {
        assets.register(ItemType.STRAWBERRY.texture, TextureFactory.toTexture(strawberry()));
        assets.register(ItemType.APPLE.texture, TextureFactory.toTexture(apple()));
        assets.register(ItemType.CARROT.texture, TextureFactory.toTexture(carrot()));

        assets.register(ItemType.CRYSTAL_PINK.texture,
                TextureFactory.toTexture(crystal(ItemType.CRYSTAL_PINK.color)));
        assets.register(ItemType.CRYSTAL_CAT.texture,
                TextureFactory.toTexture(crystal(ItemType.CRYSTAL_CAT.color)));
        assets.register(ItemType.CRYSTAL_ROCK.texture,
                TextureFactory.toTexture(crystal(ItemType.CRYSTAL_ROCK.color)));
        assets.register(ItemType.CRYSTAL_HONEY.texture,
                TextureFactory.toTexture(crystal(ItemType.CRYSTAL_HONEY.color)));
        assets.register(ItemType.CRYSTAL_PHOSPHOR.texture,
                TextureFactory.toTexture(crystal(ItemType.CRYSTAL_PHOSPHOR.color)));
        assets.register(ItemType.CRYSTAL_GOLD.texture,
                TextureFactory.toTexture(crystal(ItemType.CRYSTAL_GOLD.color)));
        assets.register(ItemType.BLUEPRINT_CORRAL.texture,
                TextureFactory.toTexture(blueprint()));
    }

    private static Pixmap blueprint() {
        Pixmap p = TextureFactory.newPixmap(20, 20);
        Color border = Color.valueOf("1E3A8A");
        Color bg = Color.valueOf("2563EB");
        Color grid = Color.valueOf("93C5FD");
        Color pillar = Color.valueOf("FFFFFF");

        TextureFactory.rect(p, 2, 2, 16, 16, border);
        TextureFactory.rect(p, 3, 3, 14, 14, bg);

        // Grade da planta arquitetônica
        for (int x = 6; x <= 14; x += 4) {
            p.setColor(grid);
            p.drawLine(x, 4, x, 15);
        }
        for (int y = 6; y <= 14; y += 4) {
            p.setColor(grid);
            p.drawLine(4, y, 15, y);
        }

        // 4 pilares desenhados nos 4 cantos da planta
        TextureFactory.pixel(p, 5, 5, pillar);
        TextureFactory.pixel(p, 14, 5, pillar);
        TextureFactory.pixel(p, 5, 14, pillar);
        TextureFactory.pixel(p, 14, 14, pillar);

        return p;
    }

    private static Pixmap strawberry() {
        Pixmap p = TextureFactory.newPixmap(18, 20);
        Color red = ItemType.STRAWBERRY.color;
        TextureFactory.ellipseOutlined(p, 9, 11, 6, 7, red, OUTLINE);
        TextureFactory.ellipse(p, 7, 9, 3, 4, TextureFactory.lighter(red, 0.25f));
        // folhinhas no topo
        TextureFactory.rect(p, 6, 3, 6, 2, LEAF);
        TextureFactory.pixel(p, 8, 2, LEAF);
        // sementinhas claras
        Color seed = Color.valueOf("FFE07A");
        TextureFactory.pixel(p, 7, 10, seed);
        TextureFactory.pixel(p, 11, 11, seed);
        TextureFactory.pixel(p, 8, 14, seed);
        return p;
    }

    private static Pixmap apple() {
        Pixmap p = TextureFactory.newPixmap(18, 20);
        Color red = ItemType.APPLE.color;
        TextureFactory.ellipseOutlined(p, 9, 11, 6, 6, red, OUTLINE);
        TextureFactory.ellipse(p, 7, 9, 2, 2, TextureFactory.lighter(red, 0.4f));
        TextureFactory.rect(p, 8, 3, 2, 3, Color.valueOf("6B4423"));
        TextureFactory.rect(p, 10, 4, 3, 2, LEAF);
        return p;
    }

    private static Pixmap carrot() {
        Pixmap p = TextureFactory.newPixmap(18, 20);
        Color orange = ItemType.CARROT.color;
        TextureFactory.triangle(p, 4, 6, 14, 6, 9, 18, OUTLINE);
        TextureFactory.triangle(p, 5, 7, 13, 7, 9, 17, orange);
        TextureFactory.rect(p, 7, 3, 4, 4, LEAF);
        return p;
    }

    /** Cristal em formato de losango facetado com brilho especular. */
    private static Pixmap crystal(Color base) {
        int w = 18;
        int h = 22;
        Pixmap p = TextureFactory.newPixmap(w, h);
        int cx = w / 2;
        int cy = h / 2;
        TextureFactory.diamond(p, cx, cy, 7, 9, OUTLINE);
        TextureFactory.diamond(p, cx, cy, 6, 8, TextureFactory.darker(base, 0.2f));
        // faceta esquerda mais clara
        TextureFactory.triangle(p, cx, cy - 8, cx - 6, cy, cx, cy + 8, base);
        TextureFactory.triangle(p, cx, cy - 7, cx - 4, cy - 1, cx, cy + 1, TextureFactory.lighter(base, 0.45f));
        TextureFactory.pixel(p, cx - 2, cy - 4, Color.WHITE);
        TextureFactory.pixel(p, cx - 1, cy - 3, Color.WHITE);
        return p;
    }
}

