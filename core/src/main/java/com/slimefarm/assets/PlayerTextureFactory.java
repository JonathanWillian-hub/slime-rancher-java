package com.slimefarm.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Gera os sprites originais do jogador (explorador espacial com mochila e aspirador)
 * em 2 orientações verticais (frente/costas) e 3 quadros de caminhada (parado, passo E, passo D).
 * O espelhamento horizontal é feito no momento do desenho.
 */
public final class PlayerTextureFactory implements Assets.LoadTask {

    private static final Color OUTLINE = Color.valueOf("1B222C");
    private static final Color SUIT = Color.valueOf("E6ECF5");
    private static final Color SUIT_SHADE = Color.valueOf("B0BDCE");
    private static final Color PANTS = Color.valueOf("4C5B70");
    private static final Color BOOTS = Color.valueOf("3A2E28");
    private static final Color VISOR = Color.valueOf("66E3FF");
    private static final Color PACK = Color.valueOf("C47A3B");
    private static final Color PACK_DARK = Color.valueOf("8E5222");
    private static final Color GUN = Color.valueOf("D99B26");
    private static final Color GUN_METAL = Color.valueOf("5B697A");

    @Override
    public void load(Assets assets) {
        for (int f = 0; f < 3; f++) {
            assets.register("player_down_" + f, TextureFactory.toTexture(drawPlayer(false, f)));
            assets.register("player_up_" + f, TextureFactory.toTexture(drawPlayer(true, f)));
        }
    }

    private static Pixmap drawPlayer(boolean up, int frame) {
        int w = 40;
        int h = 48;
        Pixmap p = TextureFactory.newPixmap(w, h);
        int cx = w / 2 - 2;
        int bob = (frame == 0) ? 0 : -1;

        int legL = (frame == 1) ? -2 : (frame == 2 ? 2 : 0);
        int legR = -legL;

        // pernas e botas
        drawLeg(p, cx - 4, 36 + legL, PANTS, BOOTS);
        drawLeg(p, cx + 2, 36 + legR, PANTS, BOOTS);

        // mochila (se de frente, fica atrás do corpo à esquerda)
        if (!up) {
            TextureFactory.rect(p, cx - 11, 18 + bob, 6, 15, OUTLINE);
            TextureFactory.rect(p, cx - 10, 19 + bob, 4, 13, PACK);
            TextureFactory.rect(p, cx - 10, 26 + bob, 4, 5, PACK_DARK);
        }

        // tronco
        TextureFactory.ellipseOutlined(p, cx, 26 + bob, 7, 9, SUIT, OUTLINE);
        TextureFactory.ellipse(p, cx - 1, 25 + bob, 5, 6, SUIT_SHADE);

        // mochila vista de costas
        if (up) {
            TextureFactory.rect(p, cx - 7, 18 + bob, 12, 15, OUTLINE);
            TextureFactory.rect(p, cx - 6, 19 + bob, 10, 13, PACK);
            TextureFactory.rect(p, cx - 5, 21 + bob, 8, 4, VISOR);
            TextureFactory.rect(p, cx - 6, 27 + bob, 10, 4, PACK_DARK);
        }

        // capacete
        TextureFactory.ellipseOutlined(p, cx, 12 + bob, 8, 8, SUIT, OUTLINE);
        if (!up) {
            TextureFactory.ellipseOutlined(p, cx + 2, 12 + bob, 5, 4, VISOR, OUTLINE);
            TextureFactory.pixel(p, cx + 1, 10 + bob, Color.WHITE);
        } else {
            TextureFactory.ellipse(p, cx, 12 + bob, 6, 6, SUIT_SHADE);
        }

        // aspirador (apontando para a direita)
        int gy = 25 + bob;
        TextureFactory.rect(p, cx + 2, gy - 3, 14, 7, OUTLINE);
        TextureFactory.rect(p, cx + 3, gy - 2, 8, 5, GUN);
        TextureFactory.rect(p, cx + 11, gy - 3, 5, 7, GUN_METAL);
        TextureFactory.rect(p, cx + 15, gy - 4, 3, 9, OUTLINE);
        TextureFactory.rect(p, cx + 15, gy - 3, 2, 7, VISOR);

        return p;
    }

    private static void drawLeg(Pixmap p, int x, int y, Color pants, Color boot) {
        TextureFactory.rect(p, x - 1, y - 5, 5, 9, OUTLINE);
        TextureFactory.rect(p, x, y - 4, 3, 5, pants);
        TextureFactory.rect(p, x, y + 1, 3, 3, boot);
    }
}

