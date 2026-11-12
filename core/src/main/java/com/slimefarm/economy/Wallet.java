package com.slimefarm.economy;

import com.slimefarm.util.Constants;

/**
 * Carteira de moedas do jogador. Começa com {@link Constants#START_COINS} ("10050",
 * igual à imagem de referência).
 */
public class Wallet {

    private int coins = Constants.START_COINS;

    /**
     * Adiciona moedas à carteira.
     *
     * @param amount valor positivo a somar
     */
    public void add(int amount) {
        if (amount > 0) {
            coins += amount;
        }
    }

    /**
     * Tenta gastar moedas.
     *
     * @param amount valor a gastar
     * @return true se tinha saldo suficiente
     */
    public boolean spend(int amount) {
        if (amount < 0 || coins < amount) {
            return false;
        }
        coins -= amount;
        return true;
    }

    /**
     * Aplica penalidade percentual (ex.: 10% ao desmaiar).
     *
     * @param fraction fração entre 0 e 1
     */
    public void loseFraction(float fraction) {
        int lost = Math.round(coins * fraction);
        coins = Math.max(0, coins - lost);
    }

    /** @param coins define o saldo diretamente (usado no carregamento de save) */
    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    /** @return saldo atual de moedas */
    public int getCoins() {
        return coins;
    }
}

