package com.slimefarm.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Conversões entre coordenadas de MUNDO (tiles, x/y no chão + z de altura)
 * e coordenadas de TELA (pixels, Y para cima como no libGDX).
 *
 * <pre>
 * screenX = (x - y) * TILE_W / 2
 * screenY = -(x + y) * TILE_H / 2 + z * Z_PIXELS
 * </pre>
 * O tile (0,0) fica no topo do mapa; x cresce para baixo-direita, y para baixo-esquerda.
 */
public final class IsoUtils {

    private IsoUtils() {
    }

    /** @return coordenada X de tela do ponto de mundo (x, y). */
    public static float toScreenX(float x, float y) {
        return (x - y) * Constants.HALF_W;
    }

    /** @return coordenada Y de tela do ponto de mundo (x, y, z). */
    public static float toScreenY(float x, float y, float z) {
        return -(x + y) * Constants.HALF_H + z * Constants.Z_PIXELS;
    }

    /**
     * Converte mundo → tela.
     *
     * @param x   x do mundo
     * @param y   y do mundo
     * @param z   altura
     * @param out vetor de saída (reutilizado para evitar lixo)
     * @return o próprio {@code out}
     */
    public static Vector2 worldToScreen(float x, float y, float z, Vector2 out) {
        return out.set(toScreenX(x, y), toScreenY(x, y, z));
    }

    /**
     * Converte tela → mundo (assumindo z = 0, ou seja, no chão).
     *
     * @param sx  x da tela
     * @param sy  y da tela
     * @param out vetor de saída
     * @return o próprio {@code out}
     */
    public static Vector2 screenToWorld(float sx, float sy, Vector2 out) {
        float a = sx / Constants.HALF_W;   // x - y
        float b = -sy / Constants.HALF_H;  // x + y
        return out.set((a + b) / 2f, (b - a) / 2f);
    }

    /**
     * Converte um vetor de direção de TELA (ex.: WASD) em vetor de MUNDO.
     * O resultado é normalizado se não for zero.
     *
     * @param dx  direção horizontal na tela
     * @param dy  direção vertical na tela (positivo = cima)
     * @param out vetor de saída
     * @return o próprio {@code out}
     */
    public static Vector2 screenDirToWorld(float dx, float dy, Vector2 out) {
        // Usa as mesmas fórmulas, mas tratando o tile como quadrado visual (2:1 compensado)
        float a = dx;   // x - y
        float b = -dy;  // x + y
        out.set((a + b) / 2f, (b - a) / 2f);
        if (!out.isZero()) {
            out.nor();
        }
        return out;
    }

    /**
     * Converte uma direção de MUNDO em direção de TELA (para escolher sprites/ângulos).
     *
     * @param wx  direção x no mundo
     * @param wy  direção y no mundo
     * @param out vetor de saída
     * @return o próprio {@code out}
     */
    public static Vector2 worldDirToScreen(float wx, float wy, Vector2 out) {
        return out.set((wx - wy) * Constants.HALF_W, -(wx + wy) * Constants.HALF_H);
    }

    /**
     * Valor de profundidade: quanto MAIOR, mais "na frente" (desenha depois).
     *
     * @param x x do mundo
     * @param y y do mundo
     * @return profundidade para ordenação
     */
    public static float depth(float x, float y) {
        return x + y;
    }
}

