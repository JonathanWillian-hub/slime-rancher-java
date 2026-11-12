package com.slimefarm.inventory;

import com.slimefarm.entities.items.ItemType;
import com.slimefarm.util.Constants;

/**
 * Inventário da hotbar do jogador ({@link Constants#INVENTORY_SLOTS} slots).
 * Prioriza empilhar em slots que já têm o mesmo item antes de ocupar um slot vazio.
 */
public class Inventory {

    private final InventorySlot[] slots = new InventorySlot[Constants.INVENTORY_SLOTS];
    private int selectedIndex;

    /** Cria o inventário vazio. */
    public Inventory() {
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new InventorySlot();
        }
    }

    /**
     * Tenta adicionar 1 unidade do item: primeiro em um slot que já tenha o mesmo tipo,
     * depois no primeiro slot vazio disponível.
     *
     * @param item tipo de item
     * @return true se foi adicionado
     */
    public boolean add(ItemType item) {
        for (InventorySlot s : slots) {
            if (!s.isEmpty() && s.getType() == item && !s.isFull()) {
                return s.addOne(item);
            }
        }
        for (InventorySlot s : slots) {
            if (s.isEmpty()) {
                return s.addOne(item);
            }
        }
        return false;
    }

    /**
     * Remove 1 unidade do slot atualmente selecionado.
     *
     * @return tipo removido, ou {@code null} se o slot selecionado estava vazio
     */
    public ItemType popSelected() {
        return getSelectedSlot().removeOne();
    }

    /**
     * Remove 1 unidade de um tipo específico (procura em todos os slots).
     *
     * @param type tipo procurado
     * @return true se encontrou e removeu
     */
    public boolean removeOneOf(ItemType type) {
        for (InventorySlot s : slots) {
            if (!s.isEmpty() && s.getType() == type) {
                s.removeOne();
                return true;
            }
        }
        return false;
    }

    /**
     * Conta o total de unidades de um tipo somando todos os slots.
     *
     * @param type tipo
     * @return quantidade total
     */
    public int count(ItemType type) {
        int total = 0;
        for (InventorySlot s : slots) {
            if (!s.isEmpty() && s.getType() == type) {
                total += s.getCount();
            }
        }
        return total;
    }

    /**
     * Seleciona um slot pelo índice (0..INVENTORY_SLOTS-1).
     *
     * @param index índice
     */
    public void select(int index) {
        if (index >= 0 && index < slots.length) {
            selectedIndex = index;
        }
    }

    /**
     * Desloca a seleção ciclicamente (ex.: roda do mouse).
     *
     * @param delta deslocamento (+1 ou -1)
     */
    public void cycle(int delta) {
        int n = slots.length;
        selectedIndex = ((selectedIndex + delta) % n + n) % n;
    }

    /** @return slot atualmente selecionado */
    public InventorySlot getSelectedSlot() {
        return slots[selectedIndex];
    }

    /** @return índice do slot selecionado (0..7) */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /** @return slot na posição {@code index} */
    public InventorySlot getSlot(int index) {
        return slots[index];
    }

    /** @return quantidade total de slots */
    public int size() {
        return slots.length;
    }

    /**
     * Atualiza a capacidade máxima de todos os slots da hotbar (usado pela Loja de Upgrades).
     *
     * @param maxCapacity nova capacidade por slot
     */
    public void setSlotMaxCapacity(int maxCapacity) {
        for (InventorySlot s : slots) {
            s.setMaxCapacity(maxCapacity);
        }
    }
}

