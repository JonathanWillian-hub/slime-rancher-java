package com.slimefarm.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.slimefarm.SlimeFarmGame;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.progression.Slimepedia;
import com.slimefarm.save.SaveHelper;
import com.slimefarm.save.SaveManager;
import com.slimefarm.ui.SlimepediaWindow;
import com.slimefarm.util.Constants;

/**
 * Tela de menu inicial simples e direta:
 * <ul>
 *   <li>Botão "Iniciar Jogo" (ou continuar se houver save salvo);</li>
 *   <li>Botão "Ver Inventário / Slimes Pegos" (abre a Pokédex/Slimepedia mostrando todos os slimes já capturados);</li>
 *   <li>Suporte a clique do mouse e atalhos de teclado (Enter / I / Esc).</li>
 * </ul>
 */
public class MenuScreen extends ScreenAdapter {

    private final SlimeFarmGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final ExtendViewport viewport = new ExtendViewport(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, camera);
    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SaveManager saveManager = new SaveManager();
    private final Slimepedia slimepedia = new Slimepedia();
    private final SlimepediaWindow slimepediaWindow = new SlimepediaWindow();

    private final Rectangle startButtonBounds = new Rectangle();
    private final Rectangle invButtonBounds = new Rectangle();
    private final Vector3 tmpClick = new Vector3();
    private boolean startingGame;

    /** @param game jogo principal */
    public MenuScreen(SlimeFarmGame game) {
        this.game = game;
        // Carrega registros da Slimepedia salvos em disco (ou inicializa com o Rosa padrão)
        if (!SaveHelper.loadSlimepediaFromSave(slimepedia)) {
            slimepedia.registerCapture(SlimeType.PINK);
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        if (startingGame || game.getScreen() != this) {
            return;
        }

        Gdx.gl.glClearColor(0.14f, 0.28f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        camera.position.set(w / 2f, h / 2f, 0f);
        camera.update();

        float boxW = 420f;
        float boxH = 260f;
        float bx = (w - boxW) / 2f;
        float by = (h - boxH) / 2f;

        float btnW = 320f;
        float btnH = 46f;
        float btnX = (w - btnW) / 2f;
        float btnStartPr = by + 120f;
        float btnInvPr = by + 56f;

        startButtonBounds.set(btnX, btnStartPr, btnW, btnH);
        invButtonBounds.set(btnX, btnInvPr, btnW, btnH);

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        // Fundo do painel
        shapes.setColor(0.08f, 0.14f, 0.20f, 0.94f);
        shapes.rect(bx - 4f, by - 4f, boxW + 8f, boxH + 8f);
        shapes.setColor(0.16f, 0.26f, 0.38f, 0.96f);
        shapes.rect(bx, by, boxW, boxH);

        // Botão Iniciar Jogo
        shapes.setColor(0.12f, 0.44f, 0.24f, 1f);
        shapes.rect(btnX, btnStartPr, btnW, btnH);
        shapes.setColor(0.25f, 0.72f, 0.36f, 1f);
        shapes.rect(btnX + 2f, btnStartPr + 2f, btnW - 4f, btnH - 4f);

        // Botão Ver Inventário / Slimes Pegos
        shapes.setColor(0.18f, 0.28f, 0.52f, 1f);
        shapes.rect(btnX, btnInvPr, btnW, btnH);
        shapes.setColor(0.32f, 0.50f, 0.82f, 1f);
        shapes.rect(btnX + 2f, btnInvPr + 2f, btnW - 4f, btnH - 4f);

        shapes.end();

        BitmapFont font = game.getAssets().getFont();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Slimes decorativos no topo do painel
        batch.draw(game.getAssets().get("slime_pink_happy"), w / 2f - 90f, by + boxH - 58f, 44f, 42f);
        batch.draw(game.getAssets().get("slime_cat_happy"), w / 2f - 22f, by + boxH - 55f, 44f, 42f);
        batch.draw(game.getAssets().get("slime_rock_happy"), w / 2f + 46f, by + boxH - 58f, 44f, 42f);

        font.setColor(Color.valueOf("FFE580"));
        font.draw(batch, "SLIME FARM ISO", bx + 145f, by + boxH - 74f);

        // Texto dos Botões
        font.setColor(Color.WHITE);
        String startText = saveManager.hasSave() ? "CONTINUAR / INICIAR JOGO" : "INICIAR JOGO";
        font.draw(batch, startText, btnX + (btnW - (startText.length() * 8f)) / 2f, btnStartPr + 28f);

        String invText = "VER INVENTARIO (SLIMES PEGOS: " + slimepedia.getCapturedCount() + "/" + slimepedia.getTotalSpecies() + ")";
        font.draw(batch, invText, btnX + (btnW - (invText.length() * 7.5f)) / 2f, btnInvPr + 28f);

        font.setColor(Color.valueOf("C2D0E0"));
        font.draw(batch, "Clique nos botoes ou use [ENTER] / [I] / [ESC]", bx + 65f, by + 26f);

        batch.end();

        // Janela de Slimes Pegos (Slimepedia) sobreposta quando aberta
        slimepediaWindow.render(shapes, batch, game.getAssets(), slimepedia, w, h);
    }

    private void handleInput() {
        if (startingGame) {
            return;
        }

        if (Gdx.input.justTouched()) {
            tmpClick.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(tmpClick);

            if (slimepediaWindow.isOpen()) {
                slimepediaWindow.setOpen(false);
                return;
            }

            if (startButtonBounds.contains(tmpClick.x, tmpClick.y)) {
                startingGame = true;
                game.startGame(saveManager.hasSave());
                return;
            }
            if (invButtonBounds.contains(tmpClick.x, tmpClick.y)) {
                slimepediaWindow.toggle();
                return;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (slimepediaWindow.isOpen()) {
                slimepediaWindow.setOpen(false);
            } else {
                startingGame = true;
                game.startGame(saveManager.hasSave());
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I) || Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            slimepediaWindow.toggle();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (slimepediaWindow.isOpen()) {
                slimepediaWindow.setOpen(false);
            } else {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
    }
}
