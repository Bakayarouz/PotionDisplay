package com.example.potiondisplay;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class PotionDisplay extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Register command
        if (getCommand("potiondisplay") != null) {
            getCommand("potiondisplay").setExecutor(this);
        }

        // Register PlaceholderAPI expansion
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PotionExpansion(this).register();
            getLogger().info("Successfully registered PotionDisplay PlaceholderAPI expansion!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Disabling expansion registration.");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("potiondisplay.admin")) {
                sender.sendMessage("§cYou do not have permission to execute this command.");
                return true;
            }

            reloadConfig();
            sender.sendMessage("§a[PotionDisplay] Configuration reloaded successfully!");
            return true;
        }

        sender.sendMessage("§eUsage: /potiondisplay reload");
        return true;
    }
}
