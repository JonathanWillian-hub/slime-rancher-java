package com.slimefarm.entities.slimes;

/**
 * Slime Dourado: lendário (apenas 3% de chance), exclusivo da Pedreira Cristalina.
 * Brilha intensamente e produz o valioso Cristal Dourado.
 */
public class GoldSlime extends Slime {

    /**
     * @param x posição x inicial
     * @param y posição y inicial
     */
    public GoldSlime(float x, float y) {
        super(x, y, SlimeType.GOLD);
    }
}
