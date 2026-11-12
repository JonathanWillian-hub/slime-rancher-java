package com.slimefarm.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.Entity;

/**
 * Colisão simples em coordenadas de mundo:
 * <ul>
 *   <li>tiles não andáveis e diferença de elevação (penhascos);</li>
 *   <li>paredes retangulares (cercas, curral) registradas pelos props;</li>
 *   <li>entidades sólidas (círculos), ex.: árvores e pedras.</li>
 * </ul>
 * O movimento é resolvido eixo por eixo, permitindo "deslizar" pelas paredes.
 */
public class CollisionMap {

    private final World world;
    private final Array<Rectangle> walls = new Array<>();

    /** @param world mundo dono */
    public CollisionMap(World world) {
        this.world = world;
    }

    /**
     * Registra uma parede retangular (em tiles).
     *
     * @param rect retângulo (x, y, largura, altura)
     */
    public void addWall(Rectangle rect) {
        walls.add(rect);
    }

    /** Remove uma parede registrada (ex.: portão aberto). */
    public void removeWall(Rectangle rect) {
        walls.removeValue(rect, true);
    }

    /**
     * Move a entidade tentando cada eixo separadamente.
     *
     * @param e  entidade
     * @param dx deslocamento x (tiles)
     * @param dy deslocamento y (tiles)
     * @return true se houve colisão em algum eixo
     */
    public boolean move(Entity e, float dx, float dy) {
        boolean hit = false;
        float x = e.getPos().x;
        float y = e.getPos().y;
        if (dx != 0) {
            if (canOccupy(e, x + dx, y)) {
                x += dx;
            } else {
                hit = true;
            }
        }
        if (dy != 0) {
            if (canOccupy(e, x, y + dy)) {
                y += dy;
            } else {
                hit = true;
            }
        }
        e.getPos().set(x, y);
        return hit;
    }

    /**
     * Verifica se a entidade pode ficar no ponto (x, y).
     *
     * @param self entidade que está se movendo (ignorada nas colisões com entidades)
     * @param x    x destino
     * @param y    y destino
     * @return true se o ponto está livre
     */
    public boolean canOccupy(Entity self, float x, float y) {
        float r = self.getRadius();
        float currentElev = world.getElevationAt(self.getPos().x, self.getPos().y);
        // pontos de amostra: centro e 4 extremos do círculo
        if (!tileOk(x, y, currentElev) || !tileOk(x + r, y, currentElev) || !tileOk(x - r, y, currentElev)
                || !tileOk(x, y + r, currentElev) || !tileOk(x, y - r, currentElev)) {
            return false;
        }
        for (Rectangle w : walls) {
            if (circleHitsRect(x, y, r, w)) {
                return false;
            }
        }
        for (Entity other : world.getEntities()) {
            if (other == self || !other.isSolid() || other.isRemoved()) {
                continue;
            }
            float min = r + other.getRadius();
            if (other.getPos().dst2(x, y) < min * min) {
                return false;
            }
        }
        return true;
    }

    /** @return true se o tile em (x,y) é andável e na mesma elevação. */
    private boolean tileOk(float x, float y, float elevation) {
        Tile t = world.getTileMap().getAt(x, y);
        return t != null && t.isWalkable() && t.getElevation() == (int) elevation;
    }

    /** @return true se o círculo encosta no retângulo. */
    private static boolean circleHitsRect(float cx, float cy, float r, Rectangle rect) {
        float nx = Math.max(rect.x, Math.min(cx, rect.x + rect.width));
        float ny = Math.max(rect.y, Math.min(cy, rect.y + rect.height));
        float dx = cx - nx;
        float dy = cy - ny;
        return dx * dx + dy * dy < r * r;
    }

    /**
     * @param x ponto x
     * @param y ponto y
     * @return true se o ponto está dentro de alguma parede
     */
    public boolean insideWall(float x, float y) {
        for (Rectangle w : walls) {
            if (w.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    /** @return paredes registradas (somente leitura) */
    public Array<Rectangle> getWalls() {
        return walls;
    }
}
