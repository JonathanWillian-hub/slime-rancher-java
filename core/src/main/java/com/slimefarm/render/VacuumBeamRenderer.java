package com.slimefarm.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.slimefarm.entities.Player;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.util.RandomUtils;

/**
 * Desenha o feixe translúcido em formato de cone do aspirador (como na imagem de referência)
 * e emite partículas coloridas sendo sugadas para dentro do bico.
 */
public class VacuumBeamRenderer implements Disposable {

    private static final Color[] CONFETTI = {
            Color.valueOf("FF5E9C"),
            Color.valueOf("5CE1FF"),
            Color.valueOf("FFE055"),
            Color.valueOf("7DFF7A"),
            Color.valueOf("C468FF")
    };

    private final ShapeRenderer shapes = new ShapeRenderer();
    private final Vector2 nozzleWorld = new Vector2();
    private float emitAccumulator;

    /**
     * Emite partículas coloridas quando o jogador está aspirando.
     *
     * @param player    jogador
     * @param particles sistema de partículas
     * @param dt        tempo do quadro (s)
     */
    public void update(Player player, ParticleSystem particles, float dt) {
        if (!player.isVacuuming()) {
            return;
        }
        emitAccumulator += dt * 45f;
        player.getNozzleWorldPos(nozzleWorld);
        float nx = IsoUtils.toScreenX(nozzleWorld.x, nozzleWorld.y);
        float ny = IsoUtils.toScreenY(nozzleWorld.x, nozzleWorld.y, player.getGroundZ()) + 16f;
        float aimX = IsoUtils.toScreenX(player.getAimWorld().x, player.getAimWorld().y);
        float aimY = IsoUtils.toScreenY(player.getAimWorld().x, player.getAimWorld().y, player.getGroundZ()) + 12f;
        float baseAngle = MathUtils.atan2(aimY - ny, aimX - nx);

        while (emitAccumulator >= 1f) {
            emitAccumulator -= 1f;
            float a = baseAngle + RandomUtils.range(-0.45f, 0.45f);
            float dist = RandomUtils.range(28f, 92f);
            float sx = nx + MathUtils.cos(a) * dist;
            float sy = ny + MathUtils.sin(a) * dist;
            float life = 0.28f;
            float vx = (nx - sx) / life;
            float vy = (ny - sy) / life;
            particles.spawn(sx, sy, vx, vy, RandomUtils.range(3.5f, 5.5f), life, RandomUtils.pick(CONFETTI));
        }
    }

    /**
     * Desenha os feixes translúcidos do cone de sucção (chamar fora do {@code SpriteBatch.begin()}).
     *
     * @param camera câmera do mundo
     * @param player jogador
     */
    public void renderBeam(OrthographicCamera camera, Player player) {
        if (!player.isVacuuming()) {
            return;
        }
        player.getNozzleWorldPos(nozzleWorld);
        float nx = IsoUtils.toScreenX(nozzleWorld.x, nozzleWorld.y);
        float ny = IsoUtils.toScreenY(nozzleWorld.x, nozzleWorld.y, player.getGroundZ()) + 16f;
        float aimX = IsoUtils.toScreenX(player.getAimWorld().x, player.getAimWorld().y);
        float aimY = IsoUtils.toScreenY(player.getAimWorld().x, player.getAimWorld().y, player.getGroundZ()) + 12f;

        float angle = MathUtils.atan2(aimY - ny, aimX - nx);
        float len = 95f;
        float spread = Constants.VACUUM_HALF_ANGLE * MathUtils.degreesToRadians * 0.75f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // Cone externo suave
        float x1 = nx + MathUtils.cos(angle - spread) * len;
        float y1 = ny + MathUtils.sin(angle - spread) * len;
        float x2 = nx + MathUtils.cos(angle + spread) * len;
        float y2 = ny + MathUtils.sin(angle + spread) * len;
        shapes.setColor(0.55f, 0.94f, 1f, 0.20f);
        shapes.triangle(nx, ny, x1, y1, x2, y2);

        // Cone central mais brilhante
        float x3 = nx + MathUtils.cos(angle - spread * 0.45f) * (len * 0.85f);
        float y3 = ny + MathUtils.sin(angle - spread * 0.45f) * (len * 0.85f);
        float x4 = nx + MathUtils.cos(angle + spread * 0.45f) * (len * 0.85f);
        float y4 = ny + MathUtils.sin(angle + spread * 0.45f) * (len * 0.85f);
        shapes.setColor(0.82f, 0.98f, 1f, 0.26f);
        shapes.triangle(nx, ny, x3, y3, x4, y4);

        shapes.end();
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}

