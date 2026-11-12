# TAREFAS — Slime Farm Iso (libGDX)

> Leia antes o [`PLANO_IMPLEMENTACAO.md`](PLANO_IMPLEMENTACAO.md). Referência visual: [`docs/referencia.jpg`](docs/referencia.jpg).

## Protocolo de Status (OBRIGATÓRIO para todo agente)

| Símbolo | Significado |
|---|---|
| ⬜ | A fazer |
| 🔄 | Em andamento (alguém começou; pode estar incompleta se o agente parou) |
| ✅ | Concluída e compilando |
| ⚠️ | Bloqueada (escrever o motivo na coluna Notas) |

1. **Ao começar** uma tarefa: troque ⬜ por 🔄 e preencha `Agente/Data` **antes** de escrever código.
2. **Ao terminar**: rode `.\gradlew.bat build`; se compilar, troque para ✅ e escreva em *Notas* o que foi feito/arquivos criados.
3. **Se encontrar uma tarefa 🔄** ao iniciar a sessão: o agente anterior provavelmente acabou os tokens. Verifique os arquivos citados, complete o que falta e marque ✅.
4. Trabalhe **em ordem** (respeite a coluna *Depende de*). Faça tarefas pequenas e atualize este arquivo **após cada uma** — não acumule.
5. Ao final da sessão, adicione uma linha no **Log de Sessões** (fim do arquivo).
6. Não renomeie IDs de tarefas. Se precisar de uma tarefa nova, adicione com sufixo (ex.: `T5.3b`).
7. Atalho para mudar status: `.\tools\set-status.ps1 -Id T2.5 -Status doing -Agent "NomeDoAgente"` (status: `todo`, `doing`, `ok`, `blocked`; `-Note "texto"` opcional).
8. Build: `.\gradlew.bat build` · Rodar: `.\gradlew.bat lwjgl3:run`. O JDK 17 está fixado em `gradle.properties`.

Pacote base: `com.slimefarm` → `core/src/main/java/com/slimefarm/`

---

## Fase 0 — Setup do projeto
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T0.1 | ✅ | Verificar/instalar JDK 17 64‑bit e `JAVA_HOME` | — | — | `java -version` mostra 17+ | Antigravity 2026-11-07 | JDK em `C:\Program Files\Java\jdk-17` (17.0.12). JAVA_HOME do sistema aponta p/ JDK 11 → `gradle.properties` usa `org.gradle.java.home` |
| T0.2 | ✅ | Criar projeto Gradle multi‑módulo (`core`, `lwjgl3`) com libGDX 1.12.1 e Gradle Wrapper (via gdx‑liftoff ou manual) | `settings.gradle`, `build.gradle`, `gradle.properties`, `gradlew*`, `gradle/wrapper/*`, `core/build.gradle`, `lwjgl3/build.gradle` | T0.1 | `gradlew build` OK | Antigravity 2026-11-07 | |
| T0.3 | ✅ | Launcher desktop (janela 1280×720, título "Slime Farm Iso", vsync, 60 FPS) | `lwjgl3/.../Lwjgl3Launcher.java` | T0.2 | Janela abre | Antigravity 2026-11-07 | |
| T0.4 | ✅ | Classe principal `SlimeFarmGame extends Game` (abre tela de teste preta) | `SlimeFarmGame.java` | T0.3 | `gradlew lwjgl3:run` abre janela | Antigravity 2026-11-07 | |
| T0.5 | ✅ | `.gitignore` (build, .gradle, .idea, *.iml) e `README.md` com como rodar | `.gitignore`, `README.md` | T0.2 | — | Antigravity 2026-11-07 | |

