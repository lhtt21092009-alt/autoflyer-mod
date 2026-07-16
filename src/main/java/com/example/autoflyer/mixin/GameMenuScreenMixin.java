package com.example.autoflyer.mixin;

import com.example.autoflyer.gui.SettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Them 1 nut nho "Auto Flyer" vao goc tren ben trai man hinh Pause (bam ESC),
 * bam vao mo thang man hinh cai dat, khong can vao Mods/ModMenu.
 */
@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void autoflyer$addButton(CallbackInfo ci) {
        GameMenuScreen self = (GameMenuScreen) (Object) this;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Auto Flyer"), b -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.setScreen(new SettingsScreen(self));
                })
                .dimensions(10, 10, 90, 20)
                .build());
    }
}
