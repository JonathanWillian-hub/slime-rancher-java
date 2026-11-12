package com.slimefarm.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.slimefarm.assets.Assets;
import com.slimefarm.inventory.Inventory;
import com.slimefarm.player.PlayerStats;
import com.slimefarm.util.Constants;
import com.slimefarm.util.Direction;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.world.World;

/**
 * Entidade do jogador: guarda atributos ({@link PlayerStats}), direção de mira,
 * estado de animação e desenha o personagem com traje espacial e aspirador.
 */
public class Player extends LivingEntity {

    private final PlayerStats stats = new PlayerStats();
    private final Inventory inventory = new Inventory();
    private final Vector2 aimWorld = new Vector2();
    private Direction facing = Direction.SOUTH_EAST;
    private float walkTime;
    private boolean vacuuming;

    /**
     * @param x posição x inicial (tiles)
     * @param y posição y inicial (tiles)
     */
    public Player(float x, float y) {
        super(x, y, Constants.PLAYER_RADIUS);
        this.aimWorld.set(x + 1f, y);
    }

    @Override
    public void update(World world, float dt) {
        stats.update(dt);
        moveWithCollision(world, dt);
        if (!velocity.isZero()) {
            walkTime += dt * 8f;
        } else {
            walkTime = 0f;
        }
    }

    @Override
    public void render(SpriteBatch batch, Assets assets) {
        int frame = velocity.isZero() ? 0 : (1 + ((int) walkTime % 2));
        String key = (facing.isUp() ? "player_up_" : "player_down_") + frame;
        TextureRegion reg = assets.get(key);
        if (stats.isHurt() && ((int) (walkTime * 10f) % 2 == 0)) {
            batch.setColor(1f, 0.4f, 0.4f, 1f);
        }
        drawAnchored(batch, reg, facing.isLeft() ? -1f : 1f, 1f, 2f);
        batch.setColor(Color.WHITE);
    }

    /**
     * Atualiza o ponto de mira no mundo e calcula a direção visual ({@link Direction}).
     *
     * @param wx coordenada x no mundo
     * @param wy coordenada y no mundo
     */
    public void setAimWorld(float wx, float wy) {
        aimWorld.set(wx, wy);
        float sx = IsoUtils.toScreenX(wx - pos.x, wy - pos.y);
        float sy = IsoUtils.toScreenY(wx - pos.x, wy - pos.y, 0f);
        if (sx * sx + sy * sy > 0.01f) {
            facing = Direction.fromVector(sx, sy);
        }
    }

    /**
     * Calcula a posição da ponta do aspirador em coordenadas de mundo.
     *
     * @param out vetor de saída
     * @return o próprio {@code out}
     */
    public Vector2 getNozzleWorldPos(Vector2 out) {
        out.set(aimWorld).sub(pos);
        if (out.isZero()) {
            out.set(1f, 0f);
        } else {
            out.nor();
        }
        return out.scl(0.45f).add(pos);
    }

    /** @return atributos de vida e energia */
    public PlayerStats getStats() {
        return stats;
    }

    /** @return inventário (hotbar de 8 slots) */
    public Inventory getInventory() {
        return inventory;
    }

    /** @return ponto do mundo onde o mouse está mirando */
    public Vector2 getAimWorld() {
        return aimWorld;
    }

    /** @return direção visual atual */
    public Direction getFacing() {
        return facing;
    }

    /** @return true se está segurando o aspirador ligado */
    public boolean isVacuuming() {
        return vacuuming;
    }

    /** @param vacuuming estado do aspirador */
    public void setVacuuming(boolean vacuuming) {
        this.vacuuming = vacuuming;
    }
}
