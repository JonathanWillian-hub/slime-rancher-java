package com.slimefarm.entities.props;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.slimefarm.assets.Assets;
import com.slimefarm.entities.Entity;
import com.slimefarm.world.World;

/**
 * Objeto estático do cenário (árvore, pedra, cerca...). Por padrão não tem lógica
 * e é desenhado com uma única textura ancorada na base.
 */
public abstract class Prop extends Entity {

    /** Nome da textura no {@link Assets}. */
    protected String textureName;
    /** Desenhar espelhado horizontalmente. */
    protected boolean flipped;
    /** Quantos pixels a base da imagem fica acima da borda inferior da textura. */
    protected float drawOffsetY;

    /**
     * @param x           x (tiles)
     * @param y           y (tiles)
     * @param radius      raio de colisão
     * @param solid       se bloqueia movimento
     * @param textureName textura
     */
    protected Prop(float x, float y, float radius, boolean solid, String textureName) {
        super(x, y, radius);
        this.solid = solid;
        this.textureName = textureName;
    }

    /**
     * Chamado uma vez quando o prop é colocado no mundo (ex.: registrar paredes).
     *
     * @param world mundo
     */
    public void onPlaced(World world) {
    }

    @Override
    public void update(World world, float dt) {
        // props são estáticos por padrão
    }

    @Override
    public void render(SpriteBatch batch, Assets assets) {
        drawAnchored(batch, assets.get(textureName), flipped ? -1f : 1f, 1f, drawOffsetY);
    }

    /** @param flipped desenhar espelhado */
    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }
}
