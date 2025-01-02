package me.github.calaritooo.utils;

import me.github.calaritooo.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.logging.Level;

public class BalancesHandler {
    private final File balancesFile;
    private FileConfiguration balancesConfig;
    private final cBanking plugin;
    private final double startingBal;

    public BalancesHandler(cBanking plugin) {
        this.plugin = plugin;
        this.startingBal = plugin.getConfig().getDouble("economy-settings.starting-bal", 0.0);

        File dataFolder = plugin.getDataFolder();
        this.balancesFile = new File(dataFolder, "balances.yml");

        if (!balancesFile.exists()) {
            try {
                dataFolder.mkdirs(); // Ensure data folder exists
                balancesFile.createNewFile(); // Create balances.yml if it doesn't exist
                plugin.getLogger().info("balances.yml created successfully.");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create balances.yml!", e);
                throw new RuntimeException("Could not create balances.yml!");
            }
        }
        loadBalancesConfig();
    }

    // Reloads the balances.yml configuration from disk
    public void loadBalancesConfig() {
        this.balancesConfig = YamlConfiguration.loadConfiguration(balancesFile);
    }

    // Get the balance of a player
    public double getBalance(UUID playerUUID) {
        return balancesConfig.getDouble("balances." + playerUUID + ".balance", startingBal);
    }

    // Get the player's name stored in balances.yml
    public String getPlayerName(UUID playerUUID) {
        return balancesConfig.getString("balances." + playerUUID + ".name", "Unknown");
    }

    // Set the balance of a player
    public void setBalance(UUID playerUUID, String playerName, double amount) {
        balancesConfig.set("balances." + playerUUID + ".name", playerName);
        BigDecimal roundedBalance = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
        balancesConfig.set("balances." + playerUUID + ".balance", roundedBalance.doubleValue());
        saveBalancesFile();
    }

    // Check if a player has a balance entry
    public boolean hasBalance(UUID playerUUID) {
        return balancesConfig.contains("balances." + playerUUID);
    }

    // Save balances.yml to disk
    public void saveBalancesFile() {
        try {
            balancesConfig.save(balancesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save balances.yml!", e);
            throw new RuntimeException("Failed to save balances.yml!");
        }
    }
}
