package com.slimefarm.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.slimefarm.assets.Assets;
import com.slimefarm.assets.TextureFactory;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;

/**
 * Desenha o {@link Corral} em duas etapas (paredes de trás antes dos slimes e paredes da frente
 * depois dos slimes), com vidro energético translúcido azul-ciano e postes metálicos nos cantos.
 */
public class CorralRenderer implements Assets.LoadTask {

    private final Assets assets;

    /** @param assets repositório de texturas */
    public CorralRenderer(Assets assets) {
        this.assets = assets;
    }

    @Override
    public void load(Assets a) {
        a.register("corral_post", TextureFactory.toTexture(postTexture()));
        Pixmap gx = glassSegmentX();
        a.register("corral_glass_y", TextureFactory.toTexture(mirror(gx)));
        a.register("corral_glass_x", TextureFactory.toTexture(gx));
    }

    /** Desenha as duas paredes traseiras (topo-esquerda e topo-direita) e os postes de trás. */
    public void renderBack(SpriteBatch batch, Corral corral) {
        if (corral == null || !assets.has("corral_post")) {
            return;
        }
        Rectangle b = corral.getBounds();
        int x0 = (int) b.x;
        int y0 = (int) b.y;
        int w = (int) b.width;
        int h = (int) b.height;

        drawPost(batch, x0, y0);
        for (int i = 0; i < w; i++) {
            drawGlassX(batch, x0 + i + 0.5f, y0);
        }
        for (int i = 0; i < h; i++) {
            drawGlassY(batch, x0, y0 + i + 0.5f);
        }
        drawPost(batch, x0 + w, y0);
        drawPost(batch, x0, y0 + h);
    }

    /** Desenha as duas paredes frontais (baixo-esquerda e baixo-direita) e o poste da frente. */
    public void renderFront(SpriteBatch batch, Corral corral) {
        if (corral == null || !assets.has("corral_post")) {
            return;
        }
        Rectangle b = corral.getBounds();
        int x0 = (int) b.x;
        int y0 = (int) b.y;
        int w = (int) b.width;
        int h = (int) b.height;

        for (int i = 0; i < h; i++) {
            drawGlassY(batch, x0 + w, y0 + i + 0.5f);
        }
        for (int i = 0; i < w; i++) {
            drawGlassX(batch, x0 + i + 0.5f, y0 + h);
        }
        drawPost(batch, x0 + w, y0 + h);
    }

    /**
     * Desenha a planta holográfica translúcida (estilo papel de construção do Fortnite)
     * mostrando os 4 pilares nos cantos e as 4 paredes de vidro conectadas.
     *
     * @param batch batch de desenho
     * @param x0    x inicial (tiles)
     * @param y0    y inicial (tiles)
     * @param w     largura em tiles
     * @param h     altura em tiles
     * @param valid true se o local é válido (ciano/verde); false se bloqueado (vermelho)
     * @param pulse valor de pulsação para efeito animado
     */
    public void renderHologram(SpriteBatch batch, int x0, int y0, int w, int h, boolean valid, float pulse) {
        if (!assets.has("corral_post")) {
            return;
        }
        float alpha = 0.55f + 0.20f * (float) Math.sin(pulse);
        Color holoColor = valid ? new Color(0.35f, 0.85f, 1f, alpha) : new Color(1f, 0.30f, 0.35f, alpha);
        batch.setColor(holoColor);

        // Paredes de trás e pilares traseiros
        drawPost(batch, x0, y0);
        for (int i = 0; i < w; i++) {
            drawGlassX(batch, x0 + i + 0.5f, y0);
        }
        for (int i = 0; i < h; i++) {
            drawGlassY(batch, x0, y0 + i + 0.5f);
        }
        drawPost(batch, x0 + w, y0);
        drawPost(batch, x0, y0 + h);

        // Paredes da frente e pilar frontal (4º pilar fechando o cercado)
        for (int i = 0; i < h; i++) {
            drawGlassY(batch, x0 + w, y0 + i + 0.5f);
        }
        for (int i = 0; i < w; i++) {
            drawGlassX(batch, x0 + i + 0.5f, y0 + h);
        }
        drawPost(batch, x0 + w, y0 + h);

        batch.setColor(Color.WHITE);
    }

