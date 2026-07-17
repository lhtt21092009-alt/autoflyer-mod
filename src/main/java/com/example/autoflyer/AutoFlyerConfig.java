package com.example.autoflyer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Config cua Auto Flyer v3 - chi con che do Auto Build (Litematica),
 * duoc ghi ra config/autoflyer.json de giu lai sau khi thoat game.
 */
public class AutoFlyerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("autoflyer.json");

    public double litematicaHoverHeight = 2.0; // bay cao hon block dich bao nhieu (y+2 mac dinh)
    public double litematicaSpeed = 0.8; // toc do bay (block/tick)

    public static AutoFlyerConfig INSTANCE = load();

    public static AutoFlyerConfig load() {
        if (Files.exists(FILE)) {
            try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                AutoFlyerConfig cfg = GSON.fromJson(reader, AutoFlyerConfig.class);
                if (cfg != null) return cfg;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new AutoFlyerConfig();
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
