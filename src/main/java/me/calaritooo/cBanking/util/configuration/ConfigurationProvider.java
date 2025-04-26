package me.calaritooo.cBanking.util.configuration;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationProvider {

    private final FileConfiguration config;

    public ConfigurationProvider(FileConfiguration config) {
        this.config = config;
        populateDefaults();
    }

    public void populateDefaults() {
        for (ConfigurationOption option : ConfigurationOption.values()) {
            if (!config.contains(option.path())) {
                config.set(option.path(), option.defaultValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ConfigurationOption option, Class<T> type) {
        if (config == null) throw new IllegalStateException("ConfigProvider not initialized!");

        Object value = config.get(option.path());

        if (value == null) {
            return type.cast(option.defaultValue());
        }

        if (type == Double.class && value instanceof Number) {
            return (T) Double.valueOf(((Number) value).doubleValue());
        }

        return type.cast(value);
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
