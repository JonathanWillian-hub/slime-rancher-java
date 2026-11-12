package com.slimefarm.world;

/**
 * Uma célula do mapa: tipo de piso, bioma ao qual pertence, elevação e variação visual.
 */
public class Tile {

    /** Quantidade de variações visuais por tipo de tile. */
    public static final int VARIANTS = 3;

    private TileType type;
    private BiomeType biome = BiomeType.RANCH;
    private int elevation;
    private final int variant;

    /**
     * @param type      tipo de piso
     * @param elevation altura em níveis (0 = chão, 1 = planalto do penhasco)
     * @param variant   variação visual (0..VARIANTS-1)
     */
    public Tile(TileType type, int elevation, int variant) {
        this.type = type;
        this.elevation = elevation;
        this.variant = variant;
    }

    /** @return tipo de piso */
    public TileType getType() {
        return type;
    }

    /** @param type novo tipo de piso */
    public void setType(TileType type) {
        this.type = type;
    }

    /** @return bioma ao qual este tile pertence */
    public BiomeType getBiome() {
        return biome;
    }

    /** @param biome define o bioma deste tile */
    public void setBiome(BiomeType biome) {
        this.biome = biome;
    }

    /** @return elevação em níveis */
    public int getElevation() {
        return elevation;
    }

    /** @param elevation nova elevação */
    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    /** @return variação visual */
    public int getVariant() {
        return variant;
    }

    /** @return nome da textura deste tile no {@link com.slimefarm.assets.Assets} */
    public String getTextureName() {
        return type.texture + "_" + variant;
    }

    /** @return true se é possível andar sobre o tile (ignorando elevação) */
    public boolean isWalkable() {
        return type.walkable;
    }
}
