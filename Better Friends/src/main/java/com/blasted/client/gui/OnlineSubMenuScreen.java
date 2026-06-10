package com.blasted.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;

public class OnlineSubMenuScreen extends Screen {
    private final Screen parent;
    private final AbstractWidget friendsButton;
    private final AbstractWidget reportPlayerButton;

    public OnlineSubMenuScreen(Screen parent, AbstractWidget friendsButton, AbstractWidget reportPlayerButton) {
        super(Component.literal("Online..."));
        this.parent = parent;
        this.friendsButton = friendsButton;
        this.reportPlayerButton = reportPlayerButton;
    }

    @Override
    protected void init() {
        Button b1 = Button.builder(Component.literal("Friends"), (btn) -> {
            if (this.friendsButton instanceof Button bw) {
                bw.onPress(new net.minecraft.client.input.InputWithModifiers() { public int input() { return 0; } public int modifiers() { return 0; } });
            }
        }).bounds(this.width / 2 - 100, this.height / 4 + 48, 200, 20).build();
        this.addRenderableWidget(b1);

        Button b2 = Button.builder(Component.literal("Player Reporting"), (btn) -> {
            if (this.reportPlayerButton instanceof Button bw) {
                bw.onPress(new net.minecraft.client.input.InputWithModifiers() { public int input() { return 0; } public int modifiers() { return 0; } });
            }
        }).bounds(this.width / 2 - 100, this.height / 4 + 72, 200, 20).build();
        if (this.reportPlayerButton == null || !this.reportPlayerButton.active) b2.active = false;
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
