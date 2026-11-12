package com.slimefarm.entities.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.slimefarm.assets.Assets;
import com.slimefarm.entities.LivingEntity;
import com.slimefarm.util.Constants;
import com.slimefarm.world.World;

/**
 * Item solto no chão (fruta, vegetal ou cristal).
 * Possui física leve de quicar (eixo z), flutuação suave e pode ser puxado pelo aspirador.
 */
public class ItemEntity extends LivingEntity {

    private final ItemType type;
    private float vz;
    private float age;
    private float pickupCooldown;

    /**
     * @param x    posição x inicial
     * @param y    posição y inicial
     * @param type tipo do item
     */
    public ItemEntity(float x, float y, ItemType type) {
        super(x, y, 0.2f);
        this.type = type;
        this.z = 0.25f;
        this.vz = 2.5f;
    }

    /**
     * Lança o item em uma direção (ex.: quando atirado pelo aspirador ou expelido por um slime).
     *
     * @param vx velocidade x
     * @param vy velocidade y
     * @param vz velocidade vertical inicial
     * @param cooldown tempo sem poder ser aspirado de volta (s)
     */
    public void launch(float vx, float vy, float vz, float cooldown) {
        this.velocity.set(vx, vy);
        this.vz = vz;
        this.pickupCooldown = cooldown;
    }

    @Override
    public void update(World world, float dt) {
        age += dt;
        if (pickupCooldown > 0f) {
            pickupCooldown -= dt;
        }
        // Gravidade e quique no eixo Z
        vz -= Constants.GRAVITY * dt;
        z += vz * dt;
        if (z <= 0f) {
            z = 0f;
            if (Math.abs(vz) > 1.2f) {
                vz = -vz * 0.45f;
            } else {
                vz = 0f;
            }
        }
        // Movimento horizontal com atrito
        if (!velocity.isZero()) {
            boolean hit = moveWithCollision(world, dt);
            if (hit) {
                velocity.scl(-0.4f);
            }
            float damping = (z <= 0.01f) ? 6f : 1.5f;
            velocity.scl(Math.max(0f, 1f - damping * dt));
            if (velocity.len2() < 0.01f) {
                velocity.setZero();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, Assets assets) {
        float bob = (z <= 0.02f) ? (MathUtils.sin(age * 3.5f) + 1f) * 0.04f : 0f;
        float savedZ = z;
        z += bob;
        drawAnchored(batch, assets.get(type.texture), 1f, 1f, 2f);
        z = savedZ;
    }

    /** @return true se já pode ser puxado/coletado pelo aspirador */
    public boolean canBeVacuumed() {
        return pickupCooldown <= 0f && !isRemoved();
    }

    /**
     * Aplica força de atração em direção a um ponto (usado pelo aspirador).
     *
     * @param target ponto alvo (bico do aspirador)
     * @param accel  aceleração (tiles/s²)
     * @param dt     tempo do quadro
     */
    public void pullTowards(Vector2 target, float accel, float dt) {
        float dx = target.x - pos.x;
        float dy = target.y - pos.y;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0.001f) {
            velocity.x += (dx / len) * accel * dt;
            velocity.y += (dy / len) * accel * dt;
        }
    }

    /** @return tipo do item */
    public ItemType getType() {
        return type;
    }
}

