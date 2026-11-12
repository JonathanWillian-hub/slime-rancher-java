# Slime Farm Iso — Jogo 2D em libGDX (Game Jam)

## 1. Mecânica Central Original (Até 3 linhas)
O jogador pilota um aspirador a vácuo bidirecional em perspectiva isométrica para capturar e arremessar slimes vivos e alimentos coloridos pelo mapa. A graça é explorar biomas perigosos, atrair slimes pelas suas comidas prediletas e dominar a física de sucção para encher o curral de vidro, lucrando cristais antes que a energia do traje se esgote.

---

## 2. Restrição do Tema / Regra de Jogabilidade
**"Barreira Seletiva de Energia e Risco por Agitação":**
- **Barreira de Energia do Curral:** Somente o jogador consegue atravessar livremente o vidro do cercado; slimes capturados ficam presos no interior e slimes selvagens são repelidos pela barreira externa a menos que sejam arremessados intencionalmente pelo aspirador do jogador.
- **Risco por Fome e Dano:** Slimes deixados com fome acumulam agitação; o Slime Pedra faminto entra em rotação agressiva e desfere dano de contato ao jogador, forçando o rancheiro a balancear exploração externa com alimentação periódica da sua fazenda.

---

## 3. Níveis / Estágios de Dificuldade e Biomas
O mundo aberto (80×80 tiles) é dividido em 4 biomas com progressão de desafio e espécies raras:
1. **Nível 1 — Rancho Central (`RANCH`):** Zona segura inicial onde ficam o curral de vidro, o terminal do Mercado de Cristais e a Loja de Upgrades. Slimes dóceis (Slime Rosa - 45%).
2. **Nível 2 — Pradaria Florida (`MEADOW`):** Área aberta com alta densidade de árvores e arbustos frutíferos. Introduz o ágil Slime Gatinho (25%) e o Slime Fósforo (10%).
3. **Nível 3 — Floresta de Musgo (`MOSS_FOREST`):** Mata úmida e densa com caminhos estreitos. Habita o raro Slime de Mel (12%) e slimes com maior apetite.
4. **Nível 4 / Estágio Extremo — Pedreira Cristalina (`CRYSTAL_QUARRY`):** Região mineral hostil com terreno pedregoso. Área de alto perigo com o agressivo Slime Pedra (25%) e a rara aparição do lendário Slime Dourado (3%).

---

## 4. Pontuação e Game Over
- **Pontuação (Moedas $):** O score acumulado é medido pela carteira de moedas (`Wallet`), obtidas vendendo cristais no terminal do Mercado de Cristais, cujos valores flutuam diariamente em até ±30%.
- **Game Over / Desmaio do Traje:** Quando a vida do jogador chega a zero por dano contínuo dos slimes agitados, o personagem desmaia, perde **10% de toda sua pontuação acumulada** e é teleportado de volta ao início do Rancho para reiniciar sua jornada.

---

## 5. Menus e Navegação
- **Menu Inicial:**
  - Botão **"INICIAR JOGO / CONTINUAR"**: inicia uma nova jornada ou retoma o progresso salvo.
  - Botão **"VER INVENTÁRIO (SLIMES PEGOS)"**: abre o catálogo estilo Pokédex/Slimepedia, mostrando os slimes já descobertos e as silhuetas pretas com as chances de aparição e biomas dos ainda não capturados.
- **Menu de Pausa (`Esc`):**
  - Continuar, salvar partida (`F5`) ou retornar ao Menu Principal (`M`).

---

## 6. Organização do Código (Classes Java Separadas)

