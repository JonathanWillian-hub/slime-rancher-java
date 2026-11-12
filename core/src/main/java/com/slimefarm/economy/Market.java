package com.slimefarm.economy;

import com.slimefarm.entities.items.ItemType;
import com.slimefarm.inventory.Inventory;
import com.slimefarm.util.Constants;

/**
 * Mercado de Cristais: controla a passagem dos dias, atualiza a {@link PriceTable}
 * e realiza vendas transferindo o valor para a {@link Wallet} do jogador.
 */
public class Market {

    private final Wallet wallet;
    private final PriceTable priceTable = new PriceTable();
    private float dayTimer;
    private int day = 1;

    /** @param wallet carteira do jogador */
    public Market(Wallet wallet) {
        this.wallet = wallet;
    }

    /**
     * Avança o relógio do mercado e gira os preços quando um novo dia começa.
     *
     * @param dt tempo do quadro (s)
     */
    public void update(float dt) {
        dayTimer += dt;
        if (dayTimer >= Constants.DAY_LENGTH) {
            dayTimer -= Constants.DAY_LENGTH;
            day++;
            priceTable.rollDailyPrices();
        }
    }

    /**
     * Vende 1 unidade de um cristal diretamente (ex.: cristal atirado contra o terminal).
     *
     * @param crystalType tipo de cristal
     * @return moedas ganhas (0 se o item não for cristal)
     */
    public int sellOne(ItemType crystalType) {
        if (crystalType == null || !crystalType.isCrystal()) {
            return 0;
        }
        int value = priceTable.getPrice(crystalType);
        wallet.add(value);
        return value;
    }

    /**
     * Vende 1 unidade de um cristal retirando-o do inventário do jogador.
     *
     * @param inventory   inventário
     * @param crystalType tipo de cristal a vender
     * @return true se o jogador tinha o cristal e a venda foi concluída
     */
    public boolean sellFromInventory(Inventory inventory, ItemType crystalType) {
        if (crystalType == null || !crystalType.isCrystal()) {
            return false;
        }
        if (inventory.removeOneOf(crystalType)) {
            sellOne(crystalType);
            return true;
        }
        return false;
    }

    /**
     * Vende todos os cristais presentes no inventário de uma só vez.
     *
     * @param inventory inventário do jogador
     * @return total de moedas ganhas
     */
    public int sellAllFromInventory(Inventory inventory) {
        int earned = 0;
        for (ItemType t : ItemType.values()) {
            if (t.isCrystal()) {
                while (inventory.removeOneOf(t)) {
                    earned += sellOne(t);
                }
            }
        }
        return earned;
    }

    /** @return tabela de preços atuais */
    public PriceTable getPriceTable() {
        return priceTable;
    }

    /** @return carteira associada */
    public Wallet getWallet() {
        return wallet;
    }

    /** @return número do dia atual (começa em 1) */
    public int getDay() {
        return day;
    }

    /** @param day define o dia atual (usado ao carregar save) */
    public void setDay(int day) {
        this.day = Math.max(1, day);
    }
}

