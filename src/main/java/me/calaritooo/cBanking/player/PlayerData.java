package me.calaritooo.cBanking.player;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {

    private final cBanking plugin;
    private final ConfigurationProvider config;
    private final File playerDataFolder;

    public PlayerData() {
        this.plugin = cBankingCore.getPlugin();
        this.config = cBankingCore.getConfigurationProvider();
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
                FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);
                playerFile.set("username", plugin.getServer().getOfflinePlayer(uuid).getName());
                playerFile.set("balance", config.getDouble(ConfigurationOption.ECONOMY_STARTING_BALANCE));
                playerFile.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data file for: " + plugin.getServer().getOfflinePlayer(uuid).getName() + ", UUID: " + uuid);
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public void savePlayerConfig(UUID uuid, FileConfiguration playerFile) {
        try {
            playerFile.save(getPlayerFile(uuid));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data file for: " + plugin.getServer().getOfflinePlayer(uuid).getName() + ", UUID: " + uuid);
        }
    }

    public void savePlayerData(UUID uuid, double initialBalance) {
        FileConfiguration playerFile = getPlayerConfig(uuid);
        playerFile.set("balance", initialBalance);
        playerFile.set("username", plugin.getServer().getOfflinePlayer(uuid).getName());
        savePlayerConfig(uuid, playerFile);
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

    public String getName(UUID uuid) {
        FileConfiguration config = getPlayerConfig(uuid);
        return config.getString("username", "Unknown");
    }
}
