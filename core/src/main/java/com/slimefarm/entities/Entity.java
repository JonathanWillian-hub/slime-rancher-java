package com.slimefarm.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.slimefarm.assets.Assets;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.world.World;

/**
 * Base de tudo que existe no mundo além do chão (jogador, slimes, itens, props).
 * <p>
 * Posição em coordenadas de MUNDO: {@code pos} (x, y no chão, em tiles) e {@code z} (altura).
 * A lógica fica em {@link #update(World, float)} e o desenho em {@link #render(SpriteBatch, Assets)}.
 */
public abstract class Entity {

    /** Posição no chão (tiles). */
    protected final Vector2 pos = new Vector2();
    /** Altura acima do chão (unidades de elevação). */
    protected float z;
    /** Raio de colisão (tiles). */
    protected float radius;
    /** Se bloqueia o movimento de outras entidades. */
    protected boolean solid;
    /** Marcada para ser removida do mundo no fim do quadro. */
    private boolean removed;

    /**
     * @param x      posição x inicial (tiles)
     * @param y      posição y inicial (tiles)
     * @param radius raio de colisão (tiles)
     */
    protected Entity(float x, float y, float radius) {
        this.pos.set(x, y);
        this.radius = radius;
    }

    /**
     * Atualiza a lógica da entidade.
     *
     * @param world mundo onde ela está
     * @param dt    tempo do quadro (s)
     */
    public abstract void update(World world, float dt);

    /**
     * Desenha a entidade (a SpriteBatch já está iniciada, em coordenadas de tela isométrica).
     *
     * @param batch  batch ativo
     * @param assets texturas
     */
    public abstract void render(SpriteBatch batch, Assets assets);

    /**
     * Desenha uma região com a base (centro inferior) no ponto de mundo da entidade.
     *
     * @param batch  batch
     * @param region imagem
     * @param scaleX escala horizontal (negativa = espelhado)
     * @param scaleY escala vertical
     */
    protected void drawAnchored(SpriteBatch batch, TextureRegion region, float scaleX, float scaleY) {
        drawAnchored(batch, region, scaleX, scaleY, 0f);
    }

    /**
     * Igual a {@link #drawAnchored(SpriteBatch, TextureRegion, float, float)}, com deslocamento vertical.
     *
     * @param offsetY pixels a deslocar a imagem para baixo (ex.: base desenhada acima da borda inferior)
     */
    protected void drawAnchored(SpriteBatch batch, TextureRegion region, float scaleX, float scaleY, float offsetY) {
        float sx = IsoUtils.toScreenX(pos.x, pos.y);
        float sy = IsoUtils.toScreenY(pos.x, pos.y, getGroundZ() + z) - offsetY;
        float w = region.getRegionWidth();
        float h = region.getRegionHeight();
        batch.draw(region, sx - w / 2f, sy, w / 2f, offsetY, w, h, scaleX, scaleY, 0f);
    }

    /** @return elevação do terreno sob a entidade (atualizada pelo mundo). */
    public float getGroundZ() {
        return groundZ;
    }

    /** Elevação do terreno sob a entidade. */
    protected float groundZ;

    /** @param groundZ nova elevação do terreno sob a entidade */
    public void setGroundZ(float groundZ) {
        this.groundZ = groundZ;
    }

    /** @return profundidade para ordenação (maior = desenha depois). */
    public float getDepth() {
        return IsoUtils.depth(pos.x, pos.y);
    }

    /** @return true se esta entidade deve ter sombra desenhada no chão. */
    public boolean hasShadow() {
        return false;
    }

    /** @return posição (referência mutável). */
    public Vector2 getPos() {
        return pos;
    }

    /** @return altura atual */
    public float getZ() {
        return z;
    }

    /** @return raio de colisão */
    public float getRadius() {
        return radius;
    }

    /** @return true se bloqueia movimento */
    public boolean isSolid() {
        return solid;
    }

    /** Marca a entidade para remoção. */
    public void remove() {
        removed = true;
    }

    /** @return true se foi marcada para remoção */
    public boolean isRemoved() {
        return removed;
    }

    /** @return distância (no chão) até outra entidade */
    public float distanceTo(Entity other) {
        return pos.dst(other.pos);
    }
}
