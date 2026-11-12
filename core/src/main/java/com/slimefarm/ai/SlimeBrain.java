package com.slimefarm.ai;

import com.badlogic.gdx.math.Vector2;
import com.slimefarm.entities.items.FoodItem;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.entities.slimes.SlimeState;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.World;

/**
 * Cérebro (máquina de estados) compartilhado pelos slimes.
 * Decide entre vagar, procurar comida, comer, ficar feliz ou agitado,
 * e aciona os pulinhos através do {@link HopMovement}.
 */
public class SlimeBrain {

    private static final float HOP_SPEED = 2.1f;
    private static final float EAT_RANGE = 0.55f;

    private final WanderBehavior wander = new WanderBehavior();
    private final SeekFoodBehavior seekFood = new SeekFoodBehavior();
    private final Vector2 tmpDir = new Vector2();

    /**
     * Atualiza a tomada de decisão do slime.
     *
     * @param slime slime controlado
     * @param world mundo
     * @param dt    tempo do quadro (s)
     */
    public void update(Slime slime, World world, float dt) {
        HopMovement hop = slime.getHop();

        // Se está no meio de um pulo, mantém a velocidade até tocar o chão
        if (hop.isAirborne(slime.getZ())) {
            return;
        }
        slime.getVelocity().setZero();

        // Quando faminto, tenta localizar comida próxima
        if (slime.isHungry()) {
            FoodItem food = seekFood.findNearestFood(slime, world);
            if (food != null) {
                if (slime.distanceTo(food) <= EAT_RANGE) {
                    slime.eat(food);
                    return;
                }
                slime.setState(SlimeState.SEEK_FOOD);
                if (hop.tryHop(slime.getZ(), 3.4f, RandomUtils.range(0.35f, 0.7f))) {
                    tmpDir.set(food.getPos()).sub(slime.getPos()).nor().scl(HOP_SPEED * 1.25f);
                    slime.getVelocity().set(tmpDir);
                }
                return;
            }
        }

        // Define estado emocional quando não está buscando comida
        if (slime.isAgitated()) {
            slime.setState(SlimeState.AGITATED);
        } else if (slime.getHappyTimer() > 0f) {
            slime.setState(SlimeState.HAPPY);
        } else {
            slime.setState(SlimeState.WANDER);
        }

        float cooldown = slime.isAgitated()
                ? RandomUtils.range(0.25f, 0.55f)
                : RandomUtils.range(0.6f, 1.4f);
        float jumpStrength = slime.isAgitated() ? 4.0f : 3.2f;
        if (hop.tryHop(slime.getZ(), jumpStrength, cooldown)) {
            wander.pickDirection(HOP_SPEED, slime.getVelocity());
        } else {
            slime.setState(SlimeState.IDLE);
        }
    }
}

