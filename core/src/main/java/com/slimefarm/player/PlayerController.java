package com.slimefarm.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.slimefarm.entities.Player;
import com.slimefarm.input.InputState;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;

/**
 * Converte o {@link InputState} em ações do {@link Player}:
 * movimento isométrico alinhado à tela (WASD), corrida com gasto de energia
 * e mira na posição do cursor do mouse.
 */
public class PlayerController {

    private final Player player;
    private final InputState input;
    private final Vector2 tmpDir = new Vector2();
    private final Vector2 tmpWorld = new Vector2();
    private final Vector3 tmpMouse = new Vector3();

    /**
     * @param player entidade do jogador
     * @param input  estado de entrada
     */
    public PlayerController(Player player, InputState input) {
        this.player = player;
        this.input = input;
    }

    /**
     * Atualiza velocidade e mira do jogador para o quadro atual.
     *
     * @param dt       tempo do quadro (s)
     * @param viewport viewport do mundo (usada para desprojetar o mouse)
     */
    public void update(float dt, Viewport viewport) {
        float dx = (input.right ? 1f : 0f) - (input.left ? 1f : 0f);
        float dy = (input.up ? 1f : 0f) - (input.down ? 1f : 0f);

        if (dx == 0f && dy == 0f) {
            player.getVelocity().setZero();
        } else {
            IsoUtils.screenDirToWorld(dx, dy, tmpDir);
            float speed = Constants.PLAYER_SPEED * player.getStats().getSpeedMultiplier();
            if (input.run && player.getStats().consumeEnergy(Constants.RUN_ENERGY_COST * dt)) {
                speed *= Constants.PLAYER_RUN_MULT;
            }
            player.getVelocity().set(tmpDir).scl(speed);
        }

        // Converte posição do mouse na janela -> coordenadas da câmera -> coordenadas de mundo
        tmpMouse.set(input.mouseX, input.mouseY, 0f);
        viewport.unproject(tmpMouse);
        float groundElevationPx = player.getGroundZ() * Constants.Z_PIXELS;
        IsoUtils.screenToWorld(tmpMouse.x, tmpMouse.y - groundElevationPx, tmpWorld);
        player.setAimWorld(tmpWorld.x, tmpWorld.y);
    }
}