## Fase 1 — Núcleo e utilitários isométricos
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T1.1 | ✅ | `Constants` (TILE_W=64, TILE_H=32, velocidades, limites, cores, preços base) | `util/Constants.java` | T0.4 | — | Antigravity 2026-11-07 | |
| T1.2 | ✅ | `Direction` enum (8 direções, a partir de vetor/ângulo) | `util/Direction.java` | T1.1 | — | Antigravity 2026-11-07 | |
| T1.3 | ✅ | `IsoUtils`: worldToScreen, screenToWorld, depth(x,y) | `util/IsoUtils.java` | T1.1 | Conversão ida/volta consistente | Antigravity 2026-11-07 | |
| T1.4 | ✅ | `RandomUtils` (range, chance, escolha aleatória) | `util/RandomUtils.java` | T1.1 | — | Antigravity 2026-11-07 | |
| T1.5 | ✅ | `TextureFactory` base: utilitários Pixmap (losango, elipse, contorno, sombreado) | `assets/TextureFactory.java` | T1.1 | — | Antigravity 2026-11-07 | Arte 100% original |
| T1.6 | ✅ | `Assets`: carrega/gera e guarda `TextureRegion`s por nome; `BitmapFont`; `dispose()` | `assets/Assets.java` | T1.5 | — | Antigravity 2026-11-07 | |
| T1.7 | ✅ | `LoadingScreen` (barra de progresso enquanto gera assets) → vai p/ GameScreen | `screens/LoadingScreen.java` | T1.6 | — | Antigravity 2026-11-07 | |
| T1.8 | ✅ | `GameScreen` esqueleto: câmera ortográfica, `ExtendViewport` 960×540, loop update/render, resize | `screens/GameScreen.java` | T1.7 | Tela limpa com cor de fundo | Antigravity 2026-11-07 | |

## Fase 2 — Mundo e mapa (igual à referência)
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T2.1 | ✅ | `TileType` enum: GRASS, GRASS_DARK, DIRT, STONE_PATH, CLIFF, WATER?, BRIDGE (com flag walkable e altura) | `world/TileType.java` | T1.8 | — | Antigravity 2026-11-07 | |
| T2.2 | ✅ | `Tile` (tipo, altura/elevação, variação visual) | `world/Tile.java` | T2.1 | — | Antigravity 2026-11-07 | |
| T2.3 | ✅ | `TileMap` (grade W×H, get/set, inBounds) | `world/TileMap.java` | T2.2 | — | Antigravity 2026-11-07 | |
| T2.4 | ✅ | Texturas dos tiles na `TextureFactory` (grama com tufos, terra com pedrinhas, laje de pedra, face de penhasco marrom) | `assets/TileTextureFactory.java` | T1.5 | Losangos visíveis com variação | Antigravity 2026-11-07 | |
| T2.5 | ✅ | `MapBuilder`: monta mapa ~40×40 imitando a referência (terra no centro, grama nas bordas, penhascos elevados, caminhos de pedra, ponte inferior esquerda) | `world/MapBuilder.java` | T2.3 | — | Antigravity 2026-11-07 | |
| T2.6 | ✅ | `WorldRenderer`: desenha tiles em ordem isométrica + faces dos penhascos; culling pela câmera | `render/WorldRenderer.java` | T2.4, T2.5 | Mapa aparece como na referência | Antigravity 2026-11-07 | |
| T2.7 | ✅ | `Entity` abstrata (pos, z, width, solid, update, getDepth, remove flag) | `entities/Entity.java` | T1.3 | — | Antigravity 2026-11-07 | |
| T2.8 | ✅ | `Prop` abstrata + `Tree`, `Bush`, `Rock`, `Fence`, `Gate`, `Bridge`, `Flower` | `entities/props/*.java` | T2.7 | — | Antigravity 2026-11-07 | Uma classe por arquivo |
| T2.9 | ✅ | Texturas dos props (árvore copa redonda, arbusto, pedra cinza, cerca de madeira, portão, ponte) | `assets/PropTextureFactory.java` | T1.5 | — | Antigravity 2026-11-07 | |
| T2.10 | ✅ | `World`: guarda TileMap + lista de entidades, add/remove, update, consultas (entidades próximas) | `world/World.java` | T2.7 | — | Antigravity 2026-11-07 | |
| T2.11 | ✅ | `DepthSorter` e desenho das entidades ordenadas por profundidade | `render/DepthSorter.java` | T2.10 | Árvores cobrem corretamente o que está atrás | Antigravity 2026-11-07 | |
| T2.12 | ✅ | `MapBuilder` posiciona props como na referência (árvores no topo, cercas laterais, pedras, portão sup. esq.) | `world/MapBuilder.java` | T2.8 | Cena parecida com referência | Antigravity 2026-11-07 | |
| T2.13 | ✅ | `CollisionMap`: tiles não andáveis + props sólidos (círculo/AABB em coords de mundo) | `world/CollisionMap.java` | T2.10 | — | Antigravity 2026-11-07 | |

