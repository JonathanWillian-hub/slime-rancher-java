package com.slimefarm.entities.slimes;

/**
 * Slime Fósforo: raro (10% de chance), aparece na Floresta de Musgo e na Pradaria Florida.
 * Possui anteninhas luminosas e produz Cristais Luminescentes.
 */
public class PhosphorSlime extends Slime {

    /**
     * @param x posição x inicial
     * @param y posição y inicial
     */
    public PhosphorSlime(float x, float y) {
        super(x, y, SlimeType.PHOSPHOR);
    }
}
