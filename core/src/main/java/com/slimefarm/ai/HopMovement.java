package com.slimefarm.ai;

import com.slimefarm.util.Constants;

/**
 * Controla o movimento por "pulinhos" (eixo Z) e o efeito elástico
 * de achatar/esticar (squash & stretch) dos slimes.
 */
public class HopMovement {

    private float vz;
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float hopCooldown = 0.6f;

    /**
     * Atualiza a altura do pulo e a deformação elástica.
     *
     * @param currentZ altura atual da entidade
     * @param dt       tempo do quadro (s)
     * @return nova altura {@code z}
     */
    public float update(float currentZ, float dt) {
        float z = currentZ;
        if (z > 0f || vz > 0f) {
            vz -= Constants.GRAVITY * dt;
            z += vz * dt;
            if (z <= 0f) {
                z = 0f;
                vz = 0f;
                // Achatamento ao tocar o chão
                scaleX = 1.22f;
                scaleY = 0.80f;
            } else {
                // Esticado no ar
                scaleX = 0.92f;
                scaleY = 1.10f;
            }
        } else {
            hopCooldown = Math.max(0f, hopCooldown - dt);
            scaleX += (1f - scaleX) * Math.min(1f, 10f * dt);
            scaleY += (1f - scaleY) * Math.min(1f, 10f * dt);
        }
        return z;
    }

    /**
     * Inicia um pulo se estiver no chão e o tempo de espera tiver terminado.
     *
     * @param currentZ     altura atual
     * @param jumpStrength velocidade vertical inicial
     * @param nextCooldown espera após pousar até o próximo pulo
     * @return true se iniciou o pulo
     */
    public boolean tryHop(float currentZ, float jumpStrength, float nextCooldown) {
        if (currentZ <= 0.001f && vz <= 0f && hopCooldown <= 0f) {
            vz = jumpStrength;
            hopCooldown = nextCooldown;
            scaleX = 0.88f;
            scaleY = 1.15f;
            return true;
        }
        return false;
    }

    /** Força um pulo imediato (ex.: ao ser lançado pelo aspirador). */
    public void forceHop(float jumpStrength) {
        vz = jumpStrength;
    }

    /** @return true se o slime está no ar */
    public boolean isAirborne(float currentZ) {
        return currentZ > 0.01f || vz > 0f;
    }

    /** @return escala horizontal atual (squash & stretch) */
    public float getScaleX() {
        return scaleX;
    }

    /** @return escala vertical atual (squash & stretch) */
    public float getScaleY() {
        return scaleY;
    }
}