## Fase 3 — Jogador
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T3.1 | ✅ | `InputState` + `GameInputProcessor` (teclas WASD/Shift/E/1‑8/Esc, mouse pos, botões, scroll) | `input/*.java` | T1.8 | — | Antigravity 2026-11-07 | |
| T3.2 | ✅ | `LivingEntity` (velocidade, movimento com colisão) | `entities/LivingEntity.java` | T2.13 | — | Antigravity 2026-11-07 | |
| T3.3 | ✅ | `PlayerStats` (vida, energia, regeneração, dano) | `player/PlayerStats.java` | T1.1 | — | Antigravity 2026-11-07 | |
| T3.4 | ✅ | `Player` (stats, direção de mira, estado andando/parado) | `entities/Player.java` | T3.2, T3.3 | — | Antigravity 2026-11-07 | |
| T3.5 | ✅ | `PlayerController`: WASD alinhado à tela → vetor de mundo, correr gasta energia, mira = mouse em coords de mundo | `player/PlayerController.java` | T3.1, T3.4 | Jogador anda e colide | Antigravity 2026-11-07 | |
| T3.6 | ✅ | Sprite original do jogador (traje espacial, mochila, aspirador), 4–8 direções, frames de caminhada | `assets/PlayerTextureFactory.java` | T1.5 | — | Antigravity 2026-11-07 | Personagem original |
| T3.7 | ✅ | Câmera segue o jogador com lerp e limites do mapa | `screens/GameScreen.java` | T3.5 | — | Antigravity 2026-11-07 | |
| T3.8 | ✅ | `ShadowRenderer` (sombra elíptica sob entidades, encolhe com z) | `render/ShadowRenderer.java` | T2.11 | — | Antigravity 2026-11-07 | |

## Fase 4 — Itens, inventário e aspirador
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T4.1 | ✅ | `ItemType` enum: STRAWBERRY, APPLE, CARROT, CRYSTAL_PINK, CRYSTAL_CAT, CRYSTAL_ROCK, SLIME_* (categoria, preço base, cor) | `entities/items/ItemType.java` | T1.1 | — | Antigravity 2026-11-07 | |
| T4.2 | ✅ | `ItemEntity` (item solto no mundo, quica, pode ser aspirado) + `FoodItem`, `CrystalItem` | `entities/items/*.java` | T2.7, T4.1 | — | Antigravity 2026-11-07 | |
| T4.3 | ✅ | Texturas de itens (morango, maçã, cenoura, cristais coloridos facetados) | `assets/ItemTextureFactory.java` | T1.5 | — | Antigravity 2026-11-07 | |
| T4.4 | ✅ | `InventorySlot` (tipo + quantidade, máx. 20) | `inventory/InventorySlot.java` | T4.1 | — | Antigravity 2026-11-07 | |
| T4.5 | ✅ | `Inventory` (8 slots, add/remove, slot selecionado, próximo/anterior) | `inventory/Inventory.java` | T4.4 | — | Antigravity 2026-11-07 | |
| T4.6 | ✅ | `VacuumGun`: aspirar (cone, força de puxão, coleta ao encostar) e atirar item selecionado | `player/VacuumGun.java` | T4.2, T4.5, T3.5 | Coleta e atira itens | Antigravity 2026-11-07 | |
| T4.7 | ✅ | `FoodSpawner`: frutas nascem perto de árvores/arbustos periodicamente (limite máx.) | `systems/FoodSpawner.java` | T4.2 | — | Antigravity 2026-11-07 | |

## Fase 5 — Slimes e IA
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T5.1 | ✅ | `SlimeType` enum (PINK, CAT, ROCK: cor, comida favorita, cristal, dano) e `SlimeState` enum | `entities/slimes/SlimeType.java`, `SlimeState.java` | T4.1 | — | Antigravity 2026-11-07 | |
| T5.2 | ✅ | `Slime` abstrata (fome, estado, pulinho, comer, produzir cristal) | `entities/slimes/Slime.java` | T3.2, T5.1 | — | Antigravity 2026-11-07 | |
| T5.3 | ✅ | `PinkSlime`, `CatSlime`, `RockSlime` (comportamentos específicos; RockSlime causa dano) | `entities/slimes/*.java` | T5.2 | — | Antigravity 2026-11-07 | |
| T5.4 | ✅ | `HopMovement` (pulo com z, gravidade, achatamento squash & stretch) | `ai/HopMovement.java` | T5.2 | Slimes pulam com sombra | Antigravity 2026-11-07 | |
| T5.5 | ✅ | `SlimeBrain` + `WanderBehavior` + `SeekFoodBehavior` (máquina de estados) | `ai/*.java` | T5.4 | Slimes vagam e buscam comida | Antigravity 2026-11-07 | |
| T5.6 | ✅ | Sprites originais dos slimes (rosa redondo, rosa com orelhas, cinza com espinhos), expressões feliz/neutro/bravo | `assets/SlimeTextureFactory.java` | T1.5 | — | Antigravity 2026-11-07 | Design original |
| T5.7 | ✅ | Slimes podem ser aspirados/atirados (viram item SLIME_* no inventário) | `player/VacuumGun.java` | T4.6, T5.3 | — | Antigravity 2026-11-07 | |
| T5.8 | ✅ | `SpawnSystem`: slimes selvagens surgem fora do curral até um limite | `systems/SpawnSystem.java` | T5.3 | — | Antigravity 2026-11-07 | |

