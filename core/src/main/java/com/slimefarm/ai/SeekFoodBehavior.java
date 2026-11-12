package com.slimefarm.ai;

import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.items.FoodItem;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.util.Constants;
import com.slimefarm.world.World;

/**
 * Comportamento de busca de comida: procura o {@link FoodItem} mais próximo dentro do raio
 * de visão ({@link Constants#SLIME_SIGHT}), priorizando a comida favorita do slime.
 */
public class SeekFoodBehavior {

    private final Array<FoodItem> candidates = new Array<>();

    /**
     * Procura a melhor comida próxima ao slime.
     *
     * @param slime slime faminto
     * @param world mundo
     * @return comida escolhida, ou {@code null} se não houver nenhuma perto
     */
    public FoodItem findNearestFood(Slime slime, World world) {
        world.findInRadius(slime.getPos(), Constants.SLIME_SIGHT, FoodItem.class, candidates);
        FoodItem best = null;
        float bestScore = Float.MAX_VALUE;
        for (FoodItem food : candidates) {
            if (food.isRemoved()) {
                continue;
            }
            float dist = slime.distanceTo(food);
            // Comida favorita conta como se estivesse na metade da distância
            float score = (food.getType() == slime.getType().favoriteFood) ? dist * 0.5f : dist;
            if (score < bestScore) {
                bestScore = score;
                best = food;
            }
        }
        return best;
    }
}

