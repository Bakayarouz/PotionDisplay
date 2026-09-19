package com.example.potiondisplay;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectCategory;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PotionExpansion extends PlaceholderExpansion {

    private final PotionDisplay plugin;

    public PotionExpansion(PotionDisplay plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "potiondisplay";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().isEmpty() ? "Developer" : plugin.getPluginMeta().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("effects")) {
            List<PotionEffect> activeEffects = new ArrayList<>(player.getActivePotionEffects());

            if (activeEffects.isEmpty()) {
                return "";
            }

            // Sorting strategy: Good (0) -> Neutral (1) -> Bad (2)
            activeEffects.sort(Comparator.comparingInt(effect -> getCategoryPriority(effect.getType())));

            List<String> formattedIcons = new ArrayList<>();
            String fallback = plugin.getConfig().getString("fallback-format", "<gray>[%effect_name%]</gray>");

            for (PotionEffect effect : activeEffects) {
                String key = effect.getType().getKey().getKey().toLowerCase(Locale.ROOT);
                String icon = plugin.getConfig().getString("effects." + key);

                if (icon != null) {
                    formattedIcons.add(icon);
                } else {
                    formattedIcons.add(fallback.replace("%effect_name%", key));
                }
            }

            int maxPerLine = plugin.getConfig().getInt("max-per-line", 5);
            String separator = plugin.getConfig().getString("separator", " ");
            String lineSeparator = plugin.getConfig().getString("line-separator", "\n");

            // Wrap into secondary lines if active effects count exceeds limit
            if (maxPerLine > 0 && formattedIcons.size() > maxPerLine) {
                List<String> lines = new ArrayList<>();

                for (int i = 0; i < formattedIcons.size(); i += maxPerLine) {
                    int end = Math.min(i + maxPerLine, formattedIcons.size());
                    List<String> lineSublist = formattedIcons.subList(i, end);
                    lines.add(String.join(separator, lineSublist));
                }

                return String.join(lineSeparator, lines);
            }

            return String.join(separator, formattedIcons);
        }

        return null;
    }

    /**
     * Determines sorting priority using modern PotionEffectCategory:
     * 0 = Beneficial (Good)
     * 1 = Neutral
     * 2 = Harmful (Bad)
     */
    private int getCategoryPriority(PotionEffectType type) {
        PotionEffectCategory category = type.getEffectCategory();
        if (category == PotionEffectCategory.BENEFICIAL) {
            return 0;
        } else if (category == PotionEffectCategory.HARMFUL) {
            return 2;
        }
        return 1; // NEUTRAL
    }
}
