package com.slimefarm.entities.slimes;

import com.badlogic.gdx.graphics.Color;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.world.BiomeType;

/**
 * Tipos de slimes do jogo, com nome, cor, comida favorita, cristal produzido,
 * porcentagem de chance de spawn e lista de biomas onde podem aparecer.
 */
public enum SlimeType {
    PINK(
            "Slime Rosa",
            "slime_pink",
            Color.valueOf("F0507A"),
            ItemType.STRAWBERRY,
            ItemType.CRYSTAL_PINK,
            ItemType.SLIME_PINK,
            0f,
            45,
            new BiomeType[]{BiomeType.RANCH, BiomeType.MEADOW, BiomeType.MOSS_FOREST, BiomeType.CRYSTAL_QUARRY}
    ),
    CAT(
            "Slime Gatinho",
            "slime_cat",
            Color.valueOf("F5648C"),
            ItemType.APPLE,
            ItemType.CRYSTAL_CAT,
            ItemType.SLIME_CAT,
            0f,
            25,
            new BiomeType[]{BiomeType.MEADOW, BiomeType.MOSS_FOREST}
    ),
    ROCK(
            "Slime Pedra",
            "slime_rock",
            Color.valueOf("8D939E"),
            ItemType.CARROT,
            ItemType.CRYSTAL_ROCK,
            ItemType.SLIME_ROCK,
            12f,
            25,
            new BiomeType[]{BiomeType.CRYSTAL_QUARRY, BiomeType.MEADOW}
    ),
    HONEY(
            "Slime de Mel",
            "slime_honey",
            Color.valueOf("F09C2B"),
            ItemType.APPLE,
            ItemType.CRYSTAL_HONEY,
            ItemType.SLIME_HONEY,
            0f,
            12,
            new BiomeType[]{BiomeType.MOSS_FOREST}
    ),
    PHOSPHOR(
            "Slime Fosforo",
            "slime_phosphor",
            Color.valueOf("5CE1FF"),
            ItemType.STRAWBERRY,
            ItemType.CRYSTAL_PHOSPHOR,
            ItemType.SLIME_PHOSPHOR,
            0f,
            10,
            new BiomeType[]{BiomeType.MOSS_FOREST, BiomeType.MEADOW}
    ),
    GOLD(
            "Slime Dourado",
            "slime_gold",
            Color.valueOf("FFD635"),
            ItemType.CARROT,
            ItemType.CRYSTAL_GOLD,
            ItemType.SLIME_GOLD,
            0f,
            3,
            new BiomeType[]{BiomeType.CRYSTAL_QUARRY}
    );

    /** Nome em português exibido na Slimepedia. */
    public final String displayName;
    /** Prefixo da textura ("slime_pink_happy", "slime_pink_neutral", "slime_pink_angry"). */
    public final String texturePrefix;
    /** Cor principal do slime. */
    public final Color color;
    /** Comida favorita (reduz mais a fome e gera 2 cristais). */
    public final ItemType favoriteFood;
    /** Cristal gerado após se alimentar. */
    public final ItemType crystalItem;
    /** Item que representa este slime quando guardado na hotbar. */
    public final ItemType inventoryItem;
    /** Dano causado ao jogador quando agitado (0 se pacífico). */
    public final float contactDamage;
    /** Porcentagem de chance de spawn (ex.: 45%, 25%, 12%, 10%, 3%). */
    public final int spawnChancePercent;
    /** Biomas em que este slime pode nascer. */
    public final BiomeType[] allowedBiomes;

    SlimeType(String displayName, String texturePrefix, Color color, ItemType favoriteFood,
              ItemType crystalItem, ItemType inventoryItem, float contactDamage,
              int spawnChancePercent, BiomeType[] allowedBiomes) {
        this.displayName = displayName;
        this.texturePrefix = texturePrefix;
        this.color = color;
        this.favoriteFood = favoriteFood;
        this.crystalItem = crystalItem;
        this.inventoryItem = inventoryItem;
        this.contactDamage = contactDamage;
        this.spawnChancePercent = spawnChancePercent;
        this.allowedBiomes = allowedBiomes;
    }

    /**
     * Verifica se este slime pode nascer no bioma informado.
     *
     * @param biome bioma consultado
     * @return true se aparece nesse bioma
     */
    public boolean canSpawnIn(BiomeType biome) {
        for (BiomeType b : allowedBiomes) {
            if (b == biome) {
                return true;
            }
        }
        return false;
    }

    /** @return texto formatado com os nomes dos biomas onde este slime aparece */
    public String getBiomesDescription() {
        if (allowedBiomes.length >= BiomeType.values().length) {
            return "Todos os Biomas";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allowedBiomes.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(allowedBiomes[i].displayName);
        }
        return sb.toString();
    }

    /**
     * Busca o {@link SlimeType} correspondente a um {@link ItemType} de slime.
     *
     * @param item item da hotbar
     * @return tipo de slime, ou {@code null} se o item não for um slime
     */
    public static SlimeType fromInventoryItem(ItemType item) {
        for (SlimeType t : values()) {
            if (t.inventoryItem == item) {
                return t;
            }
        }
        return null;
    }
}
