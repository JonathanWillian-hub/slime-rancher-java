package com.slimefarm.economy;

import com.slimefarm.entities.Player;
import com.slimefarm.util.Constants;

import java.util.EnumMap;

/**
 * Gerencia os níveis das melhorias compradas pelo jogador na Loja de Upgrades,
 * descontando moedas da {@link Wallet} e aplicando os bônus no {@link Player}.
 */
public class UpgradeShop {

    private final Wallet wallet;
    private final EnumMap<UpgradeType, Integer> levels = new EnumMap<>(UpgradeType.class);

    /** @param wallet carteira do jogador */
    public UpgradeShop(Wallet wallet) {
        this.wallet = wallet;
        for (UpgradeType t : UpgradeType.values()) {
            levels.put(t, 0);
        }
    }

    /**
     * Tenta comprar o próximo nível de uma melhoria e aplica o efeito imediatamente no jogador.
     *
     * @param type   melhoria desejada
     * @param player jogador que receberá o bônus
     * @return true se tinha moedas suficientes e ainda não estava no nível máximo
     */
    public boolean buy(UpgradeType type, Player player) {
        int current = getLevel(type);
        if (current >= type.maxLevel) {
            return false;
        }
        int cost = type.getCostForNextLevel(current);
        if (!wallet.spend(cost)) {
            return false;
        }
        levels.put(type, current + 1);
        applyAll(player);
        return true;
    }

    /**
     * Recalcula e aplica todos os bônus de todos os upgrades comprados no jogador e inventário.
     *
     * @param player jogador
     */
    public void applyAll(Player player) {
        int energyLv = getLevel(UpgradeType.ENERGY_TANK);
        int bootsLv = getLevel(UpgradeType.JET_BOOTS);
        int vacLv = getLevel(UpgradeType.VACUUM_BOOSTER);
        int slotLv = getLevel(UpgradeType.BACKPACK_SLOT);
        int armorLv = getLevel(UpgradeType.SUIT_ARMOR);

        player.getStats().setMaxEnergy(Constants.MAX_ENERGY + energyLv * 25f);
        player.getStats().setSpeedMultiplier(1f + bootsLv * 0.15f);
        player.getStats().setVacuumRangeBonus(vacLv * 0.8f);
        player.getStats().setVacuumPullMultiplier(1f + vacLv * 0.25f);
        player.getStats().setMaxHealth(Constants.MAX_HEALTH + armorLv * 25f);
        player.getInventory().setSlotMaxCapacity(Constants.SLOT_MAX + slotLv * 10);
    }

    /** @return nível atual comprado de um upgrade (0..maxLevel) */
    public int getLevel(UpgradeType type) {
        return levels.getOrDefault(type, 0);
    }

    /** Define diretamente o nível de um upgrade (usado ao carregar save). */
    public void setLevel(UpgradeType type, int level) {
        levels.put(type, Math.max(0, Math.min(type.maxLevel, level)));
    }
}