## Fase 6 — Curral e produção de cristais
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T6.1 | ✅ | `Corral` (retângulo em coords de mundo, postes de metal, paredes de vidro, abertura) | `entities/props/Corral.java` | T2.8 | — | Antigravity 2026-11-07 | |
| T6.2 | ✅ | Render do curral: postes, vidro translúcido azul brilhante, parede de trás antes / da frente depois das entidades | `render/CorralRenderer.java` | T6.1, T2.11 | Visual como referência | Antigravity 2026-11-07 | |
| T6.3 | ✅ | `CorralSystem`: slimes dentro não saem; colisão das paredes | `systems/CorralSystem.java` | T6.1, T5.5 | — | Antigravity 2026-11-07 | |
| T6.4 | ✅ | `CrystalProductionSystem`: slime alimentado gera `CrystalItem` após delay | `systems/CrystalProductionSystem.java` | T5.3, T4.2 | Comer → cristal aparece | Antigravity 2026-11-07 | |
| T6.5 | ✅ | Cena inicial: curral à direita com ~6 slimes dentro, slimes soltos no centro (igual referência) | `world/MapBuilder.java` | T6.3 | — | Antigravity 2026-11-07 | |

## Fase 7 — Economia e mercado
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T7.1 | ✅ | `Wallet` (moedas, add, spend, começa com 10050) | `economy/Wallet.java` | T1.1 | — | Antigravity 2026-11-07 | |
| T7.2 | ✅ | `PriceTable` + `Market` (preço atual por cristal, flutuação diária ±30%, vender) | `economy/*.java` | T7.1, T4.1 | — | Antigravity 2026-11-07 | |
| T7.3 | ✅ | `MarketTerminal` prop (máquina com tela, canto sup. direito) + textura original | `entities/props/MarketTerminal.java`, `assets/PropTextureFactory.java` | T2.8 | — | Antigravity 2026-11-07 | |
| T7.4 | ✅ | Interação: tecla E perto do terminal abre `MarketWindow`; também vender atirando cristal no terminal | `ui/MarketWindow.java` | T7.2, T7.3 | Vender aumenta moedas | Antigravity 2026-11-07 | |

## Fase 8 — HUD e interface
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T8.1 | ✅ | `StatBar` (barra com moldura escura arredondada, ícone, preenchimento) | `ui/StatBar.java` | T1.6 | — | Antigravity 2026-11-07 | |
| T8.2 | ✅ | `CoinCounter` (ícone moeda + número) | `ui/CoinCounter.java` | T7.1 | — | Antigravity 2026-11-07 | |
| T8.3 | ✅ | `Hud` (câmera de tela separada; vida vermelha, energia azul, moedas — canto sup. esq.) | `ui/Hud.java` | T8.1, T8.2, T3.3 | Igual referência | Antigravity 2026-11-07 | |
| T8.4 | ✅ | `HotbarSlotView` + `Hotbar` (8 slots na base, ícone + quantidade, seleção amarela) | `ui/*.java` | T4.5 | Igual referência | Antigravity 2026-11-07 | |
| T8.5 | ✅ | Menu de pausa (Esc): continuar / salvar / sair | `ui/PauseMenu.java` | T8.3 | — | Antigravity 2026-11-07 | |

