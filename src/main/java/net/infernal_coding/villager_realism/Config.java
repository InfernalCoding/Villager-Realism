package net.infernal_coding.villager_realism;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue GOLEM_ARROWS = BUILDER
            .define("Toggle whether or not iron golems are impervious to arrows",
                    true);

    public static final ForgeConfigSpec.BooleanValue INCREASE_GOLEM_RANGE = BUILDER
            .define("Toggle whether or not iron golems have increased attack range",
                    true);


    public static final ForgeConfigSpec.BooleanValue SHOW_DIMENSIONS_TO_PLAYER = BUILDER
            .comment("Define whether or not the text the player receives when attempting to spawn the wither will show what dimensions the wither is spawnable in.")
            .define("Show valid dimensions:", true);

    static {
        SPEC = BUILDER.build();
    }
}
