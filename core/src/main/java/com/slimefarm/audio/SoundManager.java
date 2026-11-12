package com.slimefarm.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Gerenciador de efeitos sonoros originais/CC0.
 * Se o arquivo de áudio opcional existir em {@code assets/sfx/}, ele é carregado e tocado;
 * caso contrário, a chamada é ignorada de forma segura sem interromper o jogo.
 */
public class SoundManager implements Disposable {

    private final ObjectMap<String, Sound> sounds = new ObjectMap<>();
    private float volume = 0.6f;

    /** Tenta carregar os efeitos opcionais da pasta {@code sfx/}. */
    public void loadOptionalSounds() {
        String[] keys = {"pop", "shoot", "sell", "hop"};
        for (String key : keys) {
            FileHandle fh = Gdx.files.internal("sfx/" + key + ".wav");
            if (fh.exists()) {
                sounds.put(key, Gdx.audio.newSound(fh));
            }
        }
    }

    /**
     * Reproduz um efeito sonoro pelo nome, se estiver carregado.
     *
     * @param name nome do som ("pop", "shoot", "sell", "hop")
     */
    public void play(String name) {
        Sound s = sounds.get(name);
        if (s != null && volume > 0f) {
            s.play(volume);
        }
    }

    /** @param volume volume global entre 0 e 1 */
    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }

    /** @return volume atual (0..1) */
    public float getVolume() {
        return volume;
    }

    @Override
    public void dispose() {
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }
}

