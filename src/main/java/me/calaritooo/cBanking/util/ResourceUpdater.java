package me.calaritooo.cBanking.util;

import me.calaritooo.cBanking.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class ResourceUpdater {

    public static void update(cBanking plugin, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        File backupFile = new File(plugin.getDataFolder(), fileName + ".old");

        try {
            if (!file.exists()) {
                plugin.saveResource(fileName, false);
                plugin.getLogger().info("Created " + fileName + "!");
                return;
            }

            if (backupFile.exists()) backupFile.delete();
            if (!file.renameTo(backupFile)) {
                plugin.getLogger().warning("Backup creation failed for " + fileName + "!");
                return;
            }

            FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(backupFile);
            InputStreamReader reader = new InputStreamReader(plugin.getResource(fileName), "UTF-8");
            YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(reader);

            for (String key : newConfig.getKeys(true)) {
                if (oldConfig.contains(key)) {
                    Object oldValue = oldConfig.get(key);
                    Object newDefault = newConfig.get(key);

                    if (oldValue != null && !oldValue.equals(newDefault)) {
                        newConfig.set(key, oldValue);
                    }
                }
            }

            newConfig.save(file);

            plugin.getLogger().info(fileName + " updated successfully. Backup saved as " + fileName + ".old.");

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to update " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
