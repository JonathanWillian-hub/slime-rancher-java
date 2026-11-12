package com.slimefarm.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.Entity;
import com.slimefarm.entities.Player;
import com.slimefarm.entities.items.CrystalItem;
import com.slimefarm.entities.items.FoodItem;
import com.slimefarm.entities.items.ItemEntity;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.entities.slimes.CatSlime;
import com.slimefarm.entities.slimes.GoldSlime;
import com.slimefarm.entities.slimes.HoneySlime;
import com.slimefarm.entities.slimes.PhosphorSlime;
import com.slimefarm.entities.slimes.PinkSlime;
import com.slimefarm.entities.slimes.RockSlime;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.input.InputState;
import com.slimefarm.inventory.Inventory;
import com.slimefarm.progression.Slimepedia;
import com.slimefarm.util.Constants;
import com.slimefarm.world.World;

/**
 * Aspirador (Vac Gun) do jogador:
 * <ul>
 *   <li><b>Botão esquerdo</b>: puxa itens e slimes dentro de um cone frontal, guarda no {@link Inventory}
 *       e registra a espécie capturada na {@link Slimepedia};</li>
 *   <li><b>Botão direito</b>: dispara 1 unidade do slot selecionado de volta para o mundo.</li>
 * </ul>
 */
public class VacuumGun {

    private final Player player;
    private final InputState input;
    private final Slimepedia slimepedia;
    private float shootTimer;

    private final Vector2 nozzle = new Vector2();
    private final Vector2 aimDir = new Vector2();
    private final Vector2 toEntity = new Vector2();
    private final Array<Entity> nearby = new Array<>();

    /**
     * @param player     jogador dono do aspirador
     * @param input      estado de entrada
     * @param slimepedia registro de descoberta de slimes
     */
    public VacuumGun(Player player, InputState input, Slimepedia slimepedia) {
        this.player = player;
        this.input = input;
        this.slimepedia = slimepedia;
    }

    /**
     * Atualiza seleção de slots, aspiração e disparo.
     *
     * @param world mundo
     * @param dt    tempo do quadro (s)
     */
    public void update(World world, float dt) {
        Inventory inv = player.getInventory();
        int keySlot = input.consumeSlotKey();
        if (keySlot >= 0) {
            inv.select(keySlot);
        }
        int scroll = input.consumeScroll();
        if (scroll != 0) {
            inv.cycle(scroll);
        }

        if (shootTimer > 0f) {
            shootTimer -= dt;
        }

        player.getNozzleWorldPos(nozzle);
        aimDir.set(player.getAimWorld()).sub(player.getPos());
        if (aimDir.isZero()) {
            aimDir.set(1f, 0f);
        } else {
            aimDir.nor();
        }

        boolean activeVacuum = input.vacuumHeld
                && player.getStats().consumeEnergy(Constants.VACUUM_ENERGY_COST * dt);
        player.setVacuuming(activeVacuum);
        if (activeVacuum) {
            applyVacuumCone(world, dt);
        }

        if (input.shootHeld && shootTimer <= 0f) {
            if (shootSelected(world)) {
                shootTimer = Constants.SHOOT_COOLDOWN;
            }
        }
    }

    /** Puxa e coleta itens/slimes dentro do cone de sucção (aplicando bônus de upgrades). */
    private void applyVacuumCone(World world, float dt) {
        float range = Constants.VACUUM_RANGE + player.getStats().getVacuumRangeBonus();
        float pull = Constants.VACUUM_PULL * player.getStats().getVacuumPullMultiplier();
        float cosHalf = MathUtils.cosDeg(Constants.VACUUM_HALF_ANGLE);

        world.findInRadius(nozzle, range, Entity.class, nearby);
        for (Entity e : nearby) {
            if (e == player || e.isRemoved()) {
                continue;
            }
            toEntity.set(e.getPos()).sub(nozzle);
            float dist = toEntity.len();
            if (dist > range || dist < 0.001f) {
                continue;
            }
            float dot = (toEntity.x * aimDir.x + toEntity.y * aimDir.y) / dist;
            if (dot < cosHalf) {
                continue;
            }
            if (e instanceof ItemEntity item && item.canBeVacuumed()) {
                if (dist <= Constants.VACUUM_COLLECT_DIST) {
                    if (player.getInventory().add(item.getType())) {
                        item.remove();
                    }
                } else {
                    item.pullTowards(nozzle, pull, dt);
                }
            } else if (e instanceof Slime slime && slime.canBeVacuumed()) {
                if (dist <= Constants.VACUUM_COLLECT_DIST) {
                    if (player.getInventory().add(slime.getType().inventoryItem)) {
                        if (slimepedia != null) {
                            slimepedia.registerCapture(slime.getType());
                        }
                        slime.remove();
                    }
                } else {
                    slime.pullTowards(nozzle, pull * 0.85f, dt);
                }
            }
        }
    }

    /**
     * Dispara 1 unidade do slot selecionado na direção da mira.
     *
     * @param world mundo
     * @return true se havia algo para disparar
     */
    private boolean shootSelected(World world) {
        ItemType type = player.getInventory().popSelected();
        if (type == null) {
            return false;
        }
        float vx = aimDir.x * Constants.SHOOT_SPEED;
        float vy = aimDir.y * Constants.SHOOT_SPEED;

        if (type.isSlime()) {
            SlimeType st = SlimeType.fromInventoryItem(type);
            Slime slime = createSlime(st, nozzle.x, nozzle.y);
            slime.launch(vx * 0.7f, vy * 0.7f, 4.0f);
            world.add(slime);
        } else {
            ItemEntity item = type.isCrystal()
                    ? new CrystalItem(nozzle.x, nozzle.y, type)
                    : new FoodItem(nozzle.x, nozzle.y, type);
            item.launch(vx, vy, 3.4f, 0.45f);
            world.add(item);
        }
        return true;
    }

    /** Instancia a subclasse correta de {@link Slime} para um {@link SlimeType}. */
    public static Slime createSlime(SlimeType type, float x, float y) {
        return switch (type) {
            case CAT -> new CatSlime(x, y);
            case ROCK -> new RockSlime(x, y);
            case HONEY -> new HoneySlime(x, y);
            case PHOSPHOR -> new PhosphorSlime(x, y);
            case GOLD -> new GoldSlime(x, y);
            case PINK -> new PinkSlime(x, y);
        };
    }
}
