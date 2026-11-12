package com.slimefarm.entities.props;

import com.badlogic.gdx.math.Rectangle;
import com.slimefarm.world.World;

/**
 * Um pedaço (1 tile) de cerca de madeira, alinhado ao eixo X ou Y do mundo.
 * Ao ser colocado registra uma parede fina no {@link com.slimefarm.world.CollisionMap}.
 */
public class Fence extends Prop {

    /** Espessura da parede de colisão (tiles). */
    private static final float THICKNESS = 0.2f;

    private final boolean alongX;

    /**
     * @param tileX  coluna do tile
     * @param tileY  linha do tile
     * @param alongX true = cerca segue o eixo X (na tela: diagonal descendo para a direita)
     */
    public Fence(int tileX, int tileY, boolean alongX) {
        super(tileX + 0.5f, tileY + 0.5f, 0.1f, false, alongX ? "fence_x" : "fence_y");
        this.alongX = alongX;
        this.drawOffsetY = 9f;
    }

    @Override
    public void onPlaced(World world) {
        float x = pos.x;
        float y = pos.y;
        Rectangle r = alongX
                ? new Rectangle(x - 0.5f, y - THICKNESS / 2f, 1f, THICKNESS)
                : new Rectangle(x - THICKNESS / 2f, y - 0.5f, THICKNESS, 1f);
        world.getCollision().addWall(r);
    }

    /** @return true se a cerca segue o eixo X */
    public boolean isAlongX() {
        return alongX;
    }
}
