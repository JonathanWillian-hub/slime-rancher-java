package com.slimefarm.util;

import com.badlogic.gdx.math.MathUtils;

/**
 * As 8 direções de tela usadas para escolher o sprite de uma entidade.
 * O ângulo é medido na TELA (0° = direita, 90° = cima).
 */
public enum Direction {
    EAST(0), NORTH_EAST(45), NORTH(90), NORTH_WEST(135),
    WEST(180), SOUTH_WEST(225), SOUTH(270), SOUTH_EAST(315);

    /** Ângulo central da direção, em graus. */
    public final float angle;

    Direction(float angle) {
        this.angle = angle;
    }

    /**
     * Converte um ângulo (graus) na direção mais próxima.
     *
     * @param degrees ângulo em graus (qualquer valor)
     * @return direção correspondente
     */
    public static Direction fromAngle(float degrees) {
        float a = ((degrees % 360f) + 360f) % 360f;
        int index = Math.round(a / 45f) % 8;
        return values()[index];
    }

    /**
     * Converte um vetor de tela (dx, dy) em direção.
     *
     * @param dx deslocamento horizontal na tela
     * @param dy deslocamento vertical na tela (positivo = cima)
     * @return direção correspondente
     */
    public static Direction fromVector(float dx, float dy) {
        return fromAngle(MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees);
    }

    /** @return true se a direção aponta para a esquerda da tela (sprite espelhado). */
    public boolean isLeft() {
        return this == WEST || this == NORTH_WEST || this == SOUTH_WEST;
    }

    /** @return true se a direção aponta para cima na tela (personagem de costas). */
    public boolean isUp() {
        return this == NORTH || this == NORTH_EAST || this == NORTH_WEST;
    }
}

