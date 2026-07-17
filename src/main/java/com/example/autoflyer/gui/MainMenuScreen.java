package com.example.autoflyer.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * "GUI day du" cua Auto Flyer: man hinh chinh voi 3 nut:
 *  1) Auto Build  - mo man hinh cai dat che do tu dong theo Litematica.
 *  2) Auto Fly    - mo man hinh cai dat che do bay ziczac thu cong.
 *  3) Tro ve       - dong man hinh nay, quay lai noi da mo no (game hoac Pause menu).
 */
public class MainMenuScreen extends Screen {
    private final Screen parent;

    public MainMenuScreen(Screen parent) {
        super(Text.literal("Auto Flyer"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int gap = 8;

        addDrawableChild(ButtonWidget.builder(Text.literal("Auto Build (Litematica)"), b ->
                        this.client.setScreen(new AutoBuildScreen(this)))
                .dimensions(centerX - buttonWidth / 2, centerY - buttonHeight - gap, buttonWidth, buttonHeight)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Auto Fly (thu cong)"), b ->
                        this.client.setScreen(new AutoFlyScreen(this)))
                .dimensions(centerX - buttonWidth / 2, centerY, buttonWidth, buttonHeight)
                .build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Tro ve"), b -> this.close())
                .dimensions(centerX - buttonWidth / 2, centerY + buttonHeight + gap, buttonWidth, buttonHeight)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
    }

    @Override
    public void close() {
        if (this.client != null) this.client.setScreen(parent);
    }
}
