package com.slimefarm;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.slimefarm.assets.Assets;
import com.slimefarm.assets.ItemTextureFactory;
import com.slimefarm.assets.PlayerTextureFactory;
import com.slimefarm.assets.PropTextureFactory;
import com.slimefarm.assets.SlimeTextureFactory;
import com.slimefarm.assets.TileTextureFactory;
import com.slimefarm.assets.UiTextureFactory;
import com.slimefarm.audio.SoundManager;
import com.slimefarm.render.CorralRenderer;
import com.slimefarm.render.ParticleSystem;
import com.slimefarm.render.ShadowRenderer;
import com.slimefarm.screens.GameScreen;
import com.slimefarm.screens.LoadingScreen;
import com.slimefarm.screens.MenuScreen;

/**
 * Classe principal do jogo. Controla qual tela ({@link com.badlogic.gdx.Screen})
 * está ativa e guarda recursos compartilhados entre as telas ({@link Assets} e {@link SoundManager}).
 */
public class SlimeFarmGame extends Game {

    private Assets assets;
    private SoundManager sounds;

    /** Chamado uma vez quando o jogo inicia: agenda os assets e abre a tela de carregamento. */
    @Override
    public void create() {
        assets = new Assets();
        sounds = new SoundManager();
        assets.queue(new TileTextureFactory());
        assets.queue(new PropTextureFactory());
        assets.queue(new PlayerTextureFactory());
        assets.queue(new ItemTextureFactory());
        assets.queue(new SlimeTextureFactory());
        assets.queue(new UiTextureFactory());
        assets.queue(new CorralRenderer(assets));
        assets.queue(new ShadowRenderer(assets));
        assets.queue(new ParticleSystem(assets));
        setScreen(new LoadingScreen(this));
    }

    /** Abre o menu principal de forma segura fora do loop de render da tela atual. */
    public void showMenu() {
        Gdx.app.postRunnable(() -> {
            com.badlogic.gdx.Screen old = getScreen();
            setScreen(new MenuScreen(this));
            if (old != null) {
                old.dispose();
            }
        });
    }

    /**
     * Inicia a tela de jogo (nova partida ou carregando o arquivo salvo).
     * Transição agendada via postRunnable para garantir que a tela anterior não seja
     * descartada no meio do seu próprio método render.
     *
     * @param loadSave true para carregar o progresso salvo
     */
    public void startGame(boolean loadSave) {
        Gdx.app.postRunnable(() -> {
            com.badlogic.gdx.Screen old = getScreen();
            setScreen(new GameScreen(this, loadSave));
            if (old != null) {
                old.dispose();
            }
        });
    }

    /** Chamado pela LoadingScreen quando todas as texturas foram geradas. */
    public void onAssetsLoaded() {
        sounds.loadOptionalSounds();
        showMenu();
    }

    /** @return repositório de texturas e fontes */
    public Assets getAssets() {
        return assets;
    }

    /** @return gerenciador de efeitos sonoros */
    public SoundManager getSounds() {
        return sounds;
    }

    /** Libera a tela atual, sons e assets ao fechar o jogo. */
    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (sounds != null) {
            sounds.dispose();
        }
        if (assets != null) {
            assets.dispose();
        }
    }
}
