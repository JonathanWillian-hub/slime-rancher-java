package com.slimefarm.world;

/**
 * Grade retangular de {@link Tile}s que forma o chão do mundo.
 */
public class TileMap {

    private final int width;
    private final int height;
    private final Tile[][] tiles;

    /**
     * Cria um mapa preenchido com grama.
     *
     * @param width  largura em tiles
     * @param height altura em tiles
     */
    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(TileType.GRASS, 0, (x * 7 + y * 13) % Tile.VARIANTS);
            }
        }
    }

    /** @return true se (x, y) está dentro do mapa */
    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    /** @return tile em (x, y) ou null se estiver fora do mapa */
    public Tile get(int x, int y) {
        return inBounds(x, y) ? tiles[x][y] : null;
    }

    /** @return tile que contém o ponto de mundo (wx, wy), ou null */
    public Tile getAt(float wx, float wy) {
        return get((int) Math.floor(wx), (int) Math.floor(wy));
    }

    /**
     * Altera tipo e elevação de um tile (ignora posições fora do mapa).
     *
     * @param x         coluna
     * @param y         linha
     * @param type      novo tipo
     * @param elevation nova elevação
     */
    public void set(int x, int y, TileType type, int elevation) {
        Tile t = get(x, y);
        if (t != null) {
            t.setType(type);
            t.setElevation(elevation);
        }
    }

    /** Altera apenas o tipo de um tile. */
    public void setType(int x, int y, TileType type) {
        Tile t = get(x, y);
        if (t != null) {
            t.setType(type);
        }
    }

    /** Define o bioma de um tile. */
    public void setBiome(int x, int y, BiomeType biome) {
        Tile t = get(x, y);
        if (t != null) {
            t.setBiome(biome);
        }
    }

    /** @return bioma no ponto de mundo (wx, wy) */
    public BiomeType getBiomeAt(float wx, float wy) {
        Tile t = getAt(wx, wy);
        return t != null ? t.getBiome() : BiomeType.RANCH;
    }

    /** @return largura em tiles */
    public int getWidth() {
        return width;
    }

    /** @return altura em tiles */
    public int getHeight() {
        return height;
    }
}