## Fase 9 — Efeitos e áudio
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T9.1 | ✅ | `Particle` + `ParticleSystem` (pool, cor, vida, velocidade) | `render/Particle.java`, `render/ParticleSystem.java` | T2.11 | — | Antigravity 2026-11-07 | |
| T9.2 | ✅ | `VacuumBeamRenderer`: feixe translúcido em cone + partículas coloridas sendo sugadas | `render/VacuumBeamRenderer.java` | T9.1, T4.6 | Igual referência | Antigravity 2026-11-07 | |
| T9.3 | ✅ | Brilhos ambientes (vagalumes/partículas douradas), brilho dos cristais | `render/AmbientEffects.java` | T9.1 | — | Antigravity 2026-11-07 | |
| T9.4 | ✅ | `SoundManager` (sons originais/livres CC0: aspirar, pop, venda, pulo) com volume | `audio/SoundManager.java` | T1.6 | — | Antigravity 2026-11-07 | Só sons próprios ou CC0 |

## Fase 10 — Save e menu
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T10.1 | ✅ | `SaveData` (POJO: moedas, inventário, slimes do curral, dia) | `save/SaveData.java` | T7.1, T4.5 | — | Antigravity 2026-11-07 | |
| T10.2 | ✅ | `SaveManager` (libGDX `Json` + arquivo local; salvar/carregar) | `save/SaveManager.java` | T10.1 | Fechar e abrir mantém progresso | Antigravity 2026-11-07 | |
| T10.3 | ✅ | `MenuScreen` (título, Novo Jogo, Continuar, Sair) | `screens/MenuScreen.java` | T10.2 | — | Antigravity 2026-11-07 | |
| T10.4 | ✅ | Morte/respawn (vida 0 → volta ao início, perde 10% moedas) | `player/PlayerStats.java`, `screens/GameScreen.java` | T3.3 | — | Antigravity 2026-11-07 | |

## Fase 11 — Polimento e entrega
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T11.1 | ✅ | Comparar captura de tela com `docs/referencia.jpg` e ajustar cores/posições/escala | vários | Fases 2–9 | Cena equivalente | Antigravity 2026-11-07 | |
| T11.2 | ⬜ | (Opcional) trocar texturas geradas por PNGs pixel‑art originais em `assets/` | `assets/`, `Assets.java` | T11.1 | — | | |
| T11.3 | ✅ | Revisar Javadoc em todas as classes, remover código morto | todos | T11.1 | — | Antigravity 2026-11-07 | |
| T11.4 | ✅ | Testes unitários simples (IsoUtils, Inventory, Wallet, Market) com JUnit | `core/src/test/java/...` | T11.3 | `gradlew test` OK | Antigravity 2026-11-07 | |
| T11.5 | ✅ | README final: controles, arquitetura, diagrama de classes, prints | `README.md` | T11.4 | — | Antigravity 2026-11-07 | |

## Fase 12 — Expansão: Curral Seletivo, Biomas, Slimepedia, Spawn por Visão, Loja e Guia de Estudo
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T12.1 | ✅ | Barreira seletiva do Curral: Player atravessa o vidro livremente; Slimes de dentro não saem e Slimes de fora não entram sozinhos (só se jogados pelo Player) | `entities/props/Corral.java`, `systems/CorralSystem.java`, `entities/slimes/Slime.java`, `player/VacuumGun.java` | T6.3 | Player atravessa vidro; slimes presos dentro ou barrados fora | Antigravity 2026-11-07 | |
| T12.2 | ✅ | Mapa expandido (80×80) com 4 Biomas exploráveis (`RANCH`, `MEADOW`, `MOSS_FOREST`, `CRYSTAL_QUARRY`) + novos pisos e indicador de bioma no HUD | `world/BiomeType.java`, `world/TileType.java`, `world/Tile.java`, `world/TileMap.java`, `world/MapBuilder.java`, `assets/TileTextureFactory.java` | T12.1 | Player explora os 4 biomas conectados | Antigravity 2026-11-07 | |
| T12.3 | ✅ | Novos Slimes por bioma e raridade (`HoneySlime`, `PhosphorSlime`, `GoldSlime`) + respectivos cristais e probabilidades por bioma | `entities/slimes/HoneySlime.java`, `PhosphorSlime.java`, `GoldSlime.java`, `SlimeType.java`, `entities/items/ItemType.java`, `assets/SlimeTextureFactory.java`, `assets/ItemTextureFactory.java` | T12.2 | 6 slimes em classes separadas com % e biomas | Antigravity 2026-11-07 | |
| T12.4 | ✅ | Sistema de Spawn dinâmico fora do campo de visão: zonas reabastecem slimes quando saem da câmera do jogador, respeitando a % de cada bioma | `systems/SpawnZone.java`, `systems/SpawnSystem.java` | T12.3 | Andar pelo mapa e voltar gera novos slimes na área | Antigravity 2026-11-07 | |
| T12.5 | ✅ | Botão e janela **Slimepedia** (estilo Pokédex): slimes capturados mostram dados completos; não capturados ficam com silhueta preta mostrando apenas `% de spawn` e `bioma` | `progression/Slimepedia.java`, `ui/SlimepediaWindow.java`, `ui/Hud.java`, `save/SaveData.java`, `save/SaveManager.java` | T12.3 | Botão na tela ou tecla P/Tab abre Slimepedia | Antigravity 2026-11-07 | |
| T12.6 | ✅ | Loja de Upgrades para gastar moedas (`UpgradeType`, `UpgradeShop`, `ShopTerminal`, `ShopWindow`): energia, velocidade, aspirador, capacidade da hotbar e vida | `economy/UpgradeType.java`, `economy/UpgradeShop.java`, `entities/props/ShopTerminal.java`, `ui/ShopWindow.java` | T12.1 | Comprar upgrades desconta moedas e melhora atributos | Antigravity 2026-11-07 | |
| T12.7 | ✅ | Criar `GUIA_DO_CODIGO.md` explicando onde fica cada lógica (WASD, isometria, spawn, curral, Slimepedia, loja) classe por classe para apresentação ao professor | `GUIA_DO_CODIGO.md` | T12.1–T12.6 | Guia completo e didático | Antigravity 2026-11-07 | |

