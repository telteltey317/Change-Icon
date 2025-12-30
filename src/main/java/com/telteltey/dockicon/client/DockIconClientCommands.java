package com.telteltey.dockicon.client;

import com.telteltey.dockicon.DockIconMod;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = DockIconMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class DockIconClientCommands {
    private DockIconClientCommands() {
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("dockicon")
                .then(Commands.literal("reload")
                        .executes(context -> {
                            DockIconManager.trySetDockIcon();
                            context.getSource().sendSuccess(
                                    () -> Component.translatable("dockicon.command.reload.success"),
                                    false
                            );
                            return 1;
                        })));
    }
}
