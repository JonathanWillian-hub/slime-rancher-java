package com.slimefarm.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.assets.Assets;
import com.slimefarm.assets.TextureFactory;

/**
 * Gerenciador de partículas com reaproveitamento de objetos (pool simples)
 * para evitar alocações durante o jogo.
 */
public class ParticleSystem implements Assets.LoadTask {

    private static final String TEX_DOT = "particle_dot";
    private final Assets assets;
    private final Array<Particle> active = new Array<>();
    private final Array<Particle> pool = new Array<>();

    /** @param assets repositório de texturas */
    public ParticleSystem(Assets assets) {
        this.assets = assets;
    }

    @Override
    public void load(Assets a) {
        Pixmap p = TextureFactory.newPixmap(6, 6);
        TextureFactory.ellipse(p, 3, 3, 2, 2, Color.WHITE);
        a.register(TEX_DOT, TextureFactory.toTexture(p));
    }

    /**
     * Emite uma nova partícula em coordenadas de tela.
     *
     * @param x       x inicial (pixels)
     * @param y       y inicial (pixels)
     * @param vx      velocidade x
     * @param vy      velocidade y
     * @param size    diâmetro (pixels)
     * @param maxLife tempo de vida (s)
     * @param color   cor
     */
    public void spawn(float x, float y, float vx, float vy, float size, float maxLife, Color color) {
        Particle p = pool.size > 0 ? pool.pop() : new Particle();
        p.init(x, y, vx, vy, size, maxLife, color);
        active.add(p);
    }

    /**
     * Atualiza todas as partículas ativas e devolve as expiradas para o pool.
     *
     * @param dt tempo do quadro (s)
     */
    public void update(float dt) {
        for (int i = active.size - 1; i >= 0; i--) {
            Particle p = active.get(i);
            if (!p.update(dt)) {
                active.removeIndex(i);
                pool.add(p);
            }
        }
    }

    /**
     * Desenha todas as partículas ativas no {@link SpriteBatch} (já iniciado).
     *
     * @param batch batch ativo
     */
    public void render(SpriteBatch batch) {
        if (!assets.has(TEX_DOT)) {
            return;
        }
        TextureRegion dot = assets.get(TEX_DOT);
        for (Particle p : active) {
            batch.setColor(p.color.r, p.color.g, p.color.b, p.color.a * p.getAlpha());
            batch.draw(dot, p.x - p.size / 2f, p.y - p.size / 2f, p.size, p.size);
        }
        batch.setColor(Color.WHITE);
    }
}

