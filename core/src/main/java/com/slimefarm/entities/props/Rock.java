package com.slimefarm.entities.props;

/**
 * Pedra grande cinza. Sólida.
 */
public class Rock extends Prop {

    /**
     * @param x     x (tiles)
     * @param y     y (tiles)
     * @param large true para a pedra grande
     */
    public Rock(float x, float y, boolean large) {
        super(x, y, large ? 0.6f : 0.35f, true, large ? "rock_large" : "rock_small");
    }
}

