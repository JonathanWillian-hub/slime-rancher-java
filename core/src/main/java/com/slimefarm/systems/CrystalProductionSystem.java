package com.slimefarm.systems;

import com.slimefarm.entities.Entity;
import com.slimefarm.entities.items.CrystalItem;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.World;

/**
 * Sistema que verifica slimes que terminaram a digestão ({@link Slime#pollCrystalReady()})
 * e gera a entidade {@link CrystalItem} correspondente no mundo.
 */
public class CrystalProductionSystem {

    /**
     * Percorre os slimes do mundo e gera cristais prontos.
     *
     * @param world mundo
     */
    public void update(World world) {
        for (Entity e : world.getEntities()) {
            if (e instanceof Slime slime && !slime.isRemoved() && slime.pollCrystalReady()) {
                CrystalItem crystal = new CrystalItem(
                        slime.getPos().x,
                        slime.getPos().y,
                        slime.getType().crystalItem
                );
                float angle = RandomUtils.range(0f, 6.283f);
                crystal.launch((float) Math.cos(angle) * 1.6f, (float) Math.sin(angle) * 1.6f, 3.2f, 0.25f);
                world.add(crystal);
            }
        }
    }
}

