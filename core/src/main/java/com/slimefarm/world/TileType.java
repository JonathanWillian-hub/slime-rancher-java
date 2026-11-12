package com.slimefarm.world;

/**
 * Tipos de piso do mapa, incluindo pisos específicos dos biomas
 * (grama de musgo da floresta e rocha vulcânica/mineral da pedreira).
 */
public enum TileType {
    GRASS("tile_grass", true),
    GRASS_DARK("tile_grass_dark", true),
    DIRT("tile_dirt", true),
    STONE_PATH("tile_stone", true),
    BRIDGE("tile_bridge", true),
    MOSS("tile_moss", true),
    QUARRY_ROCK("tile_quarry", true),
    CLIFF("tile_grass_dark", false),
    WATER("tile_water", false);

    /** Prefixo do nome da textura (o Tile acrescenta "_" + variação). */
    public final String texture;
    /** Se o jogador e os slimes podem andar sobre o tile. */
    public final boolean walkable;

    TileType(String texture, boolean walkable) {
        this.texture = texture;
        this.walkable = walkable;
    }
}
