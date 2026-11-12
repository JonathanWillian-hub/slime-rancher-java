package com.slimefarm.entities.props;

/**
 * Florzinha decorativa (não sólida).
 */
public class Flower extends Prop {

    /**
     * @param x       x (tiles)
     * @param y       y (tiles)
     * @param variant cor (0..2)
     */
    public Flower(float x, float y, int variant) {
        super(x, y, 0.1f, false, "flower_" + variant);
    }
}

