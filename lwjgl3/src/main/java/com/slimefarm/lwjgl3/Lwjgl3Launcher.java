package com.slimefarm.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.slimefarm.SlimeFarmGame;

/**
 * Ponto de entrada da versão desktop (LWJGL3).
 * Apenas configura a janela e inicia o {@link SlimeFarmGame}.
 */
public class Lwjgl3Launcher {

    /** Largura inicial da janela, em pixels. */
    private static final int WINDOW_WIDTH = 1280;
    /** Altura inicial da janela, em pixels. */
    private static final int WINDOW_HEIGHT = 720;

    /**
     * Método main: cria a janela do jogo.
     *
     * @param args argumentos de linha de comando (não usados)
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Erro nao tratado no thread " + t.getName() + ":");
            e.printStackTrace();
        });
        try {
            new Lwjgl3Application(new SlimeFarmGame(), createConfiguration());
        } catch (Throwable t) {
            System.err.println("Falha ao iniciar aplicacao:");
            t.printStackTrace();
        }
    }

    /**
     * Monta a configuração da janela (título, tamanho, vsync e FPS).
     *
     * @return configuração pronta para o {@link Lwjgl3Application}
     */
    private static Lwjgl3ApplicationConfiguration createConfiguration() {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Slime Farm Iso");
        config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.setResizable(true);
        return config;
    }
}

