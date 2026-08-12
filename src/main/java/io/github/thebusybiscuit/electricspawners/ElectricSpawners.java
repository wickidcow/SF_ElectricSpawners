package io.github.thebusybiscuit.electricspawners;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;

public final class ElectricSpawners extends JavaPlugin implements SlimefunAddon {

    private static final String SPAWNER_TEXTURE =
            "db6bd9727abb55d5415265789d4f2984781a343c68dcaf57f554a5e9aa1cd";

    /**
     * Bukkit renamed a handful of long-standing EntityType enum constants.
     * Keep accepting the names shipped in historical ElectricSpawners configs so
     * existing servers do not need to rewrite their configuration or Slimefun IDs.
     */
    private static final Map<String, String> LEGACY_ENTITY_ALIASES = Map.of(
            "MUSHROOM_COW", "MOOSHROOM",
            "PIG_ZOMBIE", "ZOMBIFIED_PIGLIN",
            "SNOWMAN", "SNOW_GOLEM");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Preserve the original project's bStats identity.
        new Metrics(this, 6163);

        boolean forceDisableAI = config.getBoolean("mob-ai.force-disable", false);
        boolean defaultDisabledAI = config.getBoolean("mob-ai.default-disabled", false);

        ItemGroup itemGroup = new ItemGroup(
                new NamespacedKey(this, "electric_spawners"),
                new CustomItemStack(
                        PlayerHead.getItemStack(PlayerSkin.fromHashCode(SPAWNER_TEXTURE)),
                        "&9Electric Spawners"));
        Research research = new Research(
                new NamespacedKey(this, "electric_spawners"), 4820, "Powered Spawners", 30);

        int registered = 0;
        for (String configuredMob : config.getStringList("mobs")) {
            String legacyMobId = normalizeEntityName(configuredMob);
            EntityType type = resolveEntityType(legacyMobId);

            if (type == null) {
                getLogger().log(
                        Level.WARNING,
                        "Skipping Electric Spawner for unknown EntityType \"{0}\".",
                        configuredMob);
                continue;
            }

            if (!type.isAlive() || !type.isSpawnable()) {
                getLogger().log(
                        Level.WARNING,
                        "Skipping Electric Spawner for EntityType \"{0}\" because it is not a spawnable living entity.",
                        configuredMob);
                continue;
            }

            try {
                new ElectricSpawner(
                                itemGroup,
                                legacyMobId,
                                type,
                                research,
                                forceDisableAI,
                                defaultDisabledAI)
                        .register(this);
                registered++;
            } catch (RuntimeException exception) {
                getLogger().log(
                        Level.SEVERE,
                        "Failed to register an Electric Spawner for EntityType \"" + configuredMob + "\".",
                        exception);
            }
        }

        research.register();
        getLogger().info("Registered " + registered + " Electric Spawner type(s) for Slimefun.");
    }

    private String normalizeEntityName(String configuredMob) {
        if (configuredMob == null) {
            return "";
        }

        return configuredMob
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');
    }

    private EntityType resolveEntityType(String configuredMob) {
        if (configuredMob.isEmpty()) {
            return null;
        }

        String modernName = LEGACY_ENTITY_ALIASES.getOrDefault(configuredMob, configuredMob);
        try {
            return EntityType.valueOf(modernName);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/wickidcow/SF_ElectricSpawners/issues";
    }
}
