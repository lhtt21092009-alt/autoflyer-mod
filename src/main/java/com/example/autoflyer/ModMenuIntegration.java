package com.example.autoflyer;

import com.example.autoflyer.gui.SettingsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Cho phep mo man hinh cai dat tu nut "cai dat" cua mod nay trong Mod Menu.
 */
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SettingsScreen::new;
    }
}
