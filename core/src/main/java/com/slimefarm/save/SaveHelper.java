package com.slimefarm.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.progression.Slimepedia;

/**
 * Métodos auxiliares para carregar dados pontuais do save (como slimes capturados na Slimepedia)
 * mesmo sem instanciar o mundo de jogo.
 */
public final class SaveHelper {

    private static final String SAVE_FILE = "slimefarm_save.json";

    private SaveHelper() {
    }

    /**
     * Carrega a Slimepedia gravada no save para exibição no menu principal.
     *
     * @param slimepedia objeto Slimepedia de destino
     * @return true se conseguiu carregar do arquivo
     */
    public static boolean loadSlimepediaFromSave(Slimepedia slimepedia) {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        if (!file.exists()) {
            return false;
        }
        try {
            Json json = new Json();
            SaveData data = json.fromJson(SaveData.class, file.readString("UTF-8"));
            if (data != null && data.capturedSpecies != null) {
                slimepedia.clear();
                for (String name : data.capturedSpecies) {
                    slimepedia.registerCapture(SlimeType.valueOf(name));
                }
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}

