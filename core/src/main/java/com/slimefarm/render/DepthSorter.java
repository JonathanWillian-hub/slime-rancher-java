package com.slimefarm.render;

import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.Entity;

import java.util.Comparator;

/**
 * Ordena entidades por profundidade isométrica ({@link Entity#getDepth()})
 * do menor (mais ao fundo) para o maior (mais à frente).
 */
public final class DepthSorter {

    private static final Comparator<Entity> BY_DEPTH =
            (a, b) -> Float.compare(a.getDepth(), b.getDepth());

    private final Array<Entity> buffer = new Array<>();

    /**
     * Retorna uma lista ordenada das entidades recebidas (reutiliza um buffer interno).
     *
     * @param entities lista original (não é alterada)
     * @return lista ordenada do fundo para a frente
     */
    public Array<Entity> sort(Array<Entity> entities) {
        buffer.clear();
        buffer.addAll(entities);
        buffer.sort(BY_DEPTH);
        return buffer;
    }
}
