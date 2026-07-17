package com.example.autoflyer;

import com.example.autoflyer.litematica.LitematicaNavigator;

/**
 * Noi tap trung bat/tat 2 chuc nang (Auto Fly va Auto Build), dam bao khong bao gio ca 2
 * cung chay 1 luc (vi ca 2 deu dieu khien van toc nguoi choi, chay cung se bi giat/xung dot).
 * Dung chung cho ca GUI va cac phim tat.
 */
public final class AutoFlyerControl {
    private AutoFlyerControl() {}

    public static void toggleAutoFly() {
        if (FlightTask.isRunning()) {
            FlightTask.stop();
        } else {
            LitematicaNavigator.stop();
            FlightTask.start();
        }
    }

    public static void toggleAutoBuild() {
        if (LitematicaNavigator.isRunning()) {
            LitematicaNavigator.stop();
        } else {
            FlightTask.stop();
            LitematicaNavigator.start();
        }
    }

    public static void stopAll() {
        FlightTask.stop();
        LitematicaNavigator.stop();
    }
}
