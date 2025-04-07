package me.calaritooo.cBanking.player;

import me.calaritooo.cBanking.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {

    private final cBanking plugin;
    private final File playerDataFolder;

    public PlayerData(cBanking plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "players");

        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    public File getPlayerFile(UUID uuid) {
        return new File(playerDataFolder, uuid.toString() + ".yml");
    }

    public FileConfiguration getPlayerConfig(UUID uuid) {
        File file = getPlayerFile(uuid);

        if (!file.exists()) {
            try {
                file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("balance", 0.0);
                config.set("username", plugin.getServer().getOfflinePlayer(uuid).getName());
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data file for " + uuid);
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public void savePlayerConfig(UUID uuid, FileConfiguration config) {
        try {
            config.save(getPlayerFile(uuid));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data file for " + uuid);
        }
    }

    public void savePlayerData(UUID uuid, double initialBalance) {
        FileConfiguration config = getPlayerConfig(uuid);
        config.set("balance", initialBalance);
        config.set("username", plugin.getServer().getOfflinePlayer(uuid).getName());
        savePlayerConfig(uuid, config);
    }

    public void deletePlayerData(String uuidStr) {
        File file = getPlayerFile(UUID.fromString(uuidStr));
        if (file.exists()) {
            file.delete();
        }
    }

    public boolean hasPlayerData(UUID uuid) {
        return getPlayerFile(uuid).exists();
    }

    public double getBalance(UUID uuid) {
        FileConfiguration config = getPlayerConfig(uuid);
        return config.getDouble("balance", 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        FileConfiguration config = getPlayerConfig(uuid);
        config.set("balance", amount);
        savePlayerConfig(uuid, config);
    }

    public String getUsername(UUID uuid) {
        FileConfiguration config = getPlayerConfig(uuid);
        return config.getString("username", "Unknown");
    }
}
