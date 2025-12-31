package com.telteltey.dockicon.client;

import com.telteltey.dockicon.DockIconMod;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = DockIconMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class DockIconClientTicker {
    private static boolean pending = true;
    private static boolean warningPending = true;

    private DockIconClientTicker() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        if (pending) {
            if (minecraft.getWindow() == null || minecraft.getWindow().getWindow() == 0L) {
                return;
            }
            pending = false;
            DockIconManager.requestDockIconUpdate(Util.backgroundExecutor(), minecraft);
        }
        if (warningPending) {
            if (!DockIconWarningState.shouldShowWarning()) {
                warningPending = false;
                return;
            }
            if (!(minecraft.screen instanceof DockIconJvmWarningScreen)) {
                warningPending = false;
                minecraft.setScreen(new DockIconJvmWarningScreen(minecraft.screen));
            }
        }
    }
}
