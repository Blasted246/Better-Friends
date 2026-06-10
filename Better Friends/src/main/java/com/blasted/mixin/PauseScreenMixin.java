package com.blasted.mixin;

import com.blasted.client.gui.OnlineSubMenuScreen;
import com.blasted.client.gui.FeedbackSubMenuScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {

    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void betterFriends$modifyPauseMenu(CallbackInfo ci) {
        List<AbstractWidget> widgets = new ArrayList<>();
        for (Object child : this.children()) {
            if (child instanceof AbstractWidget) {
                widgets.add((AbstractWidget) child);
            }
        }

        AbstractWidget feedbackButton = null;
        AbstractWidget reportBugsButton = null;
        AbstractWidget friendsButton = null;
        AbstractWidget optionsButton = null;
        AbstractWidget lanButton = null;
        AbstractWidget reportPlayerButton = null;

        List<AbstractWidget> smallButtons = new ArrayList<>();

        for (AbstractWidget widget : widgets) {
            String msg = widget.getMessage().getString();
            if (msg.contains("Feedback")) feedbackButton = widget;
            else if (msg.contains("Bug")) reportBugsButton = widget;
            else if (msg.contains("Friends")) friendsButton = widget;
            else if (msg.contains("Options")) optionsButton = widget;
            else if (msg.contains("LAN")) lanButton = widget;
            
            if (widget.getWidth() <= 24) {
                smallButtons.add(widget);
            }
        }

        if (smallButtons.size() >= 4) {
            smallButtons.sort((a, b) -> Integer.compare(a.getX(), b.getX()));
            reportBugsButton = smallButtons.get(0);
            feedbackButton = smallButtons.get(1);
            friendsButton = smallButtons.get(2);
            reportPlayerButton = smallButtons.get(3);
        }

        if (reportBugsButton != null) { this.removeWidget(reportBugsButton); reportBugsButton.visible = false; }
        if (feedbackButton != null) { this.removeWidget(feedbackButton); feedbackButton.visible = false; }
        if (friendsButton != null) { this.removeWidget(friendsButton); friendsButton.visible = false; }
        if (reportPlayerButton != null) { this.removeWidget(reportPlayerButton); reportPlayerButton.visible = false; }

        AbstractWidget mojangCustomAdditionsButton = null;
        if (optionsButton != null) {
            for (AbstractWidget widget : widgets) {
                if (widget.getWidth() == 204 && Math.abs(widget.getY() - (optionsButton.getY() - 24)) <= 2) {
                    mojangCustomAdditionsButton = widget;
                    break;
                }
            }
        }
        if (mojangCustomAdditionsButton != null) {
            this.removeWidget(mojangCustomAdditionsButton);
            mojangCustomAdditionsButton.visible = false;
        }

        final AbstractWidget finalFeedbackBtn = feedbackButton;
        final AbstractWidget finalReportBugsBtn = reportBugsButton;
        final AbstractWidget finalFriendsBtn = friendsButton;
        final AbstractWidget finalReportPlayerBtn = reportPlayerButton;
        final AbstractWidget finalOptionsBtn = optionsButton;
        final AbstractWidget finalLanBtn = lanButton;

        int row3Y = this.height / 4 + 72 - 16;
        int row4Y = this.height / 4 + 96 - 16;
        if (optionsButton != null) {
            row4Y = optionsButton.getY();
            if (mojangCustomAdditionsButton != null) {
                row4Y -= 24;
            }
            row3Y = row4Y - 24;
        }

        Button.Builder builder = Button.builder(Component.literal("Online..."), (btn) -> {
            this.minecraft.gui.setScreen(new OnlineSubMenuScreen(this, finalFriendsBtn, finalReportPlayerBtn));
        });
        builder.bounds(this.width / 2 + 4, row3Y, 98, 20);
        Button onlineBtn = builder.build();

        Button.Builder feedbackBuilder = Button.builder(Component.literal("Feedback..."), (btn) -> {
            this.minecraft.gui.setScreen(new FeedbackSubMenuScreen(this, finalFeedbackBtn, finalReportBugsBtn));
        });
        feedbackBuilder.bounds(this.width / 2 - 102, row3Y, 98, 20);
        Button feedbackSubMenuBtn = feedbackBuilder.build();
        this.addRenderableWidget(feedbackSubMenuBtn);

        this.addRenderableWidget(onlineBtn);

        if (optionsButton != null) {
            optionsButton.setX(this.width / 2 - 102);
            optionsButton.setY(row4Y);
            optionsButton.setWidth(98);
            optionsButton.visible = true;
            optionsButton.active = true;
        } else {
            Button dummyOptions = Button.builder(Component.literal("Options..."), (btn) -> {
                if (finalOptionsBtn instanceof Button bw) bw.onPress(new net.minecraft.client.input.InputWithModifiers() { public int input() { return 0; } public int modifiers() { return 0; } });
            }).bounds(this.width / 2 - 102, row4Y, 98, 20).build();
            this.addRenderableWidget(dummyOptions);
        }

        if (this.minecraft.hasSingleplayerServer()) {
            if (lanButton != null) {
                lanButton.setX(this.width / 2 + 4);
                lanButton.setY(row4Y);
                lanButton.setWidth(98);
                lanButton.visible = true;
                lanButton.active = true;
            } else {
                Button dummyLan = Button.builder(Component.literal("Open to LAN"), (btn) -> {
                    if (finalLanBtn instanceof Button bw) bw.onPress(new net.minecraft.client.input.InputWithModifiers() { public int input() { return 0; } public int modifiers() { return 0; } });
                }).bounds(this.width / 2 + 4, row4Y, 98, 20).build();
                this.addRenderableWidget(dummyLan);
            }
        } else {
            net.minecraft.core.Registry<net.minecraft.server.dialog.Dialog> dialogRegistry = this.minecraft.player.connection.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.DIALOG);
            java.util.Optional<? extends net.minecraft.core.Holder<net.minecraft.server.dialog.Dialog>> additions = java.util.Optional.empty();
            java.util.Optional<? extends net.minecraft.core.HolderSet<net.minecraft.server.dialog.Dialog>> maybeCustomAdditions = dialogRegistry.get(net.minecraft.tags.DialogTags.PAUSE_SCREEN_ADDITIONS);
            if (maybeCustomAdditions.isPresent()) {
               net.minecraft.core.HolderSet<net.minecraft.server.dialog.Dialog> customAdditions = maybeCustomAdditions.get();
               if (customAdditions.size() > 0) {
                  if (customAdditions.size() == 1) {
                     additions = java.util.Optional.of(customAdditions.get(0));
                  } else {
                     additions = dialogRegistry.get(net.minecraft.server.dialog.Dialogs.CUSTOM_OPTIONS);
                  }
               }
            }
            if (additions.isEmpty()) {
                net.minecraft.server.ServerLinks serverLinks = this.minecraft.player.connection.serverLinks();
                if (!serverLinks.isEmpty()) {
                    additions = dialogRegistry.get(net.minecraft.server.dialog.Dialogs.SERVER_LINKS);
                }
            }
            
            final java.util.Optional<? extends net.minecraft.core.Holder<net.minecraft.server.dialog.Dialog>> finalAdditions = additions;
            
            Button.Builder slBuilder = Button.builder(
                finalAdditions.isPresent() ? finalAdditions.get().value().common().computeExternalTitle() : Component.literal("Server Links..."), 
                (btn) -> {
                    if (finalAdditions.isPresent()) {
                        this.minecraft.player.connection.showDialog(finalAdditions.get(), this);
                    }
                }
            );
            slBuilder.bounds(this.width / 2 + 4, row4Y, 98, 20);
            Button customServerLinks = slBuilder.build();
            if (finalAdditions.isEmpty()) {
                customServerLinks.active = false;
            }
            this.addRenderableWidget(customServerLinks);
        }
    }
}
