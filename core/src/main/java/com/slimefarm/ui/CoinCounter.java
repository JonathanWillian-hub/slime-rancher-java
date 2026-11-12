package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.slimefarm.economy.Wallet;

/**
 * Exibe o ícone de moeda dourada e o saldo numérico da {@link Wallet}
 * abaixo das barras de vida e energia.
 */
public class CoinCounter {

    /**
     * Desenha o ícone e o valor das moedas.
     *
     * @param batch  batch ativo
     * @param font   fonte
     * @param coin   textura da moeda
     * @param wallet carteira do jogador
     * @param x      posição x
     * @param y      posição y
     */
    public void render(SpriteBatch batch, BitmapFont font, TextureRegion coin, Wallet wallet, float x, float y) {
        batch.draw(coin, x, y - 16f, 20f, 20f);
        String text = Integer.toString(wallet.getCoins());
        font.setColor(Color.valueOf("1B1E26"));
        font.draw(batch, text, x + 26f, y - 1f);
        font.setColor(Color.valueOf("FFE580"));
        font.draw(batch, text, x + 25f, y);
    }
}

