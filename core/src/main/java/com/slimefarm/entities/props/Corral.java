package com.slimefarm.entities.props;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.slimefarm.assets.Assets;
import com.slimefarm.world.World;

/**
 * Curral tecnológico com postes metálicos nos cantos e barreiras de vidro energético.
 * <p>
 * A barreira de vidro é seletiva: o jogador ({@link com.slimefarm.entities.Player}) consegue
 * atravessar o vidro livremente para entrar/sair e capturar um slime específico, enquanto o
 * {@link com.slimefarm.systems.CorralSystem} mantém os slimes do curral presos lá dentro e impede
 * que slimes selvagens entrem sozinhos (a menos que sejam jogados pelo jogador).
 */
public class Corral extends Prop {

    private final Rectangle bounds;

    /**
     * @param tileX0 coluna inicial
     * @param tileY0 linha inicial
     * @param size   tamanho do lado em tiles
     */
    public Corral(int tileX0, int tileY0, int size) {
        this(tileX0, tileY0, size, size);
    }

    /**
     * @param tileX0 coluna inicial
     * @param tileY0 linha inicial
     * @param width  largura em tiles (ao longo do eixo X)
     * @param height altura em tiles (ao longo do eixo Y)
     */
    public Corral(int tileX0, int tileY0, int width, int height) {
        super(tileX0 + width / 2f, tileY0 + height / 2f, 0.1f, false, "corral_post");
        this.bounds = new Rectangle(tileX0, tileY0, width, height);
    }

    @Override
    public void onPlaced(World world) {
        // Não adiciona paredes físicas no CollisionMap global para permitir que o Player
        // atravesse o vidro livremente. O bloqueio seletivo de Slimes é feito pelo CorralSystem.
    }

    @Override
    public void render(SpriteBatch batch, Assets assets) {
        // Desenhado em duas camadas pelo CorralRenderer (traseira e frontal)
    }

    /** @return área retangular do curral em coordenadas de mundo */
    public Rectangle getBounds() {
        return bounds;
    }
}
