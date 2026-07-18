package com.blasted.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.ArrayList;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void betterFriends$modifyMainMenu(CallbackInfo ci) {
        List<AbstractWidget> widgets = new ArrayList<>();
        for (Object child : this.children()) {
            if (child instanceof AbstractWidget) {
                widgets.add((AbstractWidget) child);
            }
        }

        AbstractWidget optionsButton = null;
        AbstractWidget quitButton = null;
        List<AbstractWidget> mainButtons = new ArrayList<>();
        List<AbstractWidget> smallButtons = new ArrayList<>();

        for (AbstractWidget widget : widgets) {
            if (widget.getWidth() <= 24) {
                smallButtons.add(widget);
            } else {
                String msg = widget.getMessage().getString();
                String key = "";
                if (widget.getMessage().getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc) {
                    key = tc.getKey();
                }
                
                if (key.equals("menu.options") || msg.contains("Options")) {
                    optionsButton = widget;
                } else if (key.equals("menu.quit") || msg.contains("Quit")) {
                    quitButton = widget;
                } else {
                    mainButtons.add(widget);
                }
            }
        }

        if (optionsButton != null && quitButton != null && smallButtons.size() >= 3) {
            List<AbstractWidget> vanillaSmallButtons = new ArrayList<>();
            List<AbstractWidget> modButtons = new ArrayList<>();
            for (int i = 0; i < smallButtons.size(); i++) {
                if (i < 3) {
                    vanillaSmallButtons.add(smallButtons.get(i));
                } else {
                    modButtons.add(smallButtons.get(i));
                }
            }

            vanillaSmallButtons.sort((a, b) -> Integer.compare(a.getX(), b.getX()));
            AbstractWidget friendsButton = vanillaSmallButtons.get(0);
            AbstractWidget languageButton = vanillaSmallButtons.get(1);
            AbstractWidget accessibilityButton = vanillaSmallButtons.get(2);

            AbstractWidget lowestMainButton = null;
            for (AbstractWidget widget : mainButtons) {
                if (widget.getY() < this.height - 50) {
                    if (lowestMainButton == null || widget.getY() > lowestMainButton.getY()) {
                        lowestMainButton = widget;
                    }
                }
            }

            int bottomY = optionsButton.getY();
            if (lowestMainButton != null) {
                bottomY = lowestMainButton.getY() + 36;
            }
            // Cap bottomY to prevent Options/Quit from ever going off-screen
            bottomY = Math.min(bottomY, this.height - 52);

            int originalOptionsY = optionsButton.getY();
            int yOffset = bottomY - originalOptionsY;

            accessibilityButton.visible = false;
            accessibilityButton.active = false;
            accessibilityButton.setX(10000); // hide it

            for (AbstractWidget widget : widgets) {
                if (widget != languageButton && widget != friendsButton && widget != accessibilityButton && !modButtons.contains(widget)) {
                    if (widget.getY() >= originalOptionsY && widget.getY() < this.height - 50) {
                        widget.setY(widget.getY() + yOffset);
                    }
                }
            }

            languageButton.setX(this.width / 2 - 124);
            languageButton.setY(bottomY);

            friendsButton.setX(this.width / 2 + 104);
            friendsButton.setY(bottomY);

            if (!modButtons.isEmpty()) {
                int modSpacing = 4;
                int totalWidth = 0;
                for (AbstractWidget modBtn : modButtons) {
                    totalWidth += modBtn.getWidth();
                }
                totalWidth += (modButtons.size() - 1) * modSpacing;
                
                int startX = this.width / 2 - totalWidth / 2;
                int newRowY = this.height - 32; // Anchored to the bottom of the screen
                
                int currentX = startX;
                for (AbstractWidget modBtn : modButtons) {
                    modBtn.setX(currentX);
                    modBtn.setY(newRowY);
                    currentX += modBtn.getWidth() + modSpacing;
                }
            }
        }
    }
}
