package com.slimefarm.entities.props;

import com.badlogic.gdx.utils.Array;
import com.slimefarm.economy.Market;
import com.slimefarm.entities.items.CrystalItem;
import com.slimefarm.world.World;

/**
 * Terminal eletrônico do "Mercado de Cristais" (canto superior direito da referência).
 * Permite tanto abrir a janela de vendas com a tecla E quanto atirar cristais diretamente nele.
 */
public class MarketTerminal extends Prop {

    private final Market market;
    private final Array<CrystalItem> incoming = new Array<>();

    /**
     * @param x      posição x (tiles)
     * @param y      posição y (tiles)
     * @param market sistema de mercado
     */
    public MarketTerminal(float x, float y, Market market) {
        super(x, y, 0.85f, true, "market_terminal");
        this.market = market;
        this.drawOffsetY = 4f;
    }

    @Override
    public void update(World world, float dt) {
        if (market == null) {
            return;
        }
        // Se um cristal for atirado perto do terminal, é vendido automaticamente
        world.findInRadius(pos, 1.25f, CrystalItem.class, incoming);
        for (CrystalItem c : incoming) {
            if (!c.isRemoved()) {
                market.sellOne(c.getType());
                c.remove();
            }
        }
    }
}

