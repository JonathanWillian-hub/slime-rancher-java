package com.slimefarm.economy;

/**
 * Tipos de melhorias (upgrades) que o jogador pode comprar na Loja de Upgrades
 * usando as moedas ganhas com a venda de cristais.
 */
public enum UpgradeType {
    ENERGY_TANK("Tanque de Energia", "+25 Energia Maxima por nivel", 250, 4),
    JET_BOOTS("Botas Turbo", "+15% Velocidade de Movimento por nivel", 300, 4),
    VACUUM_BOOSTER("Bico Turbo do Aspirador", "+0.8 Alcance e +25% Succao por nivel", 350, 4),
    BACKPACK_SLOT("Compartimento Expandido", "+10 Capacidade por Slot da Hotbar", 400, 4),
    SUIT_ARMOR("Blindagem do Traje", "+25 Vida Maxima por nivel", 250, 4);

    /** Nome legível da melhoria. */
    public final String displayName;
    /** Descrição do efeito por nível. */
    public final String description;
    /** Custo em moedas do nível 1. */
    public final int baseCost;
    /** Nível máximo que pode ser comprado. */
    public final int maxLevel;

    UpgradeType(String displayName, String description, int baseCost, int maxLevel) {
        this.displayName = displayName;
        this.description = description;
        this.baseCost = baseCost;
        this.maxLevel = maxLevel;
    }

    /**
     * Calcula o custo para comprar o próximo nível a partir do nível atual.
     *
     * @param currentLevel nível atual (0..maxLevel-1)
     * @return preço em moedas
     */
    public int getCostForNextLevel(int currentLevel) {
        return baseCost * (currentLevel + 1);
    }
}
