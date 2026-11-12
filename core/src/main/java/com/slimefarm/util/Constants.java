package com.slimefarm.util;

import com.badlogic.gdx.graphics.Color;

/**
 * Constantes globais do jogo (tamanhos, velocidades, limites, cores e preços).
 * Centralizar aqui evita "números mágicos" espalhados pelo código.
 */
public final class Constants {

    private Constants() {
    }

    // ---------------- Tela / câmera ----------------
    /** Largura virtual do mundo visível (viewport). */
    public static final float VIEW_WIDTH = 960f;
    /** Altura virtual do mundo visível (viewport). */
    public static final float VIEW_HEIGHT = 540f;
    /** Suavização da câmera ao seguir o jogador (0..1 por frame a 60fps). */
    public static final float CAMERA_LERP = 6f;

    // ---------------- Isométrico ----------------
    /** Largura do losango de um tile em pixels. */
    public static final int TILE_W = 64;
    /** Altura do losango de um tile em pixels. */
    public static final int TILE_H = 32;
    /** Metade da largura do tile. */
    public static final float HALF_W = TILE_W / 2f;
    /** Metade da altura do tile. */
    public static final float HALF_H = TILE_H / 2f;
    /** Pixels de altura na tela para cada 1 unidade de elevação (z). */
    public static final float Z_PIXELS = 24f;

    // ---------------- Mapa ----------------
    /** Largura do mapa em tiles (expandido para 4 biomas). */
    public static final int MAP_WIDTH = 80;
    /** Altura do mapa em tiles (expandido para 4 biomas). */
    public static final int MAP_HEIGHT = 80;

    // ---------------- Jogador ----------------
    /** Velocidade de caminhada (tiles por segundo). */
    public static final float PLAYER_SPEED = 3.2f;
    /** Multiplicador ao correr. */
    public static final float PLAYER_RUN_MULT = 1.7f;
    /** Raio de colisão do jogador (tiles). */
    public static final float PLAYER_RADIUS = 0.3f;
    /** Vida máxima. */
    public static final float MAX_HEALTH = 100f;
    /** Energia máxima. */
    public static final float MAX_ENERGY = 100f;
    /** Vida regenerada por segundo. */
    public static final float HEALTH_REGEN = 1.5f;
    /** Energia regenerada por segundo. */
    public static final float ENERGY_REGEN = 12f;
    /** Energia gasta por segundo ao correr. */
    public static final float RUN_ENERGY_COST = 18f;
    /** Energia gasta por segundo ao aspirar. */
    public static final float VACUUM_ENERGY_COST = 4f;

    // ---------------- Aspirador ----------------
    /** Alcance do aspirador (tiles). */
    public static final float VACUUM_RANGE = 4f;
    /** Meio-ângulo do cone do aspirador (graus). */
    public static final float VACUUM_HALF_ANGLE = 35f;
    /** Força de puxão (tiles/s²). */
    public static final float VACUUM_PULL = 18f;
    /** Distância em que o item é coletado (tiles). */
    public static final float VACUUM_COLLECT_DIST = 0.6f;
    /** Velocidade do tiro (tiles por segundo). */
    public static final float SHOOT_SPEED = 9f;
    /** Intervalo mínimo entre tiros (s). */
    public static final float SHOOT_COOLDOWN = 0.25f;

    // ---------------- Inventário ----------------
    /** Quantidade de slots na hotbar. */
    public static final int INVENTORY_SLOTS = 8;
    /** Máximo de itens por slot. */
    public static final int SLOT_MAX = 20;

    // ---------------- Slimes ----------------
    /** Fome ganha por segundo (0..1). */
    public static final float SLIME_HUNGER_RATE = 0.02f;
    /** A partir dessa fome o slime procura comida. */
    public static final float SLIME_HUNGRY = 0.5f;
    /** Fome que deixa o slime agitado. */
    public static final float SLIME_AGITATED = 0.9f;
    /** Raio de visão para comida (tiles). */
    public static final float SLIME_SIGHT = 6f;
    /** Tempo até o cristal sair depois de comer (s). */
    public static final float CRYSTAL_DELAY = 2f;
    /** Máximo de slimes selvagens fora do curral. */
    public static final int MAX_WILD_SLIMES = 10;
    /** Máximo de comidas soltas no mapa. */
    public static final int MAX_FOOD = 18;
    /** Gravidade usada nos pulos (z/s²). */
    public static final float GRAVITY = 14f;

    // ---------------- Economia ----------------
    /** Moedas iniciais. */
    public static final int START_COINS = 10050;
    /** Variação máxima de preço por dia (0.3 = ±30%). */
    public static final float PRICE_VARIATION = 0.3f;
    /** Duração de um "dia" do mercado (s). */
    public static final float DAY_LENGTH = 180f;

    // ---------------- Cores (paleta da referência) ----------------
    public static final Color GRASS = Color.valueOf("5DAA3C");
    public static final Color GRASS_DARK = Color.valueOf("3F8A2E");
    public static final Color DIRT = Color.valueOf("B9763F");
    public static final Color DIRT_DARK = Color.valueOf("9A5E30");
    public static final Color CLIFF = Color.valueOf("7A4A2A");
    public static final Color STONE = Color.valueOf("9C9C9C");
    public static final Color SLIME_PINK = Color.valueOf("F0507A");
    public static final Color GLASS = new Color(0.66f, 0.94f, 1f, 0.35f);
    public static final Color HEALTH = Color.valueOf("E43B44");
    public static final Color ENERGY = Color.valueOf("3FA9F5");
    public static final Color BACKGROUND = Color.valueOf("2E5A26");
}

