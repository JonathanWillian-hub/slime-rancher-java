package com.slimefarm.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.player.VacuumGun;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.BiomeType;
import com.slimefarm.world.Tile;
import com.slimefarm.world.World;

/**
 * Gerencia o nascimento de slimes selvagens por bioma e por campo de visão:
 * <ul>
 *   <li>Cada {@link SpawnZone} pertence a um {@link BiomeType};</li>
 *   <li>Sempre que o jogador caminha pelo mapa e uma zona <b>sai do campo de visão da câmera</b>,
 *       a zona é reabastecida com novos slimes;</li>
 *   <li>O tipo de slime sorteado respeita os biomas permitidos e a porcentagem de chance
 *       ({@link SlimeType#spawnChancePercent}) de cada espécie.</li>
 * </ul>
 */
public class SpawnSystem {

    private final Array<SpawnZone> zones = new Array<>();
    private final Array<Slime> zoneSlimes = new Array<>();
    private Rectangle corralBounds;

    /**
     * Registra uma nova zona de spawn no mundo.
     *
     * @param zone zona de spawn
     */
    public void addZone(SpawnZone zone) {
        zones.add(zone);
    }

    /**
     * @param corralBounds área do curral (para nunca gerar slimes selvagens dentro dele)
     */
    public void setCorralBounds(Rectangle corralBounds) {
        this.corralBounds = corralBounds;
    }

    /**
     * Popula todas as zonas inicialmente no começo da partida.
     *
     * @param world mundo
     */
    public void populateInitialZones(World world) {
        for (SpawnZone zone : zones) {
            replenishZone(world, zone);
        }
    }

    /**
     * Verifica quais zonas entraram ou saíram do campo de visão da câmera.
     * Quando uma zona sai do campo de visão ({@code wasInView -> !inView}), gera novos slimes nela
     * para que o jogador encontre novos slimes ao retornar.
     *
     * @param world  mundo
     * @param camera câmera atual do jogador
     */
    public void update(World world, OrthographicCamera camera) {
        float halfW = camera.viewportWidth * camera.zoom * 0.58f;
        float halfH = camera.viewportHeight * camera.zoom * 0.58f;

        for (SpawnZone zone : zones) {
            float sx = IsoUtils.toScreenX(zone.getCenter().x, zone.getCenter().y);
            float sy = IsoUtils.toScreenY(zone.getCenter().x, zone.getCenter().y, 0f);
            boolean inView = Math.abs(sx - camera.position.x) <= halfW
                    && Math.abs(sy - camera.position.y) <= halfH;

            if (zone.isWasInView() && !inView) {
                // Acabou de sair do campo de visão do jogador -> reabastece a área!
                replenishZone(world, zone);
            }
            zone.setWasInView(inView);
        }
    }

    /** Preenche uma zona até sua capacidade máxima usando as probabilidades do seu bioma. */
    private void replenishZone(World world, SpawnZone zone) {
        world.findInRadius(zone.getCenter(), zone.getRadius() + 2f, Slime.class, zoneSlimes);
        int current = 0;
        for (Slime s : zoneSlimes) {
            if (!s.isRemoved() && !s.isCorralled()) {
                current++;
            }
        }
        int toSpawn = zone.getMaxSlimes() - current;
        for (int i = 0; i < toSpawn; i++) {
            spawnOneInZone(world, zone);
        }
    }

    private void spawnOneInZone(World world, SpawnZone zone) {
        for (int attempt = 0; attempt < 12; attempt++) {
            float angle = RandomUtils.range(0f, 6.2831855f);
            float dist = RandomUtils.range(0.5f, zone.getRadius());
            float x = zone.getCenter().x + (float) Math.cos(angle) * dist;
            float y = zone.getCenter().y + (float) Math.sin(angle) * dist;

            if (corralBounds != null && corralBounds.contains(x, y)) {
                continue;
            }
            Tile t = world.getTileMap().getAt(x, y);
            if (t == null || !t.isWalkable() || t.getElevation() != 0 || world.getCollision().insideWall(x, y)) {
                continue;
            }
            SlimeType chosen = rollSlimeForBiome(zone.getBiome());
            world.add(VacuumGun.createSlime(chosen, x, y));
            return;
        }
    }

    /**
     * Sorteia um {@link SlimeType} que pode aparecer no bioma informado, ponderado pela
     * porcentagem {@link SlimeType#spawnChancePercent}.
     *
     * @param biome bioma onde o slime vai nascer
     * @return espécie sorteada
     */
    public static SlimeType rollSlimeForBiome(BiomeType biome) {
        int totalWeight = 0;
        for (SlimeType t : SlimeType.values()) {
            if (t.canSpawnIn(biome)) {
                totalWeight += t.spawnChancePercent;
            }
        }
        if (totalWeight <= 0) {
            return SlimeType.PINK;
        }
        int roll = RandomUtils.range(1, totalWeight);
        int acc = 0;
        for (SlimeType t : SlimeType.values()) {
            if (t.canSpawnIn(biome)) {
                acc += t.spawnChancePercent;
                if (roll <= acc) {
                    return t;
                }
            }
        }
        return SlimeType.PINK;
    }
}
