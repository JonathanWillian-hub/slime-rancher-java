package com.slimefarm.entities.props;

/**
 * Arbusto decorativo. Sólido e pequeno; perto dele nascem frutinhas.
 */
public class Bush extends Prop {

    /**
     * @param x       x (tiles)
     * @param y       y (tiles)
     * @param variant variação visual (0..1)
     */
    public Bush(float x, float y, int variant) {
        super(x, y, 0.35f, true, "bush_" + variant);
    }
}

