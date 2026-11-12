package com.slimefarm.entities.props;

/**
 * Árvore de copa arredondada. Sólida (o tronco bloqueia a passagem).
 */
public class Tree extends Prop {

    /**
     * @param x       x (tiles)
     * @param y       y (tiles)
     * @param variant variação visual (0..2)
     */
    public Tree(float x, float y, int variant) {
        super(x, y, 0.35f, true, "tree_" + variant);
    }
}

