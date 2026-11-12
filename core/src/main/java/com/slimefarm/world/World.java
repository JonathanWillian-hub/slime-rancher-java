package com.slimefarm.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.Entity;

/**
 * O mundo do jogo: o chão ({@link TileMap}) e todas as {@link Entity entidades}.
 * Responsável por atualizar as entidades e por responder consultas espaciais simples.
 */
public class World {

    private final TileMap tileMap;
    private final Array<Entity> entities = new Array<>();
    private final Array<Entity> toAdd = new Array<>();
    private final CollisionMap collision;

    /** @param tileMap chão já montado */
    public World(TileMap tileMap) {
        this.tileMap = tileMap;
        this.collision = new CollisionMap(this);
    }

    /**
     * Agenda uma entidade para entrar no mundo (entra no próximo update, seguro durante iteração).
     *
     * @param e entidade
     * @param <T> tipo concreto
     * @return a própria entidade
     */
    public <T extends Entity> T add(T e) {
        toAdd.add(e);
        updateGround(e);
        return e;
    }

    /** Adiciona imediatamente (usar apenas na montagem do mapa). */
    public <T extends Entity> T addNow(T e) {
        entities.add(e);
        updateGround(e);
        return e;
    }

    /**
     * Atualiza todas as entidades, insere as novas e remove as marcadas.
     *
     * @param dt tempo do quadro (s)
     */
    public void update(float dt) {
        if (toAdd.size > 0) {
            entities.addAll(toAdd);
            toAdd.clear();
        }
        for (int i = 0; i < entities.size; i++) {
            Entity e = entities.get(i);
            if (!e.isRemoved()) {
                e.update(this, dt);
                updateGround(e);
            }
        }
        for (int i = entities.size - 1; i >= 0; i--) {
            if (entities.get(i).isRemoved()) {
                entities.removeIndex(i);
            }
        }
    }

    /** Atualiza a elevação do terreno sob a entidade. */
    private void updateGround(Entity e) {
        e.setGroundZ(getElevationAt(e.getPos().x, e.getPos().y));
    }

    /** @return elevação do terreno no ponto (0 se fora do mapa) */
    public float getElevationAt(float x, float y) {
        Tile t = tileMap.getAt(x, y);
        return t == null ? 0f : t.getElevation();
    }

    /**
     * Busca entidades de um tipo dentro de um raio.
     *
     * @param center centro (mundo)
     * @param radius raio (tiles)
     * @param type   classe desejada
     * @param out    lista de saída (é limpa antes)
     * @param <T>    tipo
     * @return a própria lista {@code out}
     */
    public <T extends Entity> Array<T> findInRadius(Vector2 center, float radius, Class<T> type, Array<T> out) {
        out.clear();
        float r2 = radius * radius;
        for (Entity e : entities) {
            if (!e.isRemoved() && type.isInstance(e) && e.getPos().dst2(center) <= r2) {
                out.add(type.cast(e));
            }
        }
        return out;
    }

    /**
     * Conta entidades de um tipo.
     *
     * @param type classe
     * @return quantidade
     */
    public int count(Class<? extends Entity> type) {
        int n = 0;
        for (Entity e : entities) {
            if (!e.isRemoved() && type.isInstance(e)) {
                n++;
            }
        }
        for (Entity e : toAdd) {
            if (type.isInstance(e)) {
                n++;
            }
        }
        return n;
    }

    /** @return lista de entidades (não modificar diretamente) */
    public Array<Entity> getEntities() {
        return entities;
    }

    /** @return chão */
    public TileMap getTileMap() {
        return tileMap;
    }

    /** @return sistema de colisão */
    public CollisionMap getCollision() {
        return collision;
    }
}

