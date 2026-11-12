package com.slimefarm.entities.props;

/**
 * Terminal da Loja de Upgrades (canto superior esquerdo do Rancho Central).
 * Quando o jogador se aproxima e pressiona E, abre a {@link com.slimefarm.ui.ShopWindow}
 * para gastar moedas em melhorias do traje, aspirador e mochila.
 */
public class ShopTerminal extends Prop {

    /**
     * @param x posição x (tiles)
     * @param y posição y (tiles)
     */
    public ShopTerminal(float x, float y) {
        super(x, y, 0.85f, true, "shop_terminal");
        this.drawOffsetY = 4f;
    }
}
