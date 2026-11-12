package com.slimefarm.economy;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.slimefarm.entities.items.ItemType;
import com.slimefarm.util.Constants;
import com.slimefarm.util.RandomUtils;

/**
 * Tabela de preços atuais de cada tipo de cristal no mercado.
 * Os preços flutuam diariamente em até ±{@link Constants#PRICE_VARIATION}.
 */
public class PriceTable {

    private final ObjectIntMap<ItemType> prices = new ObjectIntMap<>();

    /** Cria a tabela com os preços base iniciais. */
    public PriceTable() {
        resetToBase();
    }

    /** Restaura os preços base definidos em {@link ItemType}. */
    public void resetToBase() {
        for (ItemType t : ItemType.values()) {
            if (t.isCrystal()) {
                prices.put(t, t.basePrice);
            }
        }
    }

    /** Sorteia novos preços diários dentro da faixa ±{@link Constants#PRICE_VARIATION}. */
    public void rollDailyPrices() {
        for (ItemType t : ItemType.values()) {
            if (t.isCrystal()) {
                float factor = 1f + RandomUtils.range(-Constants.PRICE_VARIATION, Constants.PRICE_VARIATION);
                int price = Math.max(1, Math.round(t.basePrice * factor));
                prices.put(t, price);
            }
        }
    }

    /**
     * Consulta o preço atual de um item.
     *
     * @param type tipo de cristal
     * @return preço em moedas (0 se não for vendável)
     */
    public int getPrice(ItemType type) {
        return prices.get(type, type != null ? type.basePrice : 0);
    }
}

