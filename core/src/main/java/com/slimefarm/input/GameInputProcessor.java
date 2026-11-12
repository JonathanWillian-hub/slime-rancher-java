package com.slimefarm.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * Recebe eventos de teclado e mouse do libGDX e atualiza o {@link InputState}.
 */
public class GameInputProcessor extends InputAdapter {

    private final InputState state;

    /** @param state estado compartilhado */
    public GameInputProcessor(InputState state) {
        this.state = state;
    }

    @Override
    public boolean keyDown(int keycode) {
        setMoveKey(keycode, true);
        if (keycode == Input.Keys.E) {
            state.triggerInteract();
        } else if (keycode == Input.Keys.ESCAPE) {
            state.triggerPause();
        } else if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_8) {
            state.setSlotKey(keycode - Input.Keys.NUM_1);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        setMoveKey(keycode, false);
        return true;
    }

    private void setMoveKey(int keycode, boolean down) {
        switch (keycode) {
            case Input.Keys.W, Input.Keys.UP -> state.up = down;
            case Input.Keys.S, Input.Keys.DOWN -> state.down = down;
            case Input.Keys.A, Input.Keys.LEFT -> state.left = down;
            case Input.Keys.D, Input.Keys.RIGHT -> state.right = down;
            case Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> state.run = down;
            default -> {
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        state.mouseX = screenX;
        state.mouseY = screenY;
        if (button == Input.Buttons.LEFT) {
            state.vacuumHeld = true;
        } else if (button == Input.Buttons.RIGHT) {
            state.shootHeld = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        state.mouseX = screenX;
        state.mouseY = screenY;
        if (button == Input.Buttons.LEFT) {
            state.vacuumHeld = false;
        } else if (button == Input.Buttons.RIGHT) {
            state.shootHeld = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        state.mouseX = screenX;
        state.mouseY = screenY;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        state.mouseX = screenX;
        state.mouseY = screenY;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY != 0) {
            state.addScroll(amountY > 0 ? 1 : -1);
        }
        return true;
    }
}
