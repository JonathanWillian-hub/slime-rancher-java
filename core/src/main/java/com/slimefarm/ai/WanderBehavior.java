package com.slimefarm.ai;

import com.badlogic.gdx.math.Vector2;
import com.slimefarm.util.RandomUtils;

/**
 * Comportamento de vagar aleatoriamente: sorteia uma direção no chão para o próximo pulo.
 */
public class WanderBehavior {

    /**
     * Escolhe uma velocidade horizontal aleatória para um pulinho de passeio.
     *
     * @param speed velocidade desejada (tiles/s)
     * @param out   vetor de saída
     * @return o próprio {@code out}
     */
    public Vector2 pickDirection(float speed, Vector2 out) {
        float angle = RandomUtils.range(0f, 6.2831855f);
        return out.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);
    }
}

