package com.slimefarm.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.assets.Assets;
import com.slimefarm.assets.TextureFactory;
import com.slimefarm.entities.Entity;
import com.slimefarm.util.IsoUtils;

/**
 * Desenha uma sombra elíptica semitransparente no chão sob cada entidade
 * que retorna {@code true} em {@link Entity#hasShadow()}.
 * Quanto mais alto o pulo ({@code z}), menor a sombra.
 */
public class ShadowRenderer implements Assets.LoadTask {

    private static final String TEX_NAME = "shadow";
    private final Assets assets;

    /** @param assets repositório de texturas */
    public ShadowRenderer(Assets assets) {
        this.assets = assets;
    }

    @Override
    public void load(Assets a) {
        Pixmap p = TextureFactory.newPixmap(28, 14);
        TextureFactory.ellipse(p, 14, 7, 13, 6, new Color(0f, 0f, 0f, 0.32f));
        a.register(TEX_NAME, TextureFactory.toTexture(p));
    }

    /**
     * Desenha as sombras de todas as entidades que têm sombra.
     *
     * @param batch    batch ativo
     * @param entities entidades
     */
    public void render(SpriteBatch batch, Array<Entity> entities) {
        if (!assets.has(TEX_NAME)) {
            return;
        }
        TextureRegion shadow = assets.get(TEX_NAME);
        for (Entity e : entities) {
            if (!e.hasShadow()) {
                continue;
            }
            float sx = IsoUtils.toScreenX(e.getPos().x, e.getPos().y);
            float sy = IsoUtils.toScreenY(e.getPos().x, e.getPos().y, e.getGroundZ());
            float scale = Math.max(0.4f, 1f - e.getZ() * 0.3f) * (e.getRadius() / 0.3f);
            float w = shadow.getRegionWidth() * scale;
            float h = shadow.getRegionHeight() * scale;
            batch.draw(shadow, sx - w / 2f, sy - h / 2f, w, h);
        }
    }
}
