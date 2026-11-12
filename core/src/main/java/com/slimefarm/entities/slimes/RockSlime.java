package com.slimefarm.entities.slimes;

import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.Player;
import com.slimefarm.world.World;

/**
 * Slime Pedra: cinza com espinhos minerais no topo.
 * Quando faminto/agitado, causa dano ao jogador ao encostar.
 */
public class RockSlime extends Slime {

    private final Array<Player> nearPlayers = new Array<>();

    /**
     * @param x posição x inicial
     * @param y posição y inicial
     */
    public RockSlime(float x, float y) {
        super(x, y, SlimeType.ROCK);
    }

    @Override
    protected void onCustomUpdate(World world, float dt) {
        if (!isAgitated()) {
            return;
        }
        world.findInRadius(pos, 0.75f, Player.class, nearPlayers);
        for (Player p : nearPlayers) {
            p.getStats().damage(getType().contactDamage);
        }
    }
}

