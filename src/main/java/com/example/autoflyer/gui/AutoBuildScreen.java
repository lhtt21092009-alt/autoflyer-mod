package com.example.autoflyer.gui;

import com.example.autoflyer.AutoFlyerConfig;
import com.example.autoflyer.AutoFlyerControl;
import com.example.autoflyer.litematica.LitematicaNavigator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * Man hinh cai dat Auto Flyer v3 - chi con 1 chuc nang duy nhat: Auto Build theo Litematica.
 * Tu dong tim block con thieu (trong lop dang hien cua Litematica) gan nguoi choi nhat,
 * bay den ngay phia tren no de mod print (vd Meteor Litematica Printer) tu dat block.
 */
public class AutoBuildScreen extends Screen {
    private final Screen parent;
    private final AutoFlyerConfig cfg = AutoFlyerConfig.INSTANCE;

    private TextFieldWidget hoverHeightField;
    private TextFieldWidget speedField;
    private ButtonWidget startPauseButton;

    public AutoBuildScreen(Screen parent) {
        super(Text.literal("Auto Build - Litematica"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int fieldHeight = 20;
        int y = 70;

        hoverHeightField = new TextFieldWidget(this.textRenderer, centerX - 90, y, 70, fieldHeight, Text.literal("Do cao"));
        hoverHeightField.setMaxLength(6);
        hoverHeightField.setTextPredicate(s -> s.isEmpty() || s.matches("\\d*(\\.\\d*)?"));
        hoverHeightField.setText(String.valueOf(cfg.litematicaHoverHeight));
        addDrawableChild(hoverHeightField);

        speedField = new TextFieldWidget(this.textRenderer, centerX + 20, y, 70, fieldHeight, Text.literal("Toc do"));
        speedField.setMaxLength(6);
        speedField.setTextPredicate(s -> s.isEmpty() || s.matches("\\d*(\\.\\d*)?"));
        speedField.setText(String.valueOf(cfg.litematicaSpeed));
        addDrawableChild(speedField);

        startPauseButton = ButtonWidget.builder(
                Text.literal(LitematicaNavigator.isRunning() ? "Pause" : "Start"),
                b -> toggleStart()
        ).dimensions(centerX - 60, this.height / 2 + 40, 120, 20).build();
        addDrawableChild(startPauseButton);
    }

    private void toggleStart() {
        saveFieldsToConfig();
        AutoFlyerControl.toggleAutoBuild();
        startPauseButton.setMessage(Text.literal(LitematicaNavigator.isRunning() ? "Pause" : "Start"));
    }

    private void saveFieldsToConfig() {
        cfg.litematicaHoverHeight = hoverHeightField != null && !hoverHeightField.getText().isEmpty()
                ? Double.parseDouble(hoverHeightField.getText()) : 2.0;
        cfg.litematicaSpeed = speedField != null && !speedField.getText().isEmpty()
                ? Double.parseDouble(speedField.getText()) : 0.8;
        cfg.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Do cao bay tren block (y+)"), this.width / 2 - 90, 58, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Toc do bay"), this.width / 2 + 55, 58, 0xAAAAAA);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Tu dong tim block con thieu (theo lop dang hien trong Litematica) va bay den."),
                this.width / 2, 105, 0x88CCFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Dung ngay tren block cho mod print dat xong roi tu bay tiep."),
                this.width / 2, 120, 0x88CCFF);

        if (!FabricLoader.getInstance().isModLoaded("litematica")) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("CANH BAO: khong tim thay mod Litematica dang cai!"),
                    this.width / 2, 140, 0xFF5555);
        }
    }

    @Override
    public void close() {
        saveFieldsToConfig();
        if (this.client != null) this.client.setScreen(parent);
    }
}
