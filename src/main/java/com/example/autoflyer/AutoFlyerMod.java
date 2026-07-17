package com.example.autoflyer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import com.example.autoflyer.litematica.LitematicaNavigator;

public class AutoFlyerMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // QUAN TRONG: dang ky keybinding NGAY TU DAU, truoc khi GameOptions duoc tao,
        // neu khong game se crash voi loi "GameOptions has already been initialised"
        AutoFlyerKeybind.init();

        // Chay logic Auto Fly + Auto Build + kiem tra phim tat moi client tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FlightTask.tick(client);
            LitematicaNavigator.tick(client);
            AutoFlyerKeybind.tick(client);
        });

        // Ve nut "Cai dat" o goc tren ben phai man hinh khi dang choi
        HudRenderCallback.EVENT.register((context, tickCounter) -> HudButton.render(context));

        // Nut "Auto Flyer" trong man hinh Pause (ESC) duoc them qua GameMenuScreenMixin
    }
}
