package com.example.autoflyer;

import com.example.autoflyer.gui.MainMenuScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * Dang ky 3 phim tat trong Options > Controls (muc "Auto Flyer"):
 *  1) Mo GUI day du (man hinh chinh co 3 nut: Auto Build / Auto Fly / Tro ve)
 *  2) Bat/tat Auto Build ngay lap tuc (khong can mo GUI)
 *  3) Bat/tat Auto Fly ngay lap tuc (khong can mo GUI)
 * Mac dinh ca 3 deu KHONG gan phim nao, nguoi choi tu chon phim trong Controls.
 */
public final class AutoFlyerKeybind {
    public static final KeyBinding OPEN_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autoflyer.opengui",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "key.categories.autoflyer"
    ));

    public static final KeyBinding TOGGLE_AUTO_BUILD = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autoflyer.toggle_autobuild",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "key.categories.autoflyer"
    ));

    public static final KeyBinding TOGGLE_AUTO_FLY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autoflyer.toggle_autofly",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "key.categories.autoflyer"
    ));

    private AutoFlyerKeybind() {}

    /** Goi 1 lan trong AutoFlyerMod.onInitializeClient(), CANG SOM CANG TOT (xem ghi chu trong init()). */
    public static void init() {
        // chi can tham chieu class nay la du de kich hoat static init (dang ky keybinding) o tren,
        // truoc khi GameOptions duoc tao xong - neu khong game se crash.
    }

    /** Goi moi client tick. */
    public static void tick(MinecraftClient client) {
        while (OPEN_GUI.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new MainMenuScreen(null));
            }
        }
        while (TOGGLE_AUTO_BUILD.wasPressed()) {
            AutoFlyerControl.toggleAutoBuild();
        }
        while (TOGGLE_AUTO_FLY.wasPressed()) {
            AutoFlyerControl.toggleAutoFly();
        }
    }
}
