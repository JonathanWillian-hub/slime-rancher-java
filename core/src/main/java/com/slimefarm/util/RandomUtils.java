package com.slimefarm.util;

import com.badlogic.gdx.math.MathUtils;

/**
 * Atalhos para números aleatórios usados em spawns, IA e efeitos.
 */
public final class RandomUtils {

    private RandomUtils() {
    }

    /** @return float aleatório entre min (incluso) e max (excluso). */
    public static float range(float min, float max) {
        return MathUtils.random(min, max);
    }

    /** @return int aleatório entre min e max (ambos inclusos). */
    public static int range(int min, int max) {
        return MathUtils.random(min, max);
    }

    /**
     * @param probability chance entre 0 e 1
     * @return true com a probabilidade informada
     */
    public static boolean chance(float probability) {
        return MathUtils.random() < probability;
    }

    /**
     * Escolhe um elemento aleatório de um array.
     *
     * @param options opções
     * @param <T>     tipo do elemento
     * @return elemento sorteado
     */
    @SafeVarargs
    public static <T> T pick(T... options) {
        return options[MathUtils.random(options.length - 1)];
    }
}

