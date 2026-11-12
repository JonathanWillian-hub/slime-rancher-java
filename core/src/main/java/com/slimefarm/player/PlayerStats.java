package com.slimefarm.player;

import com.slimefarm.util.Constants;

/**
 * Vida, energia e multiplicadores de upgrade do jogador, com regeneração automática
 * e tempo de invencibilidade curto após levar dano.
 */
public class PlayerStats {

    private float maxHealth = Constants.MAX_HEALTH;
    private float maxEnergy = Constants.MAX_ENERGY;
    private float health = maxHealth;
    private float energy = maxEnergy;
    private float speedMultiplier = 1f;
    private float vacuumRangeBonus = 0f;
    private float vacuumPullMultiplier = 1f;
    private float invulnerableTimer;
    private float energyRegenDelay;

    /**
     * Atualiza regeneração de vida/energia e timers.
     *
     * @param dt tempo do quadro (s)
     */
    public void update(float dt) {
        if (invulnerableTimer > 0) {
            invulnerableTimer -= dt;
        }
        if (energyRegenDelay > 0) {
            energyRegenDelay -= dt;
        } else {
            energy = Math.min(maxEnergy, energy + Constants.ENERGY_REGEN * dt);
        }
        health = Math.min(maxHealth, health + Constants.HEALTH_REGEN * dt);
    }

    /**
     * Tenta gastar energia. Se tiver o suficiente, desconta e pausa a regeneração por um instante.
     *
     * @param amount quantidade a gastar
     * @return true se havia energia suficiente
     */
    public boolean consumeEnergy(float amount) {
        if (energy < amount) {
            return false;
        }
        energy -= amount;
        energyRegenDelay = 0.6f;
        return true;
    }

    /**
     * Aplica dano ao jogador (ignorado se ainda estiver invulnerável).
     *
     * @param amount dano
     * @return true se o dano foi aplicado
     */
    public boolean damage(float amount) {
        if (invulnerableTimer > 0) {
            return false;
        }
        health = Math.max(0f, health - amount);
        invulnerableTimer = 0.8f;
        return true;
    }

    /** Restaura vida e energia ao máximo (usado no respawn). */
    public void restoreFull() {
        health = maxHealth;
        energy = maxEnergy;
    }

    /** @param maxHealth define a vida máxima (e cura a diferença) */
    public void setMaxHealth(float maxHealth) {
        float diff = maxHealth - this.maxHealth;
        this.maxHealth = maxHealth;
        if (diff > 0) {
            this.health = Math.min(maxHealth, this.health + diff);
        }
    }

    /** @param maxEnergy define a energia máxima (e recarrega a diferença) */
    public void setMaxEnergy(float maxEnergy) {
        float diff = maxEnergy - this.maxEnergy;
        this.maxEnergy = maxEnergy;
        if (diff > 0) {
            this.energy = Math.min(maxEnergy, this.energy + diff);
        }
    }

    /** @param speedMultiplier multiplicador de velocidade (ex.: 1.15f) */
    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    /** @return multiplicador de velocidade atual */
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    /** @param bonus bônus de alcance do aspirador em tiles */
    public void setVacuumRangeBonus(float bonus) {
        this.vacuumRangeBonus = bonus;
    }

    /** @return bônus de alcance do aspirador em tiles */
    public float getVacuumRangeBonus() {
        return vacuumRangeBonus;
    }

    /** @param mult multiplicador de força de sucção */
    public void setVacuumPullMultiplier(float mult) {
        this.vacuumPullMultiplier = mult;
    }

    /** @return multiplicador de força de sucção */
    public float getVacuumPullMultiplier() {
        return vacuumPullMultiplier;
    }

    /** @return true se a vida chegou a zero */
    public boolean isDead() {
        return health <= 0f;
    }

    /** @return true se acabou de levar dano (pisca o sprite) */
    public boolean isHurt() {
        return invulnerableTimer > 0f;
    }

    /** @return vida atual */
    public float getHealth() {
        return health;
    }

    /** @return energia atual */
    public float getEnergy() {
        return energy;
    }

    /** @return fração de vida (0..1) */
    public float getHealthRatio() {
        return health / maxHealth;
    }

    /** @return fração de energia (0..1) */
    public float getEnergyRatio() {
        return energy / maxEnergy;
    }
}
