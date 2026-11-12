package com.slimefarm.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.slimefarm.assets.Assets;
import com.slimefarm.inventory.Inventory;

/**
 * Barra inferior de 8 slots ({@link HotbarSlotView}) centralizada na base da tela,
 * fiel ao layout da imagem de referência.
 */
public class Hotbar {

    private static final float GAP = 6f;
    private static final float PADDING = 8f;
    private final HotbarSlotView slotView = new HotbarSlotView();

    /**
     * Desenha a moldura da hotbar e todos os 8 slots.
     *
     * @param shapes    renderer de formas
     * @param batch     batch de sprites
     * @param assets    texturas e fonte
     * @param inventory inventário do jogador
     * @param screenW   largura da tela virtual
     */
    public void render(ShapeRenderer shapes, SpriteBatch batch, Assets assets, Inventory inventory, float screenW) {
        int n = inventory.size();
        float totalW = n * HotbarSlotView.SIZE + (n - 1) * GAP + PADDING * 2f;
        float totalH = HotbarSlotView.SIZE + PADDING * 2f;
        float x0 = (screenW - totalW) / 2f;
        float y0 = 10f;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.08f, 0.12f, 0.18f, 0.94f);
        shapes.rect(x0 - 3f, y0 - 3f, totalW + 6f, totalH + 6f);
        shapes.setColor(0.20f, 0.29f, 0.40f, 0.96f);
        shapes.rect(x0, y0, totalW, totalH);

        for (int i = 0; i < n; i++) {
            float sx = x0 + PADDING + i * (HotbarSlotView.SIZE + GAP);
            float sy = y0 + PADDING;
            slotView.renderBox(shapes, sx, sy, i == inventory.getSelectedIndex());
        }
        shapes.end();

        batch.begin();
        for (int i = 0; i < n; i++) {
            float sx = x0 + PADDING + i * (HotbarSlotView.SIZE + GAP);
            float sy = y0 + PADDING;
            slotView.renderContent(batch, assets, inventory.getSlot(i), sx, sy);
        }
        batch.end();
    }
}

