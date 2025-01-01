package me.github.calaritooo.utils;

import me.github.calaritooo.cBanking;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import me.github.calaritooo.VaultHook;

import java.io.File;
import java.io.IOException;

public class PlayerDataManager {

    private final cBanking plugin;
    private FileConfiguration playerDataConfig = null;
    private File playerDataFile = null;

    public PlayerDataManager(cBanking plugin) {
        this.plugin = plugin;
        saveDefaultPlayerData();
    }

    public void reloadPlayerData() {
        if (playerDataFile == null) {
            playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public FileConfiguration getPlayerData() {
        if (playerDataConfig == null) {
            reloadPlayerData();
        }
        return playerDataConfig;
    }

    public void setBalance(OfflinePlayer player, double balance) {
        playerDataConfig.set(player.getUniqueId().toString(), balance);
        savePlayerData();
    }

    public void savePlayerData() {
        if (playerDataConfig == null || playerDataFile == null) {
            return;
        }
        try {
            getPlayerData().save(playerDataFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save playerdata.yml!");
        }
    }

    public void saveDefaultPlayerData() {
        if (playerDataFile == null) {
            playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        }
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            plugin.saveResource("playerdata.yml", false);
        }
    }

    public void updateBalanceFromVault(OfflinePlayer player) {
        double balance = VaultHook.getBalance(player);
        setBalance(player, balance);
    }
}