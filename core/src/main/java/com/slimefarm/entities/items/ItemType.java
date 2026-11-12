package com.slimefarm.entities.items;

import com.badlogic.gdx.graphics.Color;

/**
 * Tipos de itens existentes no mundo e no inventário:
 * comidas (frutas/vegetais), cristais (produzidos pelos 6 tipos de slimes) e slimes capturados.
 */
public enum ItemType {
    // Comidas
    STRAWBERRY("Morango", "item_strawberry", Category.FOOD, 0, Color.valueOf("E43B44")),
    APPLE("Maca Doce", "item_apple", Category.FOOD, 0, Color.valueOf("D83030")),
    CARROT("Cenoura", "item_carrot", Category.FOOD, 0, Color.valueOf("F08028")),

    // Cristais (vendáveis no mercado)
    CRYSTAL_PINK("Cristal Rosa", "item_crystal_pink", Category.CRYSTAL, 15, Color.valueOf("FF6699")),
    CRYSTAL_CAT("Cristal Gatinho", "item_crystal_cat", Category.CRYSTAL, 28, Color.valueOf("C468FF")),
    CRYSTAL_ROCK("Cristal Rocha", "item_crystal_rock", Category.CRYSTAL, 35, Color.valueOf("4CE0B3")),
    CRYSTAL_HONEY("Cristal de Mel", "item_crystal_honey", Category.CRYSTAL, 55, Color.valueOf("FFB02E")),
    CRYSTAL_PHOSPHOR("Cristal Luminescente", "item_crystal_phosphor", Category.CRYSTAL, 65, Color.valueOf("66F7FF")),
    CRYSTAL_GOLD("Cristal Dourado", "item_crystal_gold", Category.CRYSTAL, 180, Color.valueOf("FFE552")),

    // Slimes guardados na hotbar
    SLIME_PINK("Slime Rosa", "slime_pink_happy", Category.SLIME, 0, Color.valueOf("F0507A")),
    SLIME_CAT("Slime Gatinho", "slime_cat_happy", Category.SLIME, 0, Color.valueOf("F5648C")),
    SLIME_ROCK("Slime Pedra", "slime_rock_happy", Category.SLIME, 0, Color.valueOf("8D939E")),
    SLIME_HONEY("Slime de Mel", "slime_honey_happy", Category.SLIME, 0, Color.valueOf("F09C2B")),
    SLIME_PHOSPHOR("Slime Fosforo", "slime_phosphor_happy", Category.SLIME, 0, Color.valueOf("5CE1FF")),
    SLIME_GOLD("Slime Dourado", "slime_gold_happy", Category.SLIME, 0, Color.valueOf("FFD635")),

    // Construção (estilo Fortnite)
    BLUEPRINT_CORRAL("Papel de Construcao (Curral)", "item_blueprint", Category.BUILDING, 0, Color.valueOf("38BDF8"));

    /** Categoria do item. */
    public enum Category {
        FOOD, CRYSTAL, SLIME, BUILDING
    }

    /** Nome legível em português. */
    public final String displayName;
    /** Nome da textura no {@link com.slimefarm.assets.Assets}. */
    public final String texture;
    /** Categoria (comida, cristal ou slime). */
    public final Category category;
    /** Preço base de venda no mercado (0 se não for vendável). */
    public final int basePrice;
    /** Cor principal (usada em partículas e UI). */
    public final Color color;

    ItemType(String displayName, String texture, Category category, int basePrice, Color color) {
        this.displayName = displayName;
        this.texture = texture;
        this.category = category;
        this.basePrice = basePrice;
        this.color = color;
    }

    /** @return true se for comida para slimes */
    public boolean isFood() {
        return category == Category.FOOD;
    }

    /** @return true se for um cristal vendável */
    public boolean isCrystal() {
        return category == Category.CRYSTAL;
    }

    /** @return true se representar um slime capturado */
    public boolean isSlime() {
        return category == Category.SLIME;
    }
}
