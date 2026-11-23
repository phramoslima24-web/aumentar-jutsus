# Narutoto – Jutsu Slots Addon

Addon para o **Narutoto: Ninja World 0.4.1** (Forge 1.20.1) que aumenta a barra de jutsus de **15 para 40 slots** (configurável de 15 a 100).

## Como compilar pelo GitHub
1. Crie um repositório no GitHub e envie **todo o conteúdo desta pasta** (inclusive `.github/` e `libs/`).
2. A aba **Actions** roda sozinha a cada push (ou clique em *Run workflow*).
3. Ao terminar, baixe o `.jar` em **Artifacts → narutoto-jutsu-slots**.
4. Para gerar uma *Release* com o jar: crie uma tag, ex. `git tag v1.0.0 && git push --tags`.

## Instalação
Coloque o jar do addon na pasta `mods/` **junto com o Narutoto 0.4.1**, no cliente **e** no servidor (o addon tem pacote de rede próprio).

## Como funciona
- Slots **1–10** livres; **11–15** exigem inteligência 10/20/30/40/50 (igual ao original); **16–40** abrem junto com o slot 15 (inteligência 50).
- As mesmas teclas de trocar jutsu (padrão `C` / `X`) percorrem os novos slots; ao passar do último, volta ao 1 (e `X` no slot 1 vai para o último).
- Para colocar um jutsu num slot extra, selecione o slot e aprenda/equipe o jutsu normalmente.
- A lista expandida (tecla `.`) mostra os slots extras em colunas à direita da lista original.
- Os slots extras sobrevivem à morte e são zerados pelo "resetar tudo" do mod.

## Configuração
- `config/narutoto_jutsu_slots-common.toml` → `maxJutsuSlots` (padrão **40**, mín. 15, máx. 100).
- `config/narutoto_jutsu_slots-client.toml` → `rowsPerColumn` (jutsus por coluna no HUD, padrão 15).

## Detalhes técnicos
Usa **Mixin** em 5 procedures do Narutoto (`SelectJutsuUP/Down`, `JutsuSwichNameDisplay`, `Bind2`, `ResetAll`).
O Narutoto não é redistribuído: `libs/narutoto-0.4.1-classes.jar` contém só as `.class` e é usado apenas para compilar.
Se o Narutoto for atualizado e esses procedures mudarem, o jogo avisa na inicialização (Mixin `defaultRequire`).

Build local (opcional): instale Gradle 8.8 + JDK 17 e rode `gradle build`; o jar sai em `build/libs/`.
