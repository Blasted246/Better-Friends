package com.blasted.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;

public class FeedbackSubMenuScreen extends Screen {
    private final Screen parent;
    private final AbstractWidget feedbackButton;
    private final AbstractWidget reportBugsButton;

    public FeedbackSubMenuScreen(Screen parent, AbstractWidget feedbackButton, AbstractWidget reportBugsButton) {
        super(Component.literal("Feedback..."));
        this.parent = parent;
        this.feedbackButton = feedbackButton;
        this.reportBugsButton = reportBugsButton;
    }

    @Override
    protected void init() {
        Button b1 = Button.builder(Component.literal("Give Feedback"), (btn) -> {
            if (this.feedbackButton instanceof Button bw) {
                bw.onPress(new net.minecraft.client.input.InputWithModifiers() { public int input() { return 0; } public int modifiers() { return 0; } });
            }
        }).bounds(this.width / 2 - 100, this.height / 4 + 48, 200, 20).build();
        if (this.feedbackButton == null || !this.feedbackButton.active) b1.active = false;
        this.addRenderableWidget(b1);

        Button b2 = Button.builder(Component.literal("Report Bugs"), (btn) -> {
            if (this.reportBugsButton instanceof Button bw) {
                bw.onPress(new net.minecraft.client.input.InputWithModifiers() { public int input() { return 0; } public int modifiers() { return 0; } });
            }
        }).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build();
        if (this.reportBugsButton == null || !this.reportBugsButton.active) b2.active = false;
        this.addRenderableWidget(b2);

        Button b3 = Button.builder(CommonComponents.GUI_BACK, (btn) -> {
            this.minecraft.gui.setScreen(this.parent);
        }).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build();
        this.addRenderableWidget(b3);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }
}
