package com.telteltey.dockicon;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import java.util.Locale;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

import com.telteltey.dockicon.config.DockIconConfig;

@Mod(DockIconMod.MODID)
public class DockIconMod {
    public static final String MODID = "dockicon";
    public static final Logger LOGGER = LogUtils.getLogger();

    static {
        String osName = System.getProperty("os.name", "");
        if (osName.toLowerCase(Locale.ROOT).contains("mac")
                && System.getProperty("java.awt.headless") == null) {
            System.setProperty("java.awt.headless", "false");
        }
    }

    public DockIconMod(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, DockIconConfig.SPEC);
    }
}