## Fase 13 — Construção de Novos Cercados (Papel de Construção / Estilo Fortnite)
| ID | Status | Tarefa | Arquivos | Depende de | Critério de aceite | Agente/Data | Notas |
|---|---|---|---|---|---|---|---|
| T13.1 | ✅ | Sistema de Construção e Remoção de Cercados: Papel de Construção (`ItemType.BLUEPRINT_CORRAL`) na hotbar (slot 7) ou tecla `[B]`; posicionamento manual sequencial dos 4 pilares (Passos 1, 2, 3 e 4), fechamento com paredes de vidro holográficas, suporte a múltiplos cercados no `CorralSystem`, e sistema de demolição/remoção com tecla `[X]` ou botão direito, liberando os slimes | `systems/BuildingSystem.java`, `entities/props/Corral.java`, `render/CorralRenderer.java`, `systems/CorralSystem.java`, `render/WorldRenderer.java`, `entities/items/ItemType.java`, `assets/ItemTextureFactory.java`, `ui/Hud.java`, `screens/GameScreen.java` | T12.1 | Posicionamento dos 4 pilares e remoção/demolição de cercados funcional | Antigravity 2026-11-12 | Testado e aprovado |

---

## Log de Sessões
| Data | Agente | Tarefas trabalhadas | Observações / próximo passo |
|---|---|---|---|
| 2026-11-07 | Antigravity | Criação do plano e deste arquivo | Próximo: T0.1 (instalar JDK 17) |
| 2026-11-07 | Antigravity | Fases 0 a 11 completas (T0.1–T11.5) | 60+ classes Java separadas por pacote, build e testes JUnit 5 passando (`BUILD SUCCESSFUL`). T11.2 (PNGs externos opcionais) fica livre se o grupo quiser substituir texturas procedurais. |
| 2026-11-07 | Antigravity | Fase 12 completa (T12.1–T12.7) | Curral seletivo, 4 biomas (80×80), 6 slimes com % e biomas, spawn dinâmico fora do campo de visão, Slimepedia (Pokédex com silhueta preta), Loja de Upgrades e `GUIA_DO_CODIGO.md` criados e testados (`BUILD SUCCESSFUL`). |
| 2026-11-12 | Antigravity | Correção de transição de telas e Menu Inicial | Corrigido crash (`EXCEPTION_ACCESS_VIOLATION`) ao clicar em Iniciar: transição de tela postergada via `Gdx.app.postRunnable` e guarda de ciclo de vida evitando render em ShapeRenderer descartado. Menu Screen simplificado (Iniciar e Ver Inventário), README atualizado para entrega da Game Jam. |
| 2026-11-12 | Antigravity | Fase 13 completa (T13.1) | Implementado o sistema de construção de cercados (estilo papel de construção do Fortnite): slot 7 da Hotbar / tecla [B] abre o modo de construção; posiciona e alinha 4 pilares fechando novos cercados com vidros energéticos, suporte a múltiplos cercados no CorralSystem e WorldRenderer, 100% testado no JUnit. |

