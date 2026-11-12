package com.slimefarm.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.slimefarm.entities.Entity;
import com.slimefarm.entities.props.Corral;
import com.slimefarm.entities.slimes.Slime;
import com.slimefarm.world.World;

/**
 * Controla a barreira de vidro seletiva dos cercados ({@link Corral}):
 * <ul>
 *   <li>O {@link com.slimefarm.entities.Player} atravessa o vidro livremente;</li>
 *   <li>Slimes marcados como {@code corralled} ficam presos dentro do perímetro do cercado onde foram colocados;</li>
 *   <li>Slimes selvagens (fora do cercado) são bloqueados pelo lado de fora e não conseguem entrar
 *       sozinhos, exceto quando o jogador os atira para dentro ({@link Slime#isLaunchedByPlayer()}).</li>
 * </ul>
 */
public class CorralSystem {

    private static final float INNER_MARGIN = 0.45f;
    private static final float OUTER_MARGIN = 0.35f;
    private final Array<Corral> corrals = new Array<>();

    /** @param corral instância inicial do curral (pode ser null) */
    public CorralSystem(Corral corral) {
        if (corral != null) {
            this.corrals.add(corral);
        }
    }

    /** Adiciona um novo cercado ao sistema de contenção. */
    public void addCorral(Corral c) {
        if (c != null && !corrals.contains(c, true)) {
            corrals.add(c);
        }
    }

    /** Remove um cercado do sistema de contenção. */
    public void removeCorral(Corral c) {
        if (c != null) {
            corrals.removeValue(c, true);
        }
    }

    /**
     * Aplica a contenção interna e o bloqueio externo dos slimes em relação a todos os cercados.
     *
     * @param world mundo
     */
    public void update(World world) {
        if (corrals.size == 0) {
            return;
        }

        for (Entity e : world.getEntities()) {
            if (!(e instanceof Slime s) || s.isRemoved()) {
                continue;
            }
            float x = s.getPos().x;
            float y = s.getPos().y;

            if (s.isCorralled()) {
                // Encontra qual curral contém (ou é o mais próximo) do slime preso
                Corral owner = findBestCorral(s);
                if (owner != null) {
                    clampInside(s, owner.getBounds());
                }
            } else {
                // Slime selvagem: checa colisão com todos os cercados
                for (int i = 0; i < corrals.size; i++) {
                    Rectangle b = corrals.get(i).getBounds();
                    float outMinX = b.x - OUTER_MARGIN;
                    float outMaxX = b.x + b.width + OUTER_MARGIN;
                    float outMinY = b.y - OUTER_MARGIN;
                    float outMaxY = b.y + b.height + OUTER_MARGIN;

                    boolean insideExtended = x > outMinX && x < outMaxX && y > outMinY && y < outMaxY;
                    if (insideExtended) {
                        if (s.isLaunchedByPlayer() && b.contains(x, y)) {
                            s.setCorralled(true);
                            s.setLaunchedByPlayer(false);
                            break;
                        } else if (!s.isLaunchedByPlayer()) {
                            pushOutside(s, outMinX, outMaxX, outMinY, outMaxY);
                        }
                    }
                }
                if (!s.getHop().isAirborne(s.getZ())) {
                    s.setLaunchedByPlayer(false);
                }
            }
        }
    }

    private Corral findBestCorral(Slime s) {
        float x = s.getPos().x;
        float y = s.getPos().y;
        Corral closest = null;
        float minDst = Float.MAX_VALUE;

        for (int i = 0; i < corrals.size; i++) {
            Corral c = corrals.get(i);
            Rectangle b = c.getBounds();
            if (b.contains(x, y)) {
                return c;
            }
            float cx = b.x + b.width / 2f;
            float cy = b.y + b.height / 2f;
            float dst2 = s.getPos().dst2(cx, cy);
            if (dst2 < minDst) {
                minDst = dst2;
                closest = c;
            }
        }
        return closest;
    }

    private static void clampInside(Slime s, Rectangle b) {
        float inMinX = b.x + INNER_MARGIN;
        float inMaxX = b.x + b.width - INNER_MARGIN;
        float inMinY = b.y + INNER_MARGIN;
        float inMaxY = b.y + b.height - INNER_MARGIN;

        float x = s.getPos().x;
        float y = s.getPos().y;

        if (x < inMinX) {
            s.getPos().x = inMinX;
            s.getVelocity().x = Math.abs(s.getVelocity().x);
        } else if (x > inMaxX) {
            s.getPos().x = inMaxX;
            s.getVelocity().x = -Math.abs(s.getVelocity().x);
        }
        if (y < inMinY) {
            s.getPos().y = inMinY;
            s.getVelocity().y = Math.abs(s.getVelocity().y);
        } else if (y > inMaxY) {
            s.getPos().y = inMaxY;
            s.getVelocity().y = -Math.abs(s.getVelocity().y);
        }
    }

    /** Empurra um slime selvagem para a face externa mais próxima do vidro do curral. */
    private static void pushOutside(Slime s, float minX, float maxX, float minY, float maxY) {
        float x = s.getPos().x;
        float y = s.getPos().y;
        float dLeft = Math.abs(x - minX);
        float dRight = Math.abs(maxX - x);
        float dTop = Math.abs(y - minY);
        float dBottom = Math.abs(maxY - y);
        float min = Math.min(Math.min(dLeft, dRight), Math.min(dTop, dBottom));

        if (min == dLeft) {
            s.getPos().x = minX;
            s.getVelocity().x = -Math.abs(s.getVelocity().x);
        } else if (min == dRight) {
            s.getPos().x = maxX;
            s.getVelocity().x = Math.abs(s.getVelocity().x);
        } else if (min == dTop) {
            s.getPos().y = minY;
            s.getVelocity().y = -Math.abs(s.getVelocity().y);
        } else {
            s.getPos().y = maxY;
            s.getVelocity().y = Math.abs(s.getVelocity().y);
        }
    }

    /** @return curral inicial (ou o primeiro da lista) */
    public Corral getCorral() {
        return corrals.size > 0 ? corrals.first() : null;
    }

    /** @return lista completa de cercados */
    public Array<Corral> getCorrals() {
        return corrals;
    }
}
