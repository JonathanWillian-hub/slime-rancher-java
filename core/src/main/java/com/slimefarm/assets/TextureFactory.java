package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Utilitários de desenho em {@link Pixmap} usados pelas fábricas de texturas.
 * Toda a arte do jogo é ORIGINAL e gerada por código a partir destas primitivas.
 * <p>
 * Atenção: no Pixmap o eixo Y cresce para BAIXO (0 = topo da imagem).
 */
public final class TextureFactory {

    private TextureFactory() {
    }

    /** Cria um Pixmap transparente RGBA8888. */
    public static Pixmap newPixmap(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setBlending(Pixmap.Blending.SourceOver);
        p.setColor(0, 0, 0, 0);
        p.fill();
        return p;
    }

    /** Converte o Pixmap em textura (filtro Nearest para pixel-art) e libera o Pixmap. */
    public static Texture toTexture(Pixmap p) {
        Texture t = new Texture(p);
        t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        p.dispose();
        return t;
    }

    /**
     * Desenha um losango preenchido (formato do tile isométrico).
     *
     * @param p  destino
     * @param cx centro x
     * @param cy centro y
     * @param hw meia largura
     * @param hh meia altura
     * @param c  cor
     */
    public static void diamond(Pixmap p, int cx, int cy, int hw, int hh, Color c) {
        p.setColor(c);
        for (int dy = -hh; dy <= hh; dy++) {
            int half = Math.round(hw * (1f - Math.abs(dy) / (float) hh));
            if (half > 0) {
                p.drawLine(cx - half, cy + dy, cx + half - 1, cy + dy);
            }
        }
    }

    /** Desenha o contorno de um losango. */
    public static void diamondOutline(Pixmap p, int cx, int cy, int hw, int hh, Color c) {
        p.setColor(c);
        p.drawLine(cx - hw, cy, cx, cy - hh);
        p.drawLine(cx, cy - hh, cx + hw - 1, cy);
        p.drawLine(cx + hw - 1, cy, cx, cy + hh);
        p.drawLine(cx, cy + hh, cx - hw, cy);
    }

    /** Desenha uma elipse preenchida. */
    public static void ellipse(Pixmap p, int cx, int cy, int rx, int ry, Color c) {
        p.setColor(c);
        for (int dy = -ry; dy <= ry; dy++) {
            float t = dy / (float) ry;
            int half = (int) Math.round(rx * Math.sqrt(Math.max(0, 1 - t * t)));
            p.drawLine(cx - half, cy + dy, cx + half, cy + dy);
        }
    }

    /** Desenha uma elipse com contorno escuro de 1px. */
    public static void ellipseOutlined(Pixmap p, int cx, int cy, int rx, int ry, Color fill, Color outline) {
        ellipse(p, cx, cy, rx + 1, ry + 1, outline);
        ellipse(p, cx, cy, rx, ry, fill);
    }

    /** Retângulo preenchido. */
    public static void rect(Pixmap p, int x, int y, int w, int h, Color c) {
        p.setColor(c);
        p.fillRectangle(x, y, w, h);
    }

    /** Triângulo preenchido. */
    public static void triangle(Pixmap p, int x1, int y1, int x2, int y2, int x3, int y3, Color c) {
        p.setColor(c);
        p.fillTriangle(x1, y1, x2, y2, x3, y3);
    }

    /** Pinta um pixel. */
    public static void pixel(Pixmap p, int x, int y, Color c) {
        p.setColor(c);
        p.drawPixel(x, y);
    }

    /** @return cópia da cor clareada (amount 0..1). */
    public static Color lighter(Color c, float amount) {
        return new Color(c).lerp(Color.WHITE, amount);
    }

    /** @return cópia da cor escurecida (amount 0..1). */
    public static Color darker(Color c, float amount) {
        return new Color(c).lerp(Color.BLACK, amount);
    }

    /** @return cópia da cor com outro alpha. */
    public static Color alpha(Color c, float a) {
        return new Color(c.r, c.g, c.b, a);
    }
}

