package com.slimefarm.entities.props;

/**
 * Prop de ponte de madeira (opcional se desenhada como objeto sobre a água).
 */
public class Bridge extends Prop {

    /**
     * @param tileX coluna do tile
     * @param tileY linha do tile
     */
    public Bridge(int tileX, int tileY) {
        super(tileX + 0.5f, tileY + 0.5f, 0.1f, false, "tile_bridge_0");
    }
}

