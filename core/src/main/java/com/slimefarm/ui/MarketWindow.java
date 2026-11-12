package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.slimefarm.assets.Assets;
import com.slimefarm.economy.Market;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.inventory.Inventory;

/**
 * Janela interativa do "Mercado de Cristais" (aberta com a tecla E perto do terminal).
 * Exibe os preços do dia para todos os 6 tipos de cristais e permite vender individualmente
 * (teclas 1–6) ou vender todos os cristais do inventário (tecla E).
 */
public class MarketWindow {

    /** Lista dos 6 tipos de cristais vendáveis no mercado. */
    public static final ItemType[] CRYSTALS = {
            ItemType.CRYSTAL_PINK,
            ItemType.CRYSTAL_CAT,
            ItemType.CRYSTAL_ROCK,
            ItemType.CRYSTAL_HONEY,
            ItemType.CRYSTAL_PHOSPHOR,
            ItemType.CRYSTAL_GOLD
    };

    private boolean open;

    /** Alterna entre aberto e fechado. */
    public void toggle() {
        open = !open;
    }

    /** @param open define se a janela está visível */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /** @return true se a janela do mercado está aberta */
    public boolean isOpen() {
        return open;
    }

    /**
     * Desenha o painel do mercado centralizado na tela.
     *
     * @param shapes    renderer de formas
     * @param batch     batch de sprites
     * @param assets    repositório de texturas e fonte
     * @param market    sistema de mercado
     * @param inventory inventário do jogador
     * @param screenW   largura da tela
     * @param screenH   altura da tela
     */
    public void render(ShapeRenderer shapes, SpriteBatch batch, Assets assets,
                       Market market, Inventory inventory, float screenW, float screenH) {
        if (!open) {
            return;
        }
        float w = 440f;
        float h = 340f;
        float x = (screenW - w) / 2f;
        float y = (screenH - h) / 2f + 12f;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.08f, 0.14f, 0.22f, 0.94f);
        shapes.rect(x - 4f, y - 4f, w + 8f, h + 8f);
        shapes.setColor(0.16f, 0.28f, 0.42f, 0.96f);
        shapes.rect(x, y, w, h);
        shapes.setColor(0.29f, 0.78f, 0.90f, 1f);
        shapes.rect(x, y + h - 34f, w, 34f);

        for (int i = 0; i < CRYSTALS.length; i++) {
            float ry = y + h - 78f - i * 40f;
            shapes.setColor(0.11f, 0.20f, 0.31f, 1f);
            shapes.rect(x + 14f, ry, w - 28f, 34f);
        }
        shapes.end();

        BitmapFont font = assets.getFont();
        batch.begin();
        font.setColor(Color.valueOf("0E1B2A"));
        font.draw(batch, "MERCADO DE CRISTAIS  -  DIA " + market.getDay(), x + 16f, y + h - 11f);

        for (int i = 0; i < CRYSTALS.length; i++) {
            ItemType c = CRYSTALS[i];
            float ry = y + h - 78f - i * 40f;
            batch.draw(assets.get(c.texture), x + 22f, ry + 6f, 20f, 22f);
            font.setColor(Color.WHITE);
            int price = market.getPriceTable().getPrice(c);
            int owned = inventory.count(c);
            font.draw(batch, "[" + (i + 1) + "] " + c.displayName + "   Preco: $" + price + "   (Voce tem: " + owned + ")",
                    x + 50f, ry + 23f);
        }
        font.setColor(Color.valueOf("FFE07A"));
        font.draw(batch, "[E] Vender Tudo   |   [1-6] Vender 1   |   [ESC] Fechar", x + 18f, y + 22f);
        batch.end();
    }
}
