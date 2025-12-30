package com.telteltey.dockicon.client;

import java.util.Locale;

import com.telteltey.dockicon.DockIconMod;
import com.telteltey.dockicon.config.DockIconConfig;

public final class DockIconWarningState {
    private static boolean dismissedThisSession;

    private DockIconWarningState() {
    }

    public static boolean shouldShowWarning() {
        if (dismissedThisSession) {
            return false;
        }
        if (!isMacOs()) {
            return false;
        }
        return !DockIconConfig.JVM_WARNING_ACKNOWLEDGED.get();
    }

    public static void dismissThisSession() {
        dismissedThisSession = true;
    }

    public static void suppressPermanently() {
        dismissedThisSession = true;
        try {
            DockIconConfig.JVM_WARNING_ACKNOWLEDGED.set(true);
            DockIconConfig.SPEC.save();
        } catch (Throwable t) {
            DockIconMod.LOGGER.warn("Failed to persist JVM warning acknowledgement.", t);
        }
    }

    private static boolean isMacOs() {
        String osName = System.getProperty("os.name", "");
        return osName.toLowerCase(Locale.ROOT).contains("mac");
    }
}
