package com.slimefarm.progression;

import com.slimefarm.entities.slimes.SlimeType;

import java.util.EnumSet;

/**
 * Registro estilo "Pokédex" dos tipos de slimes que o jogador já capturou com o aspirador.
 * Enquanto um slime não for capturado pelo menos uma vez, a {@link com.slimefarm.ui.SlimepediaWindow}
 * mostra sua silhueta preta e apenas a porcentagem de spawn e os biomas onde aparece.
 */
public class Slimepedia {

    private final EnumSet<SlimeType> captured = EnumSet.noneOf(SlimeType.class);

    /**
     * Registra que o jogador capturou um slime deste tipo.
     *
     * @param type tipo de slime capturado
     * @return true se foi uma descoberta inédita (primeira vez capturando)
     */
    public boolean registerCapture(SlimeType type) {
        if (type == null) {
            return false;
        }
        return captured.add(type);
    }

    /**
     * @param type tipo consultado
     * @return true se o jogador já capturou este slime alguma vez
     */
    public boolean isCaptured(SlimeType type) {
        return captured.contains(type);
    }

    /** @return quantidade de tipos diferentes já capturados */
    public int getCapturedCount() {
        return captured.size();
    }

    /** @return total de espécies de slimes existentes no jogo */
    public int getTotalSpecies() {
        return SlimeType.values().length;
    }

    /** Limpa todos os registros (usado antes de carregar um save). */
    public void clear() {
        captured.clear();
    }
}
