package me.calaritooo.cBanking.util;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.messages.Message;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResourceUpdater {

    public static <T extends Enum<T>> void update(cBanking plugin, String fileName, Class<T> enumClass) {
        File dataFolder = plugin.getDataFolder();
        File file = new File(dataFolder, fileName);
        File backupFile = new File(dataFolder, fileName + ".old");

        int keysAdded = 0;
        int keysRemoved = 0;

        try {
            if (!file.exists()) {
                plugin.saveResource(fileName, false);
                plugin.getLogger().info("Created " + fileName + "!");
            }

            YamlConfiguration serverConfig = YamlConfiguration.loadConfiguration(file);

            InputStream resourceStream = plugin.getResource(fileName);
            if (resourceStream == null) {
                plugin.getLogger().severe(fileName + " not found! Contact a developer.");
                return;
            }
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));

            boolean hasChanges = false;

            for (String key : serverConfig.getKeys(true)) {
                if (!defaultConfig.contains(key)) {
                    serverConfig.set(key, null);
                    keysRemoved++;
                    hasChanges = true;
                }
            }

            for (T enumEntry : enumClass.getEnumConstants()) {
                String path = enumEntry.toString();

                if (!serverConfig.contains(path)) {
                    Object defaultValue = getDefaultValueFromEnum(enumEntry);
                    serverConfig.set(path, defaultValue);
                    keysAdded++;
                    hasChanges = true;
                }
            }

            if (hasChanges) {
                if (backupFile.exists()) backupFile.delete();
                if (file.renameTo(backupFile)) {
                    plugin.getLogger().info("Backup created for " + backupFile.getName() + "!");
                } else {
                    plugin.getLogger().warning("Backup failed for " + fileName);
                }

                serverConfig.save(file);
                plugin.getLogger().info(fileName + " updated: +" + keysAdded + " keys added, -" + keysRemoved + " keys removed.");
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Error updating " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Object getDefaultValueFromEnum(Enum<?> enumEntry) {
        if (enumEntry instanceof ConfigurationOption configOption) {
            return configOption.defaultValue();
        } else if (enumEntry instanceof Message message) {
            return message.defaultMessage();
        } else {
            throw new IllegalArgumentException("[cBanking] Unsupported enum type: " + enumEntry.getClass().getSimpleName());
        }
    }
}

