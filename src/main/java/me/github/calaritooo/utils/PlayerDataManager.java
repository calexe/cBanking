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
    private FileConfiguration playerDataConfig;
    private File playerDataFile;

    public PlayerDataManager(cBanking plugin) {
        this.plugin = plugin;
        createPlayerDataFile();
    }

    private void createPlayerDataFile() {
        playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            playerDataFile.getParentFile().mkdirs();
            plugin.saveResource("playerdata.yml", false);
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void setBalance(OfflinePlayer player, double balance) {
        playerDataConfig.set(player.getUniqueId().toString(), balance);
        savePlayerData();
    }

    private void savePlayerData() {
        try {
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateBalanceFromVault(OfflinePlayer player) {
        double balance = VaultHook.getBalance(player);
        setBalance(player, balance);
    }
}