package com.slimefarm.entities.props;

/**
 * Portão aberto (dois postes de madeira e um arame caído), por onde se passa
 * para sair da área do rancho. Não bloqueia o movimento.
 */
public class Gate extends Prop {

    /**
     * @param tileX coluna
     * @param tileY linha
     */
    public Gate(int tileX, int tileY) {
        super(tileX + 0.5f, tileY + 0.5f, 0.1f, false, "gate");
    }
}

