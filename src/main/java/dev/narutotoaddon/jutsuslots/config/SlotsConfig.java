package dev.narutotoaddon.jutsuslots.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Configuracoes do addon.
 *
 * Arquivos gerados em {@code config/}:
 *  - narutoto_jutsu_slots-common.toml  (servidor/mundo: quantidade de slots)
 *  - narutoto_jutsu_slots-client.toml  (apenas visual: linhas por coluna no HUD)
 */
public final class SlotsConfig {
    public static final int DEFAULT_MAX_SLOTS = 40;
    public static final int MIN_SLOTS = 15;
    public static final int HARD_MAX_SLOTS = 100;

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec.IntValue MAX_SLOTS;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.IntValue HUD_ROWS_PER_COLUMN;

    static {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
        common.push("jutsu_bar");
        MAX_SLOTS = common
                .comment("Quantidade total de slots da barra de jutsus.",
                        "O Narutoto original tem 15. Valor padrao deste addon: 40.",
                        "Os slots 16 em diante abrem junto com o slot 15 (inteligencia 50).",
                        "Em servidor, o valor valido e o do servidor.")
                .defineInRange("maxJutsuSlots", DEFAULT_MAX_SLOTS, MIN_SLOTS, HARD_MAX_SLOTS);
        common.pop();
        COMMON_SPEC = common.build();

        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        client.push("hud");
        HUD_ROWS_PER_COLUMN = client
                .comment("Quantos jutsus aparecem por coluna na lista expandida (tecla '.') para os slots extras.",
                        "Diminua se a lista ficar cortada em telas pequenas / GUI scale alto.")
                .defineInRange("rowsPerColumn", 15, 5, 40);
        client.pop();
        CLIENT_SPEC = client.build();
    }

    private SlotsConfig() {
    }

    /** Quantidade de slots configurada no lado do servidor (nunca lanca excecao). */
    public static int maxSlots() {
        try {
            return clamp(MAX_SLOTS.get());
        } catch (IllegalStateException notLoadedYet) {
            return DEFAULT_MAX_SLOTS;
        }
    }

    public static int hudRowsPerColumn() {
        try {
            return HUD_ROWS_PER_COLUMN.get();
        } catch (IllegalStateException notLoadedYet) {
            return 15;
        }
    }

    public static int clamp(int value) {
        return Math.max(MIN_SLOTS, Math.min(HARD_MAX_SLOTS, value));
    }
}
