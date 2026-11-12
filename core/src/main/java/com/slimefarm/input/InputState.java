package com.slimefarm.input;

/**
 * Estado atual dos controles do jogador (teclado e mouse).
 * Atualizado pelo {@link GameInputProcessor} e lido pelos controladores.
 */
public class InputState {

    /** Teclas de movimento. */
    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;
    public boolean run;

    /** Botões do mouse (mantidos pressionados). */
    public boolean vacuumHeld;
    public boolean shootHeld;

    /** Ações disparadas uma vez ao apertar. */
    private boolean interactPressed;
    private boolean pausePressed;
    private int slotKeyPressed = -1;
    private int scrollDelta;

    /** Posição do mouse na janela (pixels, Y do libGDX: 0 = topo). */
    public int mouseX;
    public int mouseY;

    /** Marca que a tecla de interação (E) foi pressionada. */
    public void triggerInteract() {
        interactPressed = true;
    }

    /** Consome o aperto de E (retorna true só uma vez por aperto). */
    public boolean consumeInteract() {
        boolean v = interactPressed;
        interactPressed = false;
        return v;
    }

    /** Marca que Esc foi pressionado. */
    public void triggerPause() {
        pausePressed = true;
    }

    /** Consome o aperto de Esc. */
    public boolean consumePause() {
        boolean v = pausePressed;
        pausePressed = false;
        return v;
    }

    /** Registra tecla numérica 1..8 (índice 0..7). */
    public void setSlotKey(int slot) {
        slotKeyPressed = slot;
    }

    /** Consome a tecla de slot (-1 se nenhuma foi apertada). */
    public int consumeSlotKey() {
        int s = slotKeyPressed;
        slotKeyPressed = -1;
        return s;
    }

    /** Acumula rolagem da roda do mouse. */
    public void addScroll(int delta) {
        scrollDelta += delta;
    }

    /** Consome a rolagem acumulada. */
    public int consumeScroll() {
        int s = scrollDelta;
        scrollDelta = 0;
        return s;
    }
}
