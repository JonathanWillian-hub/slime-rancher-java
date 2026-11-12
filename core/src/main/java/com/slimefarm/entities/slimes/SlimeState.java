package com.slimefarm.entities.slimes;

/**
 * Estados da máquina de estados (IA) de um {@link Slime}.
 */
public enum SlimeState {
    /** Parado esperando o próximo pulinho. */
    IDLE,
    /** Pulando em uma direção aleatória. */
    WANDER,
    /** Indo em direção a uma fruta/vegetal próximo. */
    SEEK_FOOD,
    /** Mastigando a comida recém-alcançada. */
    EAT,
    /** Satisfeito logo após comer e produzir cristal. */
    HAPPY,
    /** Muito faminto ou irritado. */
    AGITATED
}

