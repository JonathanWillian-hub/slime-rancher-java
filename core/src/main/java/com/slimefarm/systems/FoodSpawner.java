package com.slimefarm.systems;

import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.Entity;
import com.slimefarm.entities.items.FoodItem;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.entities.props.Bush;
import com.slimefarm.entities.props.Tree;
import com.slimefarm.util.Constants;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.Tile;
import com.slimefarm.world.World;

/**
 * Faz frutas e vegetais nascerem periodicamente perto de árvores e arbustos,
 * respeitando o limite {@link Constants#MAX_FOOD}.
 */
public class FoodSpawner {

    private static final float SPAWN_INTERVAL = 5f;
    private float timer = 2f;
    private final Array<Entity> sources = new Array<>();

    /**
     * Popula o mapa inicialmente com algumas comidas ao redor das árvores e arbustos.
     *
     * @param world mundo
     * @param count quantidade inicial
     */
    public void spawnInitial(World world, int count) {
        for (int i = 0; i < count; i++) {
            trySpawnOne(world);
        }
    }

    /**
     * Atualiza o temporizador e gera novas frutas se estiver abaixo do limite.
     *
     * @param world mundo
     * @param dt    tempo do quadro (s)
     */
    public void update(World world, float dt) {
        timer -= dt;
        if (timer <= 0f) {
            timer = SPAWN_INTERVAL;
            if (world.count(FoodItem.class) < Constants.MAX_FOOD) {
                trySpawnOne(world);
            }
        }
    }

    private void trySpawnOne(World world) {
        sources.clear();
        for (Entity e : world.getEntities()) {
            if ((e instanceof Tree || e instanceof Bush) && e.getGroundZ() == 0f) {
                sources.add(e);
            }
        }
        if (sources.isEmpty()) {
            return;
        }
        Entity src = sources.random();
        float angle = RandomUtils.range(0f, 6.283f);
        float dist = RandomUtils.range(0.9f, 1.8f);
        float x = src.getPos().x + (float) Math.cos(angle) * dist;
        float y = src.getPos().y + (float) Math.sin(angle) * dist;
        Tile t = world.getTileMap().getAt(x, y);
        if (t == null || !t.isWalkable() || t.getElevation() != 0 || world.getCollision().insideWall(x, y)) {
            return;
        }
        ItemType foodType = (src instanceof Tree)
                ? RandomUtils.pick(ItemType.APPLE, ItemType.STRAWBERRY)
                : RandomUtils.pick(ItemType.STRAWBERRY, ItemType.CARROT);
        world.add(new FoodItem(x, y, foodType));
    }
}