    /** Desenha um pilar individual holográfico na coordenada do terreno. */
    public void drawHoloPost(SpriteBatch batch, float wx, float wy, Color color) {
        if (!assets.has("corral_post")) {
            return;
        }
        TextureRegion post = assets.get("corral_post");
        float sx = IsoUtils.toScreenX(wx, wy);
        float sy = IsoUtils.toScreenY(wx, wy, 0f);
        batch.setColor(color);
        batch.draw(post, sx - post.getRegionWidth() / 2f, sy - 4f);
        batch.setColor(Color.WHITE);
    }

    /** Desenha um segmento de parede de vidro holográfica entre dois pilares alinhados. */
    public void drawHoloWall(SpriteBatch batch, float x1, float y1, float x2, float y2, Color color) {
        batch.setColor(color);
        if (Math.abs(y1 - y2) < 0.1f) {
            int minX = (int) Math.min(x1, x2);
            int maxX = (int) Math.max(x1, x2);
            for (int x = minX; x < maxX; x++) {
                drawGlassX(batch, x + 0.5f, y1);
            }
        } else if (Math.abs(x1 - x2) < 0.1f) {
            int minY = (int) Math.min(y1, y2);
            int maxY = (int) Math.max(y1, y2);
            for (int y = minY; y < maxY; y++) {
                drawGlassY(batch, x1, y + 0.5f);
            }
        }
        batch.setColor(Color.WHITE);
    }

    /** Destaca um cercado em vermelho pulsante indicando que será desmontado/removido. */
    public void renderDemolishHighlight(SpriteBatch batch, Corral corral, float pulse) {
        if (corral == null) {
            return;
        }
        Rectangle b = corral.getBounds();
        renderHologram(batch, (int) b.x, (int) b.y, (int) b.width, (int) b.height, false, pulse);
    }

    private void drawPost(SpriteBatch batch, float wx, float wy) {
        TextureRegion post = assets.get("corral_post");
        float sx = IsoUtils.toScreenX(wx, wy);
        float sy = IsoUtils.toScreenY(wx, wy, 0f);
        batch.draw(post, sx - post.getRegionWidth() / 2f, sy - 4f);
    }

    private void drawGlassX(SpriteBatch batch, float wx, float wy) {
        TextureRegion reg = assets.get("corral_glass_x");
        float sx = IsoUtils.toScreenX(wx, wy);
        float sy = IsoUtils.toScreenY(wx, wy, 0f);
        batch.draw(reg, sx - reg.getRegionWidth() / 2f, sy - 8f);
    }

    private void drawGlassY(SpriteBatch batch, float wx, float wy) {
        TextureRegion reg = assets.get("corral_glass_y");
        float sx = IsoUtils.toScreenX(wx, wy);
        float sy = IsoUtils.toScreenY(wx, wy, 0f);
        batch.draw(reg, sx - reg.getRegionWidth() / 2f, sy - 8f);
    }

    private static Pixmap postTexture() {
        int w = 16;
        int h = 38;
        Pixmap p = TextureFactory.newPixmap(w, h);
        Color outline = Color.valueOf("1F2937");
        Color metal = Color.valueOf("9CA8B8");
        Color light = Color.valueOf("D5DFEC");
        Color glow = Color.valueOf("72F2FF");
        TextureFactory.rect(p, 2, 2, 12, 34, outline);
        TextureFactory.rect(p, 3, 3, 10, 32, metal);
        TextureFactory.rect(p, 4, 3, 3, 32, light);
        TextureFactory.rect(p, 4, 6, 8, 3, glow);
        TextureFactory.rect(p, 4, 28, 8, 2, glow);
        return p;
    }

    private static Pixmap glassSegmentX() {
        int w = 36;
        int h = 38;
        Pixmap p = TextureFactory.newPixmap(w, h);
        Color glow = new Color(0.65f, 0.96f, 1f, 0.80f);
        Color glass = Constants.GLASS;
        int wallH = 20;
        for (int x = 2; x <= 34; x++) {
            int gy = 18 + (x - 2) / 2;
            p.setColor(glass);
            p.drawLine(x, gy - wallH, x, gy);
            TextureFactory.pixel(p, x, gy - wallH, glow);
            TextureFactory.pixel(p, x, gy - wallH + 1, glow);
            TextureFactory.pixel(p, x, gy, glow);
        }
        return p;
    }

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

