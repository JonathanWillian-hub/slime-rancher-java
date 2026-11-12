package com.slimefarm.entities.items;

/**
 * Entidade de cristal produzido por um slime após se alimentar.
 * Pode ser aspirado e vendido no terminal do mercado.
 */
public class CrystalItem extends ItemEntity {

    /**
     * @param x    posição x
     * @param y    posição y
     * @param type tipo de cristal ({@link ItemType#isCrystal()})
     */
    public CrystalItem(float x, float y, ItemType type) {
        super(x, y, type);
    }
}

