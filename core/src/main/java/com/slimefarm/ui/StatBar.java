package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Barra de atributo horizontal (vida ou energia) com moldura escura, preenchimento colorido
 * e ícone à esquerda, igual ao canto superior esquerdo da referência.
 */
public class StatBar {

    private final Color fillColor;
    private final String iconName;

    /**
     * @param fillColor cor do preenchimento da barra
     * @param iconName  nome da textura do ícone no {@link com.slimefarm.assets.Assets}
     */
    public StatBar(Color fillColor, String iconName) {
        this.fillColor = fillColor;
        this.iconName = iconName;
    }

    /**
     * Desenha a moldura e o preenchimento da barra no {@link ShapeRenderer} (já em modo Filled).
     *
     * @param shapes renderer de formas ativo
     * @param x      x da barra
     * @param y      y da barra
     * @param width  largura total
     * @param height altura
     * @param ratio  preenchimento (0..1)
     */
    public void renderShapes(ShapeRenderer shapes, float x, float y, float width, float height, float ratio) {
        float clamped = Math.max(0f, Math.min(1f, ratio));
        // Borda externa escura
        shapes.setColor(0.11f, 0.14f, 0.20f, 0.95f);
        shapes.rect(x, y, width, height);
        // Borda interna clara
        shapes.setColor(0.78f, 0.84f, 0.92f, 1f);
        shapes.rect(x + 2f, y + 2f, width - 4f, height - 4f);
        // Fundo vazio
        shapes.setColor(0.18f, 0.22f, 0.30f, 1f);
        shapes.rect(x + 4f, y + 4f, width - 8f, height - 8f);
        // Preenchimento colorido
        float fillW = (width - 8f) * clamped;
        if (fillW > 0f) {
            shapes.setColor(fillColor);
            shapes.rect(x + 4f, y + 4f, fillW, height - 8f);
            shapes.setColor(1f, 1f, 1f, 0.25f);
            shapes.rect(x + 4f, y + height - 8f, fillW, 3f);
        }
    }

    /**
     * Desenha o ícone à esquerda da barra no {@link SpriteBatch} (já iniciado).
     *
     * @param batch batch ativo
     * @param icon  região do ícone
     * @param x     x da barra
     * @param y     y da barra
     * @param height altura da barra
     */
    public void renderIcon(SpriteBatch batch, TextureRegion icon, float x, float y, float height) {
        batch.draw(icon, x - 12f, y + (height - 22f) / 2f, 22f, 22f);
    }

    /** @return nome da textura do ícone */
    public String getIconName() {
        return iconName;
    }
}

