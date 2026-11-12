package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.slimefarm.assets.Assets;
import com.slimefarm.inventory.InventorySlot;

/**
 * Desenha um único slot da hotbar (fundo escuro, borda cinza ou amarela quando selecionado,
 * ícone do item centralizado e quantidade no canto inferior direito).
 */
public class HotbarSlotView {

    /** Tamanho lateral do slot em pixels. */
    public static final float SIZE = 48f;

    /**
     * Desenha o fundo e a borda do slot.
     *
     * @param shapes   renderer de formas (Filled)
     * @param x        x do slot
     * @param y        y do slot
     * @param selected true se este é o slot selecionado (borda amarela)
     */
    public void renderBox(ShapeRenderer shapes, float x, float y, boolean selected) {
        if (selected) {
            shapes.setColor(Color.valueOf("F7D24C"));
        } else {
            shapes.setColor(0.36f, 0.45f, 0.56f, 1f);
        }
        shapes.rect(x, y, SIZE, SIZE);
        shapes.setColor(0.15f, 0.21f, 0.30f, 1f);
        shapes.rect(x + 3f, y + 3f, SIZE - 6f, SIZE - 6f);
    }

    /**
     * Desenha o ícone do item e o contador numérico.
     *
     * @param batch  batch ativo
     * @param assets texturas e fonte
     * @param slot   dados do slot
     * @param x      x do slot
     * @param y      y do slot
     */
    public void renderContent(SpriteBatch batch, Assets assets, InventorySlot slot, float x, float y) {
        if (slot.isEmpty()) {
            return;
        }
        TextureRegion reg = assets.get(slot.getType().texture);
        batch.draw(reg, x + 8f, y + 10f, 32f, 32f);

        BitmapFont font = assets.getFont();
        String qty = Integer.toString(slot.getCount());
        font.setColor(Color.BLACK);
        font.draw(batch, qty, x + SIZE - 14f, y + 15f);
        font.setColor(Color.WHITE);
        font.draw(batch, qty, x + SIZE - 15f, y + 16f);
    }
}

