package com.slimefarm.systems;

import com.badlogic.gdx.math.Vector2;
import com.slimefarm.world.BiomeType;

/**
 * Representa uma área de nascimento de slimes selvagens associada a um {@link BiomeType}.
 * Guarda se a área esteve visível na câmera do jogador para que novos slimes sejam gerados
 * sempre que a área sair do campo de visão.
 */
public class SpawnZone {

    private final Vector2 center = new Vector2();
    private final float radius;
    private final BiomeType biome;
    private final int maxSlimes;
    private boolean wasInView = true;

    /**
     * @param centerX   coordenada x central (tiles)
     * @param centerY   coordenada y central (tiles)
     * @param radius    raio da zona (tiles)
     * @param biome     bioma desta zona
     * @param maxSlimes população máxima de slimes selvagens nesta zona
     */
    public SpawnZone(float centerX, float centerY, float radius, BiomeType biome, int maxSlimes) {
        this.center.set(centerX, centerY);
        this.radius = radius;
        this.biome = biome;
        this.maxSlimes = maxSlimes;
    }

    /** @return centro da zona em coordenadas de mundo */
    public Vector2 getCenter() {
        return center;
    }

    /** @return raio da zona em tiles */
    public float getRadius() {
        return radius;
    }

    /** @return bioma da zona */
    public BiomeType getBiome() {
        return biome;
    }

    /** @return quantidade máxima de slimes na zona */
    public int getMaxSlimes() {
        return maxSlimes;
    }

    /** @return true se a zona estava no campo de visão da câmera na última verificação */
    public boolean isWasInView() {
        return wasInView;
    }

    /** @param wasInView atualiza se a zona está no campo de visão */
    public void setWasInView(boolean wasInView) {
        this.wasInView = wasInView;
    }
}

