package com.example.potiondisplay;

import org.bukkit.plugin.java.JavaPlugin;

public final class PotionDisplay extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PotionExpansion(this).register();
            getLogger().info("Successfully registered PotionDisplay PlaceholderAPI expansion!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Disabling expansion registration.");
        }
    }
}
