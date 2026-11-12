package com.slimefarm.entities.slimes;

/**
 * Slime de Mel: raro, exclusivo da Floresta de Musgo (12% de chance).
 * Possui um favo hexagonal dourado na testa e produz Cristais de Mel.
 */
public class HoneySlime extends Slime {

    /**
     * @param x posição x inicial
     * @param y posição y inicial
     */
    public HoneySlime(float x, float y) {
        super(x, y, SlimeType.HONEY);
    }
}
