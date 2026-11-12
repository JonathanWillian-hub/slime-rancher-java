package com.slimefarm.entities.slimes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.slimefarm.ai.HopMovement;
import com.slimefarm.ai.SlimeBrain;
import com.slimefarm.assets.Assets;
import com.slimefarm.entities.LivingEntity;
import com.slimefarm.entities.items.FoodItem;
import com.slimefarm.util.Constants;
import com.slimefarm.util.IsoUtils;
import com.slimefarm.util.RandomUtils;
import com.slimefarm.world.World;

/**
 * Classe abstrata base para todos os slimes do jogo.
 * Gerencia fome, estado emocional ({@link SlimeState}), pulinhos ({@link HopMovement}),
 * alimentação e agendamento de produção de cristais.
 */
public abstract class Slime extends LivingEntity {

    private final SlimeType type;
    private final HopMovement hop = new HopMovement();
    private final SlimeBrain brain = new SlimeBrain();

    private SlimeState state = SlimeState.IDLE;
    private float hunger;
    private float happyTimer;
    private int pendingCrystals;
    private float crystalTimer;
    private float pickupCooldown;
    private boolean facingLeft;
    private boolean corralled;
    private boolean launchedByPlayer;

    /**
     * @param x    posição x inicial
     * @param y    posição y inicial
     * @param type tipo do slime
     */
    protected Slime(float x, float y, SlimeType type) {
        super(x, y, 0.32f);
        this.type = type;
        this.hunger = RandomUtils.range(0.35f, 0.65f);
    }

    @Override
    public void update(World world, float dt) {
        if (pickupCooldown > 0f) {
            pickupCooldown -= dt;
        }
        if (happyTimer > 0f) {
            happyTimer -= dt;
        }
        hunger = Math.min(1f, hunger + Constants.SLIME_HUNGER_RATE * dt);

        if (pendingCrystals > 0) {
            crystalTimer -= dt;
        }

        z = hop.update(z, dt);
        brain.update(this, world, dt);

        if (!velocity.isZero()) {
            float sx = IsoUtils.toScreenX(velocity.x, velocity.y);
            if (Math.abs(sx) > 0.05f) {
                facingLeft = sx < 0f;
            }
            boolean hit = moveWithCollision(world, dt);
            if (hit) {
                velocity.scl(-0.5f);
            }
        }
        onCustomUpdate(world, dt);
    }

    /** Gancho para comportamentos específicos de subclasses (ex.: dano do Slime Pedra). */
    protected void onCustomUpdate(World world, float dt) {
    }

    @Override
    public void render(SpriteBatch batch, Assets assets) {
        String mood;
        if (isAgitated()) {
            mood = "_angry";
        } else if (happyTimer > 0f || hunger < 0.35f) {
            mood = "_happy";
        } else {
            mood = "_neutral";
        }
        TextureRegion reg = assets.get(type.texturePrefix + mood);
        float sx = (facingLeft ? -1f : 1f) * hop.getScaleX();
        float sy = hop.getScaleY();
        drawAnchored(batch, reg, sx, sy, 2f);
    }

    /**
     * Faz o slime comer uma fruta/vegetal, reduzindo a fome e agendando a produção de cristais.
     * Se for a comida favorita, agenda 2 cristais em vez de 1.
     *
     * @param food item de comida
     */
    public void eat(FoodItem food) {
        if (food.isRemoved()) {
            return;
        }
        boolean favorite = food.getType() == type.favoriteFood;
        food.remove();
        hunger = Math.max(0f, hunger - (favorite ? 0.75f : 0.45f));
        happyTimer = 6f;
        state = SlimeState.HAPPY;
        pendingCrystals += favorite ? 2 : 1;
        crystalTimer = Constants.CRYSTAL_DELAY;
        hop.forceHop(3.8f);
    }

    /**
     * Verifica se há um cristal pronto para ser expelido e desconta da fila.
     *
     * @return true se um cristal deve ser criado agora pelo {@code CrystalProductionSystem}
     */
    public boolean pollCrystalReady() {
        if (pendingCrystals > 0 && crystalTimer <= 0f) {
            pendingCrystals--;
            crystalTimer = Constants.CRYSTAL_DELAY;
            hop.forceHop(3.2f);
            return true;
        }
        return false;
    }

    /**
     * Lança o slime no ar (ex.: quando atirado pelo aspirador).
     *
     * @param vx velocidade x
     * @param vy velocidade y
     * @param vz impulso vertical
     */
    public void launch(float vx, float vy, float vz) {
        velocity.set(vx, vy);
        hop.forceHop(vz);
        pickupCooldown = 0.45f;
        launchedByPlayer = true;
    }

    /**
     * Puxa o slime em direção ao bico do aspirador.
     *
     * @param target bico do aspirador
     * @param accel  aceleração
     * @param dt     tempo do quadro
     */
    public void pullTowards(Vector2 target, float accel, float dt) {
        float dx = target.x - pos.x;
        float dy = target.y - pos.y;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0.001f) {
            velocity.x += (dx / len) * accel * dt;
            velocity.y += (dy / len) * accel * dt;
        }
    }

    /** @return true se pode ser puxado/capturado pelo aspirador */
    public boolean canBeVacuumed() {
        return pickupCooldown <= 0f && !isRemoved();
    }

    /** @return true se está com fome suficiente para procurar comida */
    public boolean isHungry() {
        return hunger >= Constants.SLIME_HUNGRY;
    }

    /** @return true se está faminto a ponto de ficar agitado */
    public boolean isAgitated() {
        return hunger >= Constants.SLIME_AGITATED;
    }

    /** @return tipo do slime */
    public SlimeType getType() {
        return type;
    }

    /** @return controlador de pulinhos */
    public HopMovement getHop() {
        return hop;
    }

    /** @return estado atual da IA */
    public SlimeState getState() {
        return state;
    }

    /** @param state novo estado da IA */
    public void setState(SlimeState state) {
        this.state = state;
    }

    /** @return nível de fome (0 = cheio, 1 = faminto) */
    public float getHunger() {
        return hunger;
    }

    /** @return tempo restante de felicidade após comer */
    public float getHappyTimer() {
        return happyTimer;
    }

    /** @return true se este slime pertence ao curral e não pode sair pelo vidro */
    public boolean isCorralled() {
        return corralled;
    }

    /** @param corralled define se este slime está confinado dentro do curral */
    public void setCorralled(boolean corralled) {
        this.corralled = corralled;
    }

    /** @return true se foi recém-atirado pelo aspirador do jogador */
    public boolean isLaunchedByPlayer() {
        return launchedByPlayer;
    }

    /** @param launchedByPlayer define se está em voo lançado pelo jogador */
    public void setLaunchedByPlayer(boolean launchedByPlayer) {
        this.launchedByPlayer = launchedByPlayer;
    }
}

