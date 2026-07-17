package com.example.autoflyer.mixin;

import com.example.autoflyer.HudButton;
import com.example.autoflyer.gui.MainMenuScreen;
import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bat su kien click chuot khi dang o trong game (khong mo GUI nao) de xac dinh
 * nguoi choi co bam vao nut "Cai dat" o goc tren ben phai hay khong.
 */
@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow private double x;
    @Shadow private double y;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void autoflyer$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return; // co GUI khac dang mo thi bo qua
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT || action != GLFW.GLFW_PRESS) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        double scaledX = this.x * screenWidth / client.getWindow().getWidth();
        double scaledY = this.y * screenHeight / client.getWindow().getHeight();

        if (HudButton.isInside(screenWidth, scaledX, scaledY)) {
            client.setScreen(new MainMenuScreen(null));
            ci.cancel();
        }
    }
}
