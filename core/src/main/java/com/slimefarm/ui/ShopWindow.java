package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.slimefarm.assets.Assets;
import com.slimefarm.economy.UpgradeShop;
import com.slimefarm.economy.UpgradeType;
import com.slimefarm.economy.Wallet;

/**
 * Janela interativa da Loja de Upgrades (aberta com a tecla E perto do {@link com.slimefarm.entities.props.ShopTerminal}
 * ou com a tecla U). Permite gastar moedas usando as teclas 1 a 5.
 */
public class ShopWindow {

    private boolean open;

    /** Alterna entre aberto e fechado. */
    public void toggle() {
        open = !open;
    }

    /** @param open define se a janela está visível */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /** @return true se a loja está aberta */
    public boolean isOpen() {
        return open;
    }

    /**
     * Desenha o catálogo de melhorias da loja.
     *
     * @param shapes  renderer de formas
     * @param batch   batch de sprites
     * @param assets  texturas e fonte
     * @param shop    sistema da loja de upgrades
     * @param wallet  carteira de moedas
     * @param screenW largura da tela virtual
     * @param screenH altura da tela virtual
     */
    public void render(ShapeRenderer shapes, SpriteBatch batch, Assets assets,
                       UpgradeShop shop, Wallet wallet, float screenW, float screenH) {
        if (!open) {
            return;
        }
        float w = 540f;
        float h = 320f;
        float x = (screenW - w) / 2f;
        float y = (screenH - h) / 2f + 10f;

        UpgradeType[] types = UpgradeType.values();

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.08f, 0.12f, 0.18f, 0.96f);
        shapes.rect(x - 4f, y - 4f, w + 8f, h + 8f);
        shapes.setColor(0.16f, 0.24f, 0.36f, 0.98f);
        shapes.rect(x, y, w, h);
        shapes.setColor(0.95f, 0.72f, 0.22f, 1f);
        shapes.rect(x, y + h - 36f, w, 36f);

        for (int i = 0; i < types.length; i++) {
            float ry = y + h - 84f - i * 46f;
            shapes.setColor(0.11f, 0.18f, 0.28f, 1f);
            shapes.rect(x + 14f, ry, w - 28f, 38f);
        }
        shapes.end();

        BitmapFont font = assets.getFont();
        batch.begin();
        font.setColor(Color.valueOf("1B1E26"));
        font.draw(batch, "LOJA DE UPGRADES  -  SUAS MOEDAS: $" + wallet.getCoins(), x + 16f, y + h - 12f);

        for (int i = 0; i < types.length; i++) {
            UpgradeType u = types[i];
            int lv = shop.getLevel(u);
            float ry = y + h - 84f - i * 46f;

            font.setColor(Color.valueOf("FFE580"));
            font.draw(batch, "[" + (i + 1) + "] " + u.displayName + "  (Nivel " + lv + "/" + u.maxLevel + ")",
                    x + 22f, ry + 32f);

            font.setColor(Color.WHITE);
            font.draw(batch, u.description, x + 22f, ry + 14f);

            if (lv >= u.maxLevel) {
                font.setColor(Color.valueOf("72F2FF"));
                font.draw(batch, "MAXIMO", x + w - 95f, ry + 24f);
            } else {
                int cost = u.getCostForNextLevel(lv);
                font.setColor(wallet.getCoins() >= cost ? Color.valueOf("7DFF7A") : Color.valueOf("FF6B6B"));
                font.draw(batch, "Comprar: $" + cost, x + w - 135f, ry + 24f);
            }
        }
        font.setColor(Color.valueOf("FFE07A"));
        font.draw(batch, "Pressione [1-5] para comprar upgrade   |   [ESC / U] Fechar", x + 18f, y + 20f);
        batch.end();
    }
}
