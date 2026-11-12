package com.slimefarm.render;

import com.badlogic.gdx.graphics.Color;

/**
 * Uma partícula visual leve em coordenadas de tela (para feixe do aspirador,
 * brilhos de cristais e vagalumes do ambiente).
 */
public class Particle {

    /** Posição e velocidade em pixels de tela. */
    public float x;
    public float y;
    public float vx;
    public float vy;
    /** Tamanho em pixels. */
    public float size = 3f;
    /** Tempo de vida restante e vida total (s). */
    public float life;
    public float maxLife;
    /** Cor da partícula. */
    public final Color color = new Color(Color.WHITE);

    /**
     * Inicializa os dados da partícula.
     *
     * @param x       x na tela
     * @param y       y na tela
     * @param vx      velocidade x
     * @param vy      velocidade y
     * @param size    tamanho em pixels
     * @param maxLife duração (s)
     * @param c       cor
     */
    public void init(float x, float y, float vx, float vy, float size, float maxLife, Color c) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.size = size;
        this.life = maxLife;
        this.maxLife = maxLife;
        this.color.set(c);
    }

    /**
     * Atualiza posição e tempo de vida.
     *
     * @param dt tempo do quadro (s)
     * @return true se a partícula ainda está viva
     */
    public boolean update(float dt) {
        x += vx * dt;
        y += vy * dt;
        life -= dt;
        return life > 0f;
    }

    /** @return transparência proporcional ao tempo restante (0..1) */
    public float getAlpha() {
        return maxLife <= 0f ? 0f : Math.max(0f, life / maxLife);
    }
}

