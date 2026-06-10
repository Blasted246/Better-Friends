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
        AbstractWidget realmsButton = null;

        List<AbstractWidget> smallButtons = new ArrayList<>();
        for (AbstractWidget widget : widgets) {
            if (widget.getWidth() <= 24) {
                smallButtons.add(widget);
            } else if (widget.getWidth() >= 90 && widget.getWidth() <= 104) {
                if (widget.getX() < this.width / 2) {
                    optionsButton = widget;
                } else {
                    quitButton = widget;
                }
            } else if (widget.getWidth() == 200) {
                if (widget.getY() < this.height - 50) {
                    if (realmsButton == null || widget.getY() > realmsButton.getY()) {
                        realmsButton = widget;
                    }
                }
            }
        }

        if (optionsButton != null && quitButton != null && smallButtons.size() >= 3) {
            smallButtons.sort((a, b) -> Integer.compare(a.getX(), b.getX()));
            AbstractWidget friendsButton = smallButtons.get(0);
            AbstractWidget languageButton = smallButtons.get(1);
            AbstractWidget accessibilityButton = smallButtons.get(2);

            int bottomY = optionsButton.getY();
            if (realmsButton != null) {
                bottomY = realmsButton.getY() + 36;
            }

            accessibilityButton.visible = false;
            accessibilityButton.active = false;
            accessibilityButton.setX(10000); // hide it

            optionsButton.setY(bottomY);
            quitButton.setY(bottomY);

            languageButton.setX(this.width / 2 - 124);
            languageButton.setY(bottomY);

            friendsButton.setX(this.width / 2 + 104);
            friendsButton.setY(bottomY);
        }
    }
}
