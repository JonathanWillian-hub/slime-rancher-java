package com.slimefarm.entities.items;

/**
 * Entidade de alimento (fruta ou vegetal) que slimes famintos procuram e comem.
 */
public class FoodItem extends ItemEntity {

    /**
     * @param x    posição x
     * @param y    posição y
     * @param type tipo de comida ({@link ItemType#isFood()})
     */
    public FoodItem(float x, float y, ItemType type) {
        super(x, y, type);
    }
}

