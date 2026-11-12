package com.slimefarm.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.slimefarm.SlimeFarmGame;
import com.slimefarm.util.Constants;

/**
 * Tela de carregamento: gera as texturas em etapas e mostra uma barra de progresso.
 * Ao terminar, abre a {@link GameScreen}.
 */
public class LoadingScreen extends ScreenAdapter {

    private final SlimeFarmGame game;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final ScreenViewport viewport = new ScreenViewport();

    /** @param game jogo principal (dono dos Assets) */
    public LoadingScreen(SlimeFarmGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        boolean done = game.getAssets().loadStep();

        Gdx.gl.glClearColor(Constants.BACKGROUND.r, Constants.BACKGROUND.g, Constants.BACKGROUND.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        shapes.setProjectionMatrix(viewport.getCamera().combined);
        float w = viewport.getWorldWidth() * 0.5f;
        float x = (viewport.getWorldWidth() - w) / 2f;
        float y = viewport.getWorldHeight() / 2f - 10f;
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.DARK_GRAY);
        shapes.rect(x, y, w, 20f);
        shapes.setColor(Constants.SLIME_PINK);
        shapes.rect(x + 2, y + 2, (w - 4) * game.getAssets().getProgress(), 16f);
        shapes.end();

        if (done) {
            game.onAssetsLoaded();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}

