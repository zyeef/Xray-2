package com.example.xray;

import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class XrayConfig {
    /** Alcance em blocos (16 a 256). */
    public static int range = 64;
    public static boolean unlimited = false;
    /** 0 = blocos escondidos invisíveis, 100 = normais. */
    public static int hiddenVisibility = 0;
    public static boolean showWater = false;
    public static boolean showLava = true;
    public static boolean nightVision = true;

    private XrayConfig() {}

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("xrayhelper-client.properties");
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static int parseInt(String s, int def) {
        try {
            return s == null ? def : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
            p.load(r);
        } catch (IOException e) {
            return;
        }
        range = clamp(parseInt(p.getProperty("range"), range), 16, 256);
        unlimited = Boolean.parseBoolean(p.getProperty("unlimited", "false"));
        hiddenVisibility = clamp(parseInt(p.getProperty("hiddenVisibility"), hiddenVisibility), 0, 100);
        showWater = Boolean.parseBoolean(p.getProperty("showWater", "false"));
        showLava = Boolean.parseBoolean(p.getProperty("showLava", "true"));
        nightVision = Boolean.parseBoolean(p.getProperty("nightVision", "true"));
        for (OreType t : OreType.values()) {
            t.enabled = Boolean.parseBoolean(p.getProperty("ore." + t.name(), "true"));
        }
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("range", Integer.toString(range));
        p.setProperty("unlimited", Boolean.toString(unlimited));
        p.setProperty("hiddenVisibility", Integer.toString(hiddenVisibility));
        p.setProperty("showWater", Boolean.toString(showWater));
        p.setProperty("showLava", Boolean.toString(showLava));
        p.setProperty("nightVision", Boolean.toString(nightVision));
        for (OreType t : OreType.values()) {
            p.setProperty("ore." + t.name(), Boolean.toString(t.enabled));
        }
        try (Writer w = Files.newBufferedWriter(file(), StandardCharsets.UTF_8)) {
            p.store(w, "Xray Helper");
        } catch (IOException ignored) {
        }
    }
}
