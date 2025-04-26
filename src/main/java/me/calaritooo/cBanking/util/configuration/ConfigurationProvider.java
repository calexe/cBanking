package me.calaritooo.cBanking.util.configuration;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationProvider {

    private final FileConfiguration config;

    public ConfigurationProvider(FileConfiguration config) {
        this.config = config;
    }

    public <T> T get(ConfigurationOption option, Class<T> type) {
        if (config == null) throw new IllegalStateException("ConfigProvider not initialized!");

        Object value = config.get(option.path());
        return type.cast(value != null ? value : option.defaultValue());
    }

    public String getString(ConfigurationOption option) {
        return get(option, String.class);
    }

    public int getInt(ConfigurationOption option) {
        return get(option, Integer.class);
    }

    public boolean getBoolean(ConfigurationOption option) {
        return get(option, Boolean.class);
    }

    public double getDouble(ConfigurationOption option) {
        return get(option, Double.class);
    }
}
