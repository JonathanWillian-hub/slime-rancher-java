package com.slimefarm.inventory;

import com.slimefarm.entities.items.ItemType;
import com.slimefarm.util.Constants;

/**
 * Um slot da hotbar do jogador: guarda um único {@link ItemType} e sua quantidade
 * (até {@link Constants#SLOT_MAX}).
 */
public class InventorySlot {

    private ItemType type;
    private int count;
    private int maxCapacity = Constants.SLOT_MAX;

    /** @return true se o slot está vazio */
    public boolean isEmpty() {
        return type == null || count <= 0;
    }

    /** @return true se atingiu a capacidade máxima */
    public boolean isFull() {
        return count >= maxCapacity;
    }

    /** @param maxCapacity define a capacidade máxima deste slot */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = Math.max(Constants.SLOT_MAX, maxCapacity);
    }

    /** @return capacidade máxima atual do slot */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Verifica se o slot aceita mais uma unidade do tipo informado.
     *
     * @param item tipo de item
     * @return true se vazio ou se já contém o mesmo tipo e não está cheio
     */
    public boolean canAccept(ItemType item) {
        return isEmpty() || (type == item && !isFull());
    }

    /**
     * Adiciona uma unidade do item no slot.
     *
     * @param item tipo
     * @return true se conseguiu adicionar
     */
    public boolean addOne(ItemType item) {
        if (!canAccept(item)) {
            return false;
        }
        type = item;
        count++;
        return true;
    }

    /**
     * Remove uma unidade do slot e retorna o tipo removido.
     *
     * @return tipo removido, ou {@code null} se estava vazio
     */
    public ItemType removeOne() {
        if (isEmpty()) {
            return null;
        }
        ItemType out = type;
        count--;
        if (count <= 0) {
            clear();
        }
        return out;
    }

    /** Limpa completamente o slot. */
    public void clear() {
        type = null;
        count = 0;
    }

    /**
     * Define diretamente o conteúdo do slot (usado ao carregar jogo salvo ou estado inicial).
     *
     * @param type  tipo (ou null)
     * @param count quantidade
     */
    public void set(ItemType type, int count) {
        if (type == null || count <= 0) {
            clear();
        } else {
            this.type = type;
            this.count = Math.min(maxCapacity, count);
        }
    }

    /** @return tipo guardado (ou null se vazio) */
    public ItemType getType() {
        return type;
    }

    /** @return quantidade atual */
    public int getCount() {
        return count;
    }
}

