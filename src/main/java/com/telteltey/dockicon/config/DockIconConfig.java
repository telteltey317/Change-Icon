package com.telteltey.dockicon.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class DockIconConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> ICON_PATH = BUILDER
            .comment("Path to the Dock icon image. Relative paths are resolved from the config directory, and '~' is expanded.")
            .define("iconPath", "dock_icon.png");

    public static final ModConfigSpec.BooleanValue JVM_WARNING_ACKNOWLEDGED = BUILDER
            .comment("Whether to hide the JVM argument warning screen (\"don't show again\").")
            .define("jvmWarningAcknowledged", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private DockIconConfig() {
    }
}
