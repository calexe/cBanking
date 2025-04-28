package me.calaritooo.cBanking.util.configuration;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigurationProvider {

    private cBanking plugin;
    private FileConfiguration config;
    private boolean modified = false;

    public ConfigurationProvider(FileConfiguration config) {
        this.plugin = cBankingCore.getPlugin();
        this.config = config;
        populateDefaults();
    }

    private void populateDefaults() {
        for (ConfigurationOption option : ConfigurationOption.values()) {
            if (!config.contains(option.path())) {
                config.set(option.path(), option.defaultValue());
                modified = true;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ConfigurationOption option) {
        if (config == null) {
            throw new IllegalStateException("ConfigurationProvider not initialized!");
        }

        Object value = config.get(option.path());
        Class<?> expectedType = option.type();

        if (value == null) {
            config.set(option.path(), option.defaultValue());
            modified = true;
            return (T) option.defaultValue();
        }

        if (expectedType == String.class && value instanceof String) {
            return (T) value;
        }

        if (expectedType == Double.class && value instanceof Integer) {
            return (T) Double.valueOf(((Integer) value).doubleValue());
        }

        if (!expectedType.isInstance(value)) {
            // Wrong type, fix it
            plugin.getLogger().warning(
                    "[cBanking] Config key '" + option.path() + "' had wrong type (" +
                            value.getClass().getSimpleName() +
                            "), resetting to default value."
            );
            config.set(option.path(), option.defaultValue());
            modified = true;
            return (T) option.defaultValue();
        }

        return (T) value;
    }


    public void saveIfModified() {
        if (!modified) return;
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
            plugin.getLogger().info("[cBanking] Configuration updated and saved.");
        } catch (IOException e) {
            plugin.getLogger().severe("[cBanking] Failed to save config.yml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reload() {
        this.config = cBankingCore.getPlugin().getConfig();
        populateDefaults();

    }
}
