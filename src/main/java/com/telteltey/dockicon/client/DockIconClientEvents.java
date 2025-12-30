package com.telteltey.dockicon.client;

import com.telteltey.dockicon.DockIconMod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = DockIconMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class DockIconClientEvents {
    private DockIconClientEvents() {
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new DockIconReloadListener());
    }
}
