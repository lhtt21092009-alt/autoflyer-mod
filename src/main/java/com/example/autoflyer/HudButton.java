package com.example.autoflyer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Ve 1 nut nho o goc tren ben phai man hinh khi dang choi (khong co GUI nao mo).
 * MouseMixin se dung getBounds() de kiem tra click co trung nut nay khong.
 */
public final class HudButton {
    public static final int WIDTH = 70;
    public static final int HEIGHT = 16;
    public static final int MARGIN = 6;

    private HudButton() {}

    public static int getX(int screenWidth) {
        return screenWidth - WIDTH - MARGIN;
    }

    public static int getY() {
        return MARGIN;
    }

    public static boolean isInside(int screenWidth, double mouseX, double mouseY) {
        int x = getX(screenWidth);
        int y = getY();
        return mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT;
    }

    /** Goi tu HudRenderCallback. */
    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null) return; // chi hien khi khong co GUI nao mo

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int x = getX(screenWidth);
        int y = getY();

        double scaledMouseX = client.mouse.getX() * screenWidth / client.getWindow().getWidth();
        double scaledMouseY = client.mouse.getY() * screenHeight / client.getWindow().getHeight();
        boolean hovered = isInside(screenWidth, scaledMouseX, scaledMouseY);

        int bg = hovered ? 0xAA555555 : 0xAA000000;
        context.fill(x, y, x + WIDTH, y + HEIGHT, bg);
        context.drawBorder(x, y, WIDTH, HEIGHT, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Auto Flyer"),
                x + WIDTH / 2, y + (HEIGHT - 8) / 2, 0xFFFFFF);
    }
}