O projeto é modularizado de forma estrita em Orientação a Objetos (uma classe pública por arquivo):
- **Jogador e Ações:** `Player`, `PlayerController`, `PlayerStats`, `VacuumGun`.
- **Slimes e IA:** `Slime` (abstrata), `PinkSlime`, `CatSlime`, `RockSlime`, `HoneySlime`, `PhosphorSlime`, `GoldSlime`, `SlimeBrain`, `HopMovement`, `WanderBehavior`, `SeekFoodBehavior`.
- **Mundo e Colisão:** `World`, `TileMap`, `Tile`, `TileType`, `BiomeType`, `CollisionMap`, `MapBuilder`.
- **Cenário e Props:** `Prop` (abstrata), `Tree`, `Bush`, `Rock`, `Flower`, `Fence`, `Gate`, `Bridge`, `Corral`, `MarketTerminal`, `ShopTerminal`.
- **Economia e Upgrades:** `Wallet`, `Market`, `PriceTable`, `UpgradeShop`, `UpgradeType`.
- **Progressão e UI:** `Slimepedia`, `SlimepediaWindow`, `Hud`, `Hotbar`, `HotbarSlotView`, `MarketWindow`, `ShopWindow`, `PauseMenu`, `StatBar`, `CoinCounter`.
- **Telas e Sistemas (`screens` / `systems`):** `SlimeFarmGame`, `LoadingScreen`, `MenuScreen`, `GameScreen`, `BuildingSystem`, `CorralSystem`, `SpawnSystem`, `SpawnZone`, `FoodSpawner`, `CrystalProductionSystem`.
- **Renderização e Áudio:** `WorldRenderer`, `DepthSorter`, `ShadowRenderer`, `CorralRenderer`, `ParticleSystem`, `VacuumBeamRenderer`, `AmbientEffects`, `SoundManager`.
- **Persistência (`save`):** `SaveData`, `SaveManager`, `SaveHelper`.

---

## 7. Como Executar

### 🚀 Opção 1: Execução Direta (Sem precisar abrir a IDE)
Ao descompactar o arquivo `.zip`:
1. Dê dois cliques em **`JOGAR.exe`** ou **`JOGAR.bat`** na raiz do projeto.
2. O jogo abrirá imediatamente utilizando o arquivo executável pré-compilado integrado!

> **Alternativa standalone:** Dentro da pasta `executavel/`, você também encontra o `slime-farm-iso.jar` e um atalho `JOGAR.bat` direto.

---

### 💻 Opção 2: Linha de Comando / IDE (Gradle)
Caso queira executar compilando o código-fonte via terminal:
```powershell
# Executar no Desktop (LWJGL3)
.\gradlew.bat lwjgl3:run

# Compilar e executar testes automatizados JUnit 5
.\gradlew.bat build
```
```

### Controles no Jogo
- `W, A, S, D`: movimentação isométrica
- `Shift`: correr (consome energia azul)
- `Botão Esquerdo do Mouse`: aspirar slimes, frutas e cristais
- `Botão Direito do Mouse`: arremessar item ou slime selecionado
- `1` a `8` ou `Scroll`: selecionar slot da Hotbar
- `B` ou selecionar Slot 7: **Modo Construção e Remoção de Cercados (Papel de Construção / Fortnite Style)**
  - *Passo 1 (Pilar 1):* clique no chão para posicionar o Pilar 1
  - *Passo 2 (Pilar 2):* mova o mouse e clique para posicionar o Pilar 2 (largura)
  - *Passo 3 (Pilar 3):* mova o mouse e clique para posicionar o Pilar 3 (profundidade)
  - *Passo 4 (Pilar 4):* clique para posicionar o Pilar 4 e fechar o novo cercado de vidro!
  - *Desfazer:* `Backspace` ou `Botão Direito` desfaz o último pilar posicionado
  - *Tirar / Demolir cercado:* aponte para qualquer cercado existente e aperte `[X]` (ou `Botão Direito`) para desmontá-lo e libertar os slimes!
- `P` ou `Tab` (ou botão superior direito): abrir a Slimepedia
- `E`: interagir com o Mercado de Cristais ou Loja de Upgrades
- `U`: abrir diretamente a Loja de Upgrades
- `F5`: salvar progresso | `Esc`: pausar jogo
- `U`: atalho direto para a Loja de Upgrades
- `F5`: salvar progresso manualmente
- `Esc`: pausar o jogo
