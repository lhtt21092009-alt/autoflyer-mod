package com.example.autoflyer;

import com.example.autoflyer.gui.SettingsScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * Dang ky 1 phim tat trong Options > Controls (giong nhu "F3", "Toggle Perspective"...)
 * de mo nhanh man hinh cai dat Auto Flyer o bat cu dau trong game.
 * Mac dinh khong gan phim nao (KEY_UNKNOWN), nguoi choi tu chon phim trong Controls.
 */
public final class AutoFlyerKeybind {
    public static final KeyBinding OPEN_SETTINGS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autoflyer.settings",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(), // khong gan phim mac dinh, tranh xung dot
            "key.categories.autoflyer"
    ));

    private AutoFlyerKeybind() {}

    /**
     * Goi 1 lan duy nhat trong AutoFlyerMod.onInitializeClient(), CANG SOM CANG TOT.
     * Chi can tham chieu toi class nay la Java se chay static initializer o tren
     * (dang ky keybinding) dung luc mod khoi tao, truoc khi GameOptions duoc tao xong.
     * Neu de Java tu lazy-load class nay (vd goi tick() truoc), viec dang ky se dien ra
     * qua tre va lam game crash voi loi "GameOptions has already been initialised".
     */
    public static void init() {
        // co the de trong: chi can goi ham nay la du de kich hoat static init ben tren
    }

    /** Goi moi client tick. */
    public static void tick(MinecraftClient client) {
        while (OPEN_SETTINGS.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new SettingsScreen(null));
            }
        }
    }
}
