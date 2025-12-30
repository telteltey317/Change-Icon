package com.telteltey.dockicon.client;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
    private Button checkboxButton;
    private int checkboxLabelX;
    private int checkboxLabelY;
    private int checkboxLabelWidth;
    private int checkboxLabelHeight;
    private boolean dontShowAgain;
    private boolean dismissed;

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
        int checkboxSize = 20;
        int toggleY = buttonY - 30;
        int labelWidth = this.font.width(CHECKBOX_LABEL);
        int totalWidth = checkboxSize + 6 + labelWidth;
        int checkboxX = (this.width - totalWidth) / 2;
        this.checkboxLabelX = checkboxX + checkboxSize + 6;
        this.checkboxLabelY = toggleY + (checkboxSize - this.font.lineHeight) / 2;
        this.checkboxLabelWidth = labelWidth;
        this.checkboxLabelHeight = this.font.lineHeight;
        this.checkboxButton = Button.builder(Component.literal("☐"), button -> toggleDontShowAgain())
                .bounds(checkboxX, toggleY, checkboxSize, checkboxSize)
                .build();
        this.addRenderableWidget(this.checkboxButton);
        this.addRenderableWidget(Button.builder(OK_LABEL, button -> dismiss())
                .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                .build());
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
        for (Component line : LINES) {
            guiGraphics.drawCenteredString(this.font, line, this.width / 2, y, 0xFFFFFF);
            y += lineHeight;
        }
        guiGraphics.drawString(this.font, CHECKBOX_LABEL, this.checkboxLabelX, this.checkboxLabelY, 0xFFFFFF, true);
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

    private void toggleDontShowAgain() {
        dontShowAgain = !dontShowAgain;
        if (checkboxButton != null) {
            String prefix = dontShowAgain ? "☑" : "☐";
            checkboxButton.setMessage(Component.literal(prefix));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isInsideCheckboxLabel(mouseX, mouseY)) {
            toggleDontShowAgain();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isInsideCheckboxLabel(double mouseX, double mouseY) {
        return mouseX >= this.checkboxLabelX
                && mouseX <= this.checkboxLabelX + this.checkboxLabelWidth
                && mouseY >= this.checkboxLabelY
                && mouseY <= this.checkboxLabelY + this.checkboxLabelHeight;
    }
}
