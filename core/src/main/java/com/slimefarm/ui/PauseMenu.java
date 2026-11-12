package com.slimefarm.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.slimefarm.assets.Assets;

/**
 * Menu de pausa sobreposto à tela do jogo (aberto com Esc).
 * Exibe opções de Continuar, Salvar Jogo e Voltar ao Menu / Sair.
 */
public class PauseMenu {

    private boolean paused;
    private String statusMessage = "";
    private float messageTimer;

    /** Alterna o estado de pausa. */
    public void toggle() {
        paused = !paused;
    }

    /** @param paused define se o jogo está pausado */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /** @return true se o menu de pausa está ativo */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Exibe uma mensagem rápida (ex.: "Jogo salvo com sucesso!").
     *
     * @param msg texto da mensagem
     */
    public void showMessage(String msg) {
        this.statusMessage = msg;
        this.messageTimer = 2.5f;
    }

    /**
     * Desenha o painel de pausa ou mensagens temporárias de status.
     *
     * @param shapes  renderer de formas
     * @param batch   batch de sprites
     * @param assets  texturas e fonte
     * @param screenW largura da tela
     * @param screenH altura da tela
     * @param dt      tempo do quadro
     */
    public void render(ShapeRenderer shapes, SpriteBatch batch, Assets assets,
                       float screenW, float screenH, float dt) {
        if (messageTimer > 0f) {
            messageTimer -= dt;
        }
        if (!paused) {
            if (messageTimer > 0f) {
                batch.begin();
                assets.getFont().setColor(Color.valueOf("FFE580"));
                assets.getFont().draw(batch, statusMessage, screenW / 2f - 70f, screenH - 22f);
                batch.end();
            }
            return;
        }

        float w = 280f;
        float h = 180f;
        float x = (screenW - w) / 2f;
        float y = (screenH - h) / 2f;

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0f, 0f, 0f, 0.55f);
        shapes.rect(0f, 0f, screenW, screenH);
        shapes.setColor(0.10f, 0.15f, 0.24f, 0.96f);
        shapes.rect(x - 3f, y - 3f, w + 6f, h + 6f);
        shapes.setColor(0.18f, 0.27f, 0.40f, 0.98f);
        shapes.rect(x, y, w, h);
        shapes.end();

        BitmapFont font = assets.getFont();
        batch.begin();
        font.setColor(Color.valueOf("FFE580"));
        font.draw(batch, "JOGO PAUSADO", x + 88f, y + h - 22f);
        font.setColor(Color.WHITE);
        font.draw(batch, "[ESC] Continuar", x + 76f, y + h - 62f);
        font.draw(batch, "[F5]  Salvar Progresso", x + 76f, y + h - 92f);
        font.draw(batch, "[M]   Menu Principal", x + 76f, y + h - 122f);
        if (messageTimer > 0f) {
            font.setColor(Color.valueOf("72F2FF"));
            font.draw(batch, statusMessage, x + 65f, y + 24f);
        }
        batch.end();
    }
}

