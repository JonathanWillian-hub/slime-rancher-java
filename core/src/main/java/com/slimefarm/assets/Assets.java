package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Repositório central de recursos gráficos. As texturas são geradas pelas
 * fábricas (TileTextureFactory, PropTextureFactory...) e registradas aqui por nome.
 * <p>
 * O carregamento é feito em etapas ({@link #loadStep()}) para a LoadingScreen
 * poder mostrar uma barra de progresso.
 */
public class Assets implements Disposable {

    /** Uma etapa de geração de texturas. */
    public interface LoadTask {
        /** Gera e registra texturas no {@link Assets}. */
        void load(Assets assets);
    }

    private final ObjectMap<String, TextureRegion> regions = new ObjectMap<>();
    private final Array<Texture> textures = new Array<>();
    private final Array<LoadTask> pending = new Array<>();
    private int totalTasks;
    private BitmapFont font;

    /** Cria o repositório e agenda as etapas de geração. */
    public Assets() {
        font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.setUseIntegerPositions(true);
    }

    /**
     * Agenda uma etapa de carregamento.
     *
     * @param task etapa
     */
    public void queue(LoadTask task) {
        pending.add(task);
        totalTasks++;
    }

    /**
     * Executa a próxima etapa pendente.
     *
     * @return true quando tudo foi carregado
     */
    public boolean loadStep() {
        if (pending.size > 0) {
            pending.removeIndex(0).load(this);
        }
        return pending.size == 0;
    }

    /** @return progresso de 0 a 1. */
    public float getProgress() {
        return totalTasks == 0 ? 1f : 1f - pending.size / (float) totalTasks;
    }

    /**
     * Registra uma textura inteira com um nome.
     *
     * @param name    nome único (ex.: "tile_grass_0")
     * @param texture textura gerada
     * @return região criada
     */
    public TextureRegion register(String name, Texture texture) {
        textures.add(texture);
        TextureRegion region = new TextureRegion(texture);
        regions.put(name, region);
        return region;
    }

    /**
     * Busca uma região pelo nome.
     *
     * @param name nome registrado
     * @return região
     * @throws IllegalArgumentException se o nome não existir
     */
    public TextureRegion get(String name) {
        TextureRegion r = regions.get(name);
        if (r == null) {
            throw new IllegalArgumentException("Textura não registrada: " + name);
        }
        return r;
    }

    /** @return true se existe uma região com esse nome. */
    public boolean has(String name) {
        return regions.containsKey(name);
    }

    /** @return fonte padrão do jogo. */
    public BitmapFont getFont() {
        return font;
    }

    /** Libera todas as texturas e a fonte. */
    @Override
    public void dispose() {
        for (Texture t : textures) {
            t.dispose();
        }
        textures.clear();
        regions.clear();
        if (font != null) {
            font.dispose();
            font = null;
        }
    }
}

