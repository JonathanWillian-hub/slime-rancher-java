package com.slimefarm.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.slimefarm.economy.Market;
import com.slimefarm.economy.UpgradeShop;
import com.slimefarm.economy.UpgradeType;
import com.slimefarm.economy.Wallet;
import com.slimefarm.entities.Entity;
import com.slimefarm.entities.Player;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.entities.slimes.SlimeType;
import com.slimefarm.inventory.Inventory;
import com.slimefarm.inventory.InventorySlot;
import com.slimefarm.player.VacuumGun;
import com.slimefarm.progression.Slimepedia;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.World;

/**
 * Salva e carrega o progresso completo do jogo em um arquivo JSON local ({@code slimefarm_save.json}).
 */
public class SaveManager {

    private static final String SAVE_FILE = "slimefarm_save.json";
    private final Json json = new Json();

    /** @return true se já existe um arquivo de save gravado */
    public boolean hasSave() {
        return getFile().exists();
    }

    /**
     * Grava o estado atual da partida em disco.
     *
     * @param wallet     carteira de moedas
     * @param market     mercado
     * @param shop       loja de upgrades
     * @param slimepedia registro da Slimepedia
     * @param inventory  inventário do jogador
     * @param world      mundo
     * @param corral     curral de vidro
     */
    public void save(Wallet wallet, Market market, UpgradeShop shop, Slimepedia slimepedia,
                     Inventory inventory, World world, Corral corral) {
        SaveData data = new SaveData();
        data.coins = wallet.getCoins();
        data.day = market.getDay();
        data.slotTypes = new String[inventory.size()];
        data.slotCounts = new int[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            InventorySlot s = inventory.getSlot(i);
            if (!s.isEmpty()) {
                data.slotTypes[i] = s.getType().name();
                data.slotCounts[i] = s.getCount();
            }
        }
        if (corral != null) {
            for (Entity e : world.getEntities()) {
                if (e instanceof Slime slime && !slime.isRemoved() && slime.isCorralled()) {
                    data.corralSlimes.add(slime.getType().name());
                }
            }
        }
        if (slimepedia != null) {
            for (SlimeType t : SlimeType.values()) {
                if (slimepedia.isCaptured(t)) {
                    data.capturedSpecies.add(t.name());
                }
            }
        }
        if (shop != null) {
            UpgradeType[] upTypes = UpgradeType.values();
            data.upgradeLevels = new int[upTypes.length];
            for (int i = 0; i < upTypes.length; i++) {
                data.upgradeLevels[i] = shop.getLevel(upTypes[i]);
            }
        }
        getFile().writeString(json.prettyPrint(data), false, "UTF-8");
    }

    /**
     * Carrega o arquivo salvo e aplica no estado da partida.
     *
     * @param wallet     carteira
     * @param market     mercado
     * @param shop       loja de upgrades
     * @param slimepedia registro da Slimepedia
     * @param player     jogador
     * @param world      mundo
     * @param corral     curral
     * @return true se carregou com sucesso
     */
    public boolean load(Wallet wallet, Market market, UpgradeShop shop, Slimepedia slimepedia,
                        Player player, World world, Corral corral) {
        FileHandle file = getFile();
        if (!file.exists()) {
            return false;
        }
        try {
            SaveData data = json.fromJson(SaveData.class, file.readString("UTF-8"));
            wallet.setCoins(data.coins);
            market.setDay(data.day);

            if (shop != null && data.upgradeLevels != null) {
                UpgradeType[] upTypes = UpgradeType.values();
                int n = Math.min(upTypes.length, data.upgradeLevels.length);
                for (int i = 0; i < n; i++) {
                    shop.setLevel(upTypes[i], data.upgradeLevels[i]);
                }
                shop.applyAll(player);
            }

            Inventory inventory = player.getInventory();
            if (data.slotTypes != null && data.slotCounts != null) {
                int n = Math.min(inventory.size(), data.slotTypes.length);
                for (int i = 0; i < n; i++) {
                    if (data.slotTypes[i] != null && data.slotCounts[i] > 0) {
                        inventory.getSlot(i).set(ItemType.valueOf(data.slotTypes[i]), data.slotCounts[i]);
                    } else {
                        inventory.getSlot(i).clear();
                    }
                }
            }

            if (slimepedia != null && data.capturedSpecies != null) {
                slimepedia.clear();
                for (String name : data.capturedSpecies) {
                    slimepedia.registerCapture(SlimeType.valueOf(name));
                }
            }

            if (corral != null && data.corralSlimes != null) {
                Rectangle b = corral.getBounds();
                for (Entity e : world.getEntities()) {
                    if (e instanceof Slime s && s.isCorralled()) {
                        s.remove();
                    }
                }
                for (String name : data.corralSlimes) {
                    SlimeType st = SlimeType.valueOf(name);
                    float sx = RandomUtils.range(b.x + 1.2f, b.x + b.width - 1.2f);
                    float sy = RandomUtils.range(b.y + 1.2f, b.y + b.height - 1.2f);
                    Slime s = VacuumGun.createSlime(st, sx, sy);
                    s.setCorralled(true);
                    world.add(s);
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private FileHandle getFile() {
        return Gdx.files.local(SAVE_FILE);
    }
}
