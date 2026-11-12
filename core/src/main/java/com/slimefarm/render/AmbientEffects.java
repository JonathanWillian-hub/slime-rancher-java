package com.slimefarm.render;

import com.badlogic.gdx.graphics.Color;
import com.slimefarm.entities.Entity;
import com.slimefarm.entities.items.CrystalItem;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.World;

/**
 * Gera partículas douradas suaves pelo cenário (como os pontinhos brilhantes da imagem
 * de referência) e pequenos brilhos subindo dos cristais soltos no mundo.
 */
public class AmbientEffects {

    private static final Color SPARKLE_GOLD = new Color(1f, 0.92f, 0.52f, 0.85f);
    private float timer;

    /**
     * Atualiza a emissão periódica de brilhos no ambiente e nos cristais.
     *
     * @param world     mundo
     * @param particles sistema de partículas
     * @param dt        tempo do quadro (s)
     */
    public void update(World world, ParticleSystem particles, float dt) {
        timer -= dt;
        if (timer > 0f) {
            return;
        }
        timer = 0.14f;

        // Pontinho dourado flutuante em uma posição aleatória da praça/gramado
        float wx = RandomUtils.range(8f, 32f);
        float wy = RandomUtils.range(8f, 30f);
        float sx = IsoUtils.toScreenX(wx, wy);
        float sy = IsoUtils.toScreenY(wx, wy, world.getElevationAt(wx, wy)) + RandomUtils.range(4f, 18f);
        particles.spawn(sx, sy, RandomUtils.range(-4f, 4f), RandomUtils.range(5f, 12f),
                3.2f, 1.4f, SPARKLE_GOLD);

        // Brilhos nos cristais soltos no chão
        for (Entity e : world.getEntities()) {
            if (e instanceof CrystalItem c && !c.isRemoved() && RandomUtils.chance(0.35f)) {
                float cx = IsoUtils.toScreenX(c.getPos().x, c.getPos().y) + RandomUtils.range(-5f, 5f);
                float cy = IsoUtils.toScreenY(c.getPos().x, c.getPos().y, c.getGroundZ() + c.getZ()) + 8f;
                particles.spawn(cx, cy, 0f, 14f, 3f, 0.6f, Color.WHITE);
            }
        }
    }
}

