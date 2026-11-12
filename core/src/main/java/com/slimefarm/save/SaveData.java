package com.slimefarm.save;

import java.util.ArrayList;

/**
 * Objeto serializável (POJO) com os dados do progresso do jogador para gravação em JSON:
 * moedas, dia atual, itens nos 8 slots da hotbar, slimes do curral, slimes descobertos
 * na Slimepedia e níveis comprados na Loja de Upgrades.
 */
public class SaveData {

    /** Saldo de moedas. */
    public int coins;
    /** Dia atual do mercado. */
    public int day;
    /** Nome do {@link com.slimefarm.entities.items.ItemType} em cada slot (ou null). */
    public String[] slotTypes;
    /** Quantidade em cada slot. */
    public int[] slotCounts;
    /** Lista de nomes de {@link com.slimefarm.entities.slimes.SlimeType} dentro do curral. */
    public ArrayList<String> corralSlimes = new ArrayList<>();
    /** Lista de espécies já capturadas na Slimepedia. */
    public ArrayList<String> capturedSpecies = new ArrayList<>();
    /** Lista de limites dos cercados construídos: [x, y, w, h]. */
    public ArrayList<float[]> builtCorrals = new ArrayList<>();
    /** Níveis comprados na Loja de Upgrades (na ordem de {@link com.slimefarm.economy.UpgradeType}). */
    public int[] upgradeLevels;

    /** Construtor vazio exigido pelo serializador JSON do libGDX. */
    public SaveData() {
    }
}
