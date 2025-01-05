package me.github.calaritooo.data;

import me.github.calaritooo.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PlayerDataHandler {
    private final cBanking plugin;
    private FileConfiguration playerDataConfig = null;
    private File playerDataConfigFile = null;

    public PlayerDataHandler(cBanking plugin) {
        this.plugin = plugin;
        saveDefaultPlayerDataConfig();
    }

    public void reloadPlayerDataConfig() {
        if (playerDataConfigFile == null) {
            playerDataConfigFile = new File(plugin.getDataFolder(), "playerdata.yml");
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataConfigFile);
    }

    public FileConfiguration getPlayerDataConfig() {
        if (playerDataConfig == null) {
            reloadPlayerDataConfig();
        }
        return playerDataConfig;
    }

    public void savePlayerDataConfig() {
        if (playerDataConfig == null || playerDataConfigFile == null) {
            return;
        }
        try {
            getPlayerDataConfig().save(playerDataConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + playerDataConfigFile, ex);
        }
    }

    public void saveDefaultPlayerDataConfig() {
        if (playerDataConfigFile == null) {
            playerDataConfigFile = new File(plugin.getDataFolder(), "playerdata.yml");
        }
        if (!playerDataConfigFile.exists()) {
            plugin.saveResource("playerdata.yml", false);
        }
    }

    public void savePlayerData(String playerName, String playerUUID, double balance) {
        getPlayerDataConfig().set("players." + playerUUID + ".playerName", playerName);
        getPlayerDataConfig().set("players." + playerUUID + ".UUID", playerUUID);
        getPlayerDataConfig().set("players." + playerUUID + ".balance", balance);
        getPlayerDataConfig().set("players." + playerUUID + ".accounts", new YamlConfiguration());
        savePlayerDataConfig();
    }

    public void savePlayerData(String playerName, String playerUUID, double balance, String bankID, long creationTime, double accountBalance) {
        getPlayerDataConfig().set("players." + playerUUID + ".playerName", playerName);
        getPlayerDataConfig().set("players." + playerUUID + ".UUID", playerUUID);
        getPlayerDataConfig().set("players." + playerUUID + ".balance", balance);
        getPlayerDataConfig().set("players." + playerUUID + ".accounts." + bankID + ".creationTime", creationTime);
        getPlayerDataConfig().set("players." + playerUUID + ".accounts." + bankID + ".balance", accountBalance);
        savePlayerDataConfig();
    }

    public void deletePlayerData(String playerID) {
        getPlayerDataConfig().set("players." + playerID, null);
        savePlayerDataConfig();
    }

    public FileConfiguration loadPlayerData(String playerID) {
        return (FileConfiguration) getPlayerDataConfig().getConfigurationSection("players." + playerID);
    }
}