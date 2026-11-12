package com.slimefarm.entities;

import com.badlogic.gdx.math.Vector2;
import com.slimefarm.world.World;

/**
 * Entidade viva que se move pelo mundo respeitando o {@link com.slimefarm.world.CollisionMap}.
 * Possui sombra no chão por padrão.
 */
public abstract class LivingEntity extends Entity {

    /** Velocidade atual no chão (tiles por segundo). */
    protected final Vector2 velocity = new Vector2();

    /**
     * @param x      x inicial
     * @param y      y inicial
     * @param radius raio de colisão
     */
    protected LivingEntity(float x, float y, float radius) {
        super(x, y, radius);
    }

    /**
     * Aplica {@link #velocity} usando o sistema de colisão do mundo.
     *
     * @param world mundo
     * @param dt    tempo do quadro (s)
     * @return true se bateu em algum obstáculo
     */
    protected boolean moveWithCollision(World world, float dt) {
        if (velocity.isZero()) {
            return false;
        }
        return world.getCollision().move(this, velocity.x * dt, velocity.y * dt);
    }

    @Override
    public boolean hasShadow() {
        return true;
    }

    /** @return vetor de velocidade (mutável) */
    public Vector2 getVelocity() {
        return velocity;
    }
}
