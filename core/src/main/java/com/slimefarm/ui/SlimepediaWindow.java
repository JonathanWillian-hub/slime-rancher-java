package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.slimefarm.assets.Assets;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.progression.Slimepedia;

/**
 * Janela da Slimepedia (estilo Pokédex de Slimes), aberta pelo botão no HUD ou tecla P/Tab.
 * <ul>
 *   <li>Slimes <b>já capturados</b>: exibe sprite colorido, nome, comida favorita, cristal,
 *       porcentagem de spawn e biomas;</li>
 *   <li>Slimes <b>ainda não capturados</b>: exibe a silhueta em preto ({@code Color.BLACK})
 *       e mostra <b>somente a porcentagem de aparecer e em qual bioma</b>.</li>
 * </ul>
 */
public class SlimepediaWindow {

    private boolean open;

    /** Alterna entre aberto e fechado. */
    public void toggle() {
        open = !open;
    }

    /** @param open define se a janela está visível */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /** @return true se a Slimepedia está aberta */
    public boolean isOpen() {
        return open;
    }

    /**
     * Desenha o catálogo de slimes na tela.
     *
     * @param shapes     renderer de formas
     * @param batch      batch de sprites
     * @param assets     texturas e fonte
     * @param slimepedia dados de descoberta
     * @param screenW    largura da tela virtual
     * @param screenH    altura da tela virtual
     */
    public void render(ShapeRenderer shapes, SpriteBatch batch, Assets assets,
                       Slimepedia slimepedia, float screenW, float screenH) {
        if (!open) {
            return;
        }
        float w = 640f;
        float h = 380f;
        float x = (screenW - w) / 2f;
        float y = (screenH - h) / 2f + 12f;

        SlimeType[] types = SlimeType.values();

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.06f, 0.10f, 0.16f, 0.96f);
        shapes.rect(x - 4f, y - 4f, w + 8f, h + 8f);
        shapes.setColor(0.13f, 0.21f, 0.32f, 0.98f);
        shapes.rect(x, y, w, h);
        shapes.setColor(0.94f, 0.36f, 0.54f, 1f);
        shapes.rect(x, y + h - 36f, w, 36f);

        for (int i = 0; i < types.length; i++) {
            float ry = y + h - 88f - i * 50f;
            boolean found = slimepedia.isCaptured(types[i]);
            if (found) {
                shapes.setColor(0.18f, 0.29f, 0.44f, 1f);
            } else {
                shapes.setColor(0.09f, 0.14f, 0.22f, 1f);
            }
            shapes.rect(x + 14f, ry, w - 28f, 44f);
        }
        shapes.end();

        BitmapFont font = assets.getFont();
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "SLIMEPEDIA  -  DESCOBERTOS: " + slimepedia.getCapturedCount()
                + " / " + slimepedia.getTotalSpecies() + "   ([P / TAB / ESC] Fechar)", x + 18f, y + h - 12f);

        for (int i = 0; i < types.length; i++) {
            SlimeType t = types[i];
            boolean found = slimepedia.isCaptured(t);
            float ry = y + h - 88f - i * 50f;
            TextureRegion reg = assets.get(t.texturePrefix + "_happy");

            if (found) {
                batch.setColor(Color.WHITE);
                batch.draw(reg, x + 22f, ry + 5f, 36f, 34f);

                font.setColor(Color.valueOf("FFE580"));
                font.draw(batch, t.displayName + "  (Favorito: " + t.favoriteFood.displayName
                        + " | Cristal: $" + t.crystalItem.basePrice + ")", x + 68f, ry + 36f);

                font.setColor(Color.valueOf("98F5FF"));
                font.draw(batch, "Chance de Spawn: " + t.spawnChancePercent + "%   |   Bioma: "
                        + t.getBiomesDescription(), x + 68f, ry + 16f);
            } else {
                // Silhueta preta para slimes ainda não capturados
                batch.setColor(0f, 0f, 0f, 1f);
                batch.draw(reg, x + 22f, ry + 5f, 36f, 34f);
                batch.setColor(Color.WHITE);

                font.setColor(Color.GRAY);
                font.draw(batch, "??? (Capture com o aspirador para revelar)", x + 68f, ry + 36f);

                font.setColor(Color.valueOf("FFD27A"));
                font.draw(batch, "Chance de Spawn: " + t.spawnChancePercent + "%   |   Bioma: "
                        + t.getBiomesDescription(), x + 68f, ry + 16f);
            }
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }
}
