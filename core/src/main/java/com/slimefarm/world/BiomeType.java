package com.slimefarm.world;

import com.badlogic.gdx.graphics.Color;

/**
 * Biomas do mundo expandido. Cada bioma possui um nome de exibição e uma cor temática
 * para o indicador do HUD e para a Slimepedia.
 */
public enum BiomeType {
    RANCH("Rancho Central", Color.valueOf("E8A85A")),
    MEADOW("Pradaria Florida", Color.valueOf("68D85B")),
    MOSS_FOREST("Floresta de Musgo", Color.valueOf("38B88C")),
    CRYSTAL_QUARRY("Pedreira Cristalina", Color.valueOf("9882F5"));

    /** Nome legível exibido na interface e na Slimepedia. */
    public final String displayName;
    /** Cor temática do bioma. */
    public final Color themeColor;

    BiomeType(String displayName, Color themeColor) {
        this.displayName = displayName;
        this.themeColor = themeColor;
    }
}
