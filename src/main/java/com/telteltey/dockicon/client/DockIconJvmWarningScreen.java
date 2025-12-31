package com.telteltey.dockicon.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public final class DockIconJvmWarningScreen extends Screen {
    private static final Component TITLE = Component.translatable("dockicon.warning.title");
    private static final Component CHECKBOX_LABEL = Component.translatable("dockicon.warning.checkbox");
    private static final Component OK_LABEL = Component.translatable("dockicon.warning.ok");
    private static final List<Component> LINES = List.of(
            Component.translatable("dockicon.warning.line1"),
            Component.translatable("dockicon.warning.line2"),
            Component.translatable("dockicon.warning.line3"),
            Component.translatable("dockicon.warning.line4"),
            Component.translatable("dockicon.warning.line5"),
            Component.translatable("dockicon.warning.line6"),
            Component.translatable("dockicon.warning.line7")
    );

    private final Screen previous;
    private Checkbox checkbox;
    private boolean dontShowAgain;
    private boolean dismissed;
    private List<FormattedCharSequence> wrappedLines = List.of();

    public DockIconJvmWarningScreen(Screen previous) {
        super(TITLE);
        this.previous = previous;
    }

    @Override
    protected void init() {
        int buttonWidth = 200;
        int buttonHeight = 20;
        int buttonX = (this.width - buttonWidth) / 2;
        int buttonY = this.height - 40;
        int toggleY = buttonY - 30;
        int checkboxWidth = Checkbox.getBoxSize(this.font) + 4 + this.font.width(CHECKBOX_LABEL);
        int checkboxX = (this.width - checkboxWidth) / 2;
        this.checkbox = Checkbox.builder(CHECKBOX_LABEL, this.font)
                .pos(checkboxX, toggleY)
                .selected(dontShowAgain)
                .onValueChange((checkbox, value) -> dontShowAgain = value)
                .build();
        this.addRenderableWidget(this.checkbox);
        this.addRenderableWidget(Button.builder(OK_LABEL, button -> dismiss())
                .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                .build());
        rebuildWrappedLines();
    }

    @Override
    public void onClose() {
        dismiss();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        int lineHeight = this.font.lineHeight + 2;
        int startY = 50;
        int y = startY;
        for (FormattedCharSequence line : wrappedLines) {
            guiGraphics.drawCenteredString(this.font, line, this.width / 2, y, 0xFFFFFF);
            y += lineHeight;
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft != null && this.minecraft.level == null) {
            this.renderPanorama(guiGraphics, partialTick);
        }
        this.renderMenuBackground(guiGraphics);
    }

    private void dismiss() {
        if (dismissed) {
            return;
        }
        dismissed = true;
        if (dontShowAgain) {
            DockIconWarningState.suppressPermanently();
        } else {
            DockIconWarningState.dismissThisSession();
        }
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.previous);
        }
    }

    private void rebuildWrappedLines() {
        int maxWidth = Math.max(100, this.width - 40);
        List<FormattedCharSequence> lines = new ArrayList<>();
        for (Component line : LINES) {
            lines.addAll(this.font.split(line, maxWidth));
        }
        if (lines.isEmpty()) {
            lines.add(FormattedCharSequence.EMPTY);
        }
        this.wrappedLines = lines;
    }
}
