package com.example.autoflyer.gui;

import com.example.autoflyer.AutoFlyerConfig;
import com.example.autoflyer.AutoFlyerControl;
import com.example.autoflyer.FlightTask;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * Man hinh cai dat che do "Auto Fly" (thu cong): 2 vi tri (moi vi tri 3 o nhap x/y/z +
 * nut "Set here" + nut "Clear"), 1 o "Buoc ziczac", va nut Start/Pause.
 */
public class AutoFlyScreen extends Screen {
    private final Screen parent;
    private final AutoFlyerConfig cfg = AutoFlyerConfig.INSTANCE;

    private TextFieldWidget x1Field, y1Field, z1Field;
    private TextFieldWidget x2Field, y2Field, z2Field;
    private TextFieldWidget zigzagStepField;
    private ButtonWidget startPauseButton;

    public AutoFlyScreen(Screen parent) {
        super(Text.literal("Auto Fly - Cai dat"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int fieldWidth = 70;
        int fieldHeight = 20;
        int gap = 6;

        int row1Y = 55;
        int row2Y = 135;

        int startX1 = centerX - (fieldWidth * 3 + gap * 2) / 2;
        x1Field = new TextFieldWidget(this.textRenderer, startX1, row1Y, fieldWidth, fieldHeight, Text.literal("X"));
        y1Field = new TextFieldWidget(this.textRenderer, startX1 + fieldWidth + gap, row1Y, fieldWidth, fieldHeight, Text.literal("Y"));
        z1Field = new TextFieldWidget(this.textRenderer, startX1 + (fieldWidth + gap) * 2, row1Y, fieldWidth, fieldHeight, Text.literal("Z"));
        setNumericOnly(x1Field);
        setNumericOnly(y1Field);
        setNumericOnly(z1Field);
        x1Field.setText(fmt(cfg.x1));
        y1Field.setText(fmt(cfg.y1));
        z1Field.setText(fmt(cfg.z1));
        addDrawableChild(x1Field);
        addDrawableChild(y1Field);
        addDrawableChild(z1Field);

        addDrawableChild(ButtonWidget.builder(Text.literal("Set here"), b -> setHere(1))
                .dimensions(centerX - 130, row1Y + 28, 120, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), b -> clear(1))
                .dimensions(centerX + 10, row1Y + 28, 120, 20).build());

        int startX2 = centerX - (fieldWidth * 3 + gap * 2) / 2;
        x2Field = new TextFieldWidget(this.textRenderer, startX2, row2Y, fieldWidth, fieldHeight, Text.literal("X"));
        y2Field = new TextFieldWidget(this.textRenderer, startX2 + fieldWidth + gap, row2Y, fieldWidth, fieldHeight, Text.literal("Y"));
        z2Field = new TextFieldWidget(this.textRenderer, startX2 + (fieldWidth + gap) * 2, row2Y, fieldWidth, fieldHeight, Text.literal("Z"));
        setNumericOnly(x2Field);
        setNumericOnly(y2Field);
        setNumericOnly(z2Field);
        x2Field.setText(fmt(cfg.x2));
        y2Field.setText(fmt(cfg.y2));
        z2Field.setText(fmt(cfg.z2));
        addDrawableChild(x2Field);
        addDrawableChild(y2Field);
        addDrawableChild(z2Field);

        addDrawableChild(ButtonWidget.builder(Text.literal("Set here"), b -> setHere(2))
                .dimensions(centerX - 130, row2Y + 28, 120, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear"), b -> clear(2))
                .dimensions(centerX + 10, row2Y + 28, 120, 20).build());

        int stepY = row2Y + 60;
        zigzagStepField = new TextFieldWidget(this.textRenderer, centerX - 40, stepY, 80, fieldHeight, Text.literal("Buoc"));
        zigzagStepField.setMaxLength(6);
        zigzagStepField.setTextPredicate(s -> s.isEmpty() || s.matches("\\d*(\\.\\d*)?"));
        zigzagStepField.setText(cfg.zigzagStep == 0 ? "" : String.valueOf(cfg.zigzagStep));
        addDrawableChild(zigzagStepField);

        startPauseButton = ButtonWidget.builder(
                Text.literal(FlightTask.isRunning() ? "Pause" : "Start"),
                b -> toggleStart()
        ).dimensions(centerX - 60, this.height - 32, 120, 20).build();
        addDrawableChild(startPauseButton);
    }

    private void setNumericOnly(TextFieldWidget field) {
        field.setMaxLength(16);
        field.setTextPredicate(s -> s.isEmpty() || s.matches("-?\\d*(\\.\\d*)?"));
    }

    private String fmt(double v) {
        return v == 0 ? "" : String.valueOf(v);
    }

    private double parse(TextFieldWidget field) {
        try {
            return Double.parseDouble(field.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setHere(int point) {
        if (this.client == null || this.client.player == null) return;
        var pos = this.client.player.getPos();
        if (point == 1) {
            x1Field.setText(fmt(pos.x));
            y1Field.setText(fmt(pos.y));
            z1Field.setText(fmt(pos.z));
        } else {
            x2Field.setText(fmt(pos.x));
            y2Field.setText(fmt(pos.y));
            z2Field.setText(fmt(pos.z));
        }
    }

    private void clear(int point) {
        if (point == 1) {
            x1Field.setText("");
            y1Field.setText("");
            z1Field.setText("");
        } else {
            x2Field.setText("");
            y2Field.setText("");
            z2Field.setText("");
        }
    }

    private void toggleStart() {
        saveFieldsToConfig();
        AutoFlyerControl.toggleAutoFly();
        startPauseButton.setMessage(Text.literal(FlightTask.isRunning() ? "Pause" : "Start"));
    }

    private void saveFieldsToConfig() {
        cfg.x1 = parse(x1Field);
        cfg.y1 = parse(y1Field);
        cfg.z1 = parse(z1Field);
        cfg.x2 = parse(x2Field);
        cfg.y2 = parse(y2Field);
        cfg.z2 = parse(z2Field);
        cfg.zigzagStep = zigzagStepField != null && !zigzagStepField.getText().isEmpty()
                ? Double.parseDouble(zigzagStepField.getText()) : 2.0;
        cfg.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Vi tri 1"), this.width / 2, 40, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Vi tri 2"), this.width / 2, 120, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Buoc ziczac (block)"), this.width / 2, 195, 0xAAAAAA);
    }

    @Override
    public void close() {
        saveFieldsToConfig();
        if (this.client != null) this.client.setScreen(parent);
    }
}
