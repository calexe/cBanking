package me.calaritooo.cBanking.player;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final cBanking plugin;
    private final ConfigurationProvider config;
    private final File playersFolder;

    private final Map<UUID, FileConfiguration> playerCache = new HashMap<>();

    public PlayerData() {
        this.plugin = cBankingCore.getPlugin();
        this.config = cBankingCore.getConfigurationProvider();
        this.playersFolder = new File(plugin.getDataFolder(), "players");
        if (!playersFolder.exists()) playersFolder.mkdirs();
    }

    private File getPlayerFile(UUID uuid) {
        return new File(playersFolder, uuid.toString() + ".yml");
    }

    private void createPlayerFile(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.set("username", plugin.getServer().getOfflinePlayer(uuid).getName());
                config.set("balance", this.config.get(ConfigurationOption.ECONOMY_STARTING_BALANCE));
                config.save(file);
                plugin.getLogger().info("Created data file for player: " + uuid);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create player file for: " + uuid);
            }
        }
    }

    public FileConfiguration getPlayerConfig(UUID uuid) {
        if (!playerCache.containsKey(uuid)) {
            createPlayerFile(uuid);
            FileConfiguration config = YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
            playerCache.put(uuid, config);
        }
        return playerCache.get(uuid);
    }

    public void savePlayerConfig(UUID uuid) {
        File file = getPlayerFile(uuid);
        FileConfiguration config = playerCache.get(uuid);
        if (config != null) {
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save player data for: " + uuid);
            }
        }
    }

    public void saveAll() {
        for (UUID uuid : playerCache.keySet()) {
            savePlayerConfig(uuid);
        }
    }

    // Player data operations

    public void savePlayerData(UUID uuid, double balance) {
        FileConfiguration config = getPlayerConfig(uuid);
        config.set("balance", balance);
        config.set("username", plugin.getServer().getOfflinePlayer(uuid).getName());
        savePlayerConfig(uuid);
    }

    public boolean hasPlayerData(UUID uuid) {
        return getPlayerFile(uuid).exists();
    }

    public double getBalance(UUID uuid) {
        return getPlayerConfig(uuid).getDouble("balance", 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        getPlayerConfig(uuid).set("balance", amount);
        savePlayerConfig(uuid);
    }

    public String getName(UUID uuid) {
        return getPlayerConfig(uuid).getString("username", "Unknown");
    }

    public void deletePlayerData(String uuidStr) {
        UUID uuid = UUID.fromString(uuidStr);
        playerCache.remove(uuid); // Remove from memory too
        File file = getPlayerFile(uuid);
        if (file.exists()) {
            file.delete();
        }
    }
}
