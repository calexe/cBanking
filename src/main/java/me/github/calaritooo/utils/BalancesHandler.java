package me.github.calaritooo.utils;

import me.github.calaritooo.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class BalancesHandler {
    private final File balancesFile;
    private final FileConfiguration balancesConfig;
    private final cBanking plugin = cBanking.getInstance();
    private final Double startingBal = plugin.getConfig().getDouble("economy-settings.starting-bal");

    public BalancesHandler(File dataFolder) {
        this.balancesFile = new File(dataFolder, "balances.yml");
        if (!balancesFile.exists()) {
            try {
                dataFolder.mkdirs();
                balancesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not create balances.yml!");
            }
        }
        this.balancesConfig = YamlConfiguration.loadConfiguration(balancesFile);

    }

    // Get the balance of a player
    public double getBalance(UUID playerUUID) {
        return balancesConfig.getDouble("balances." + playerUUID, startingBal);
    }

    public String getPlayerName(UUID playerUUID) {
        return balancesConfig.getString("balances." + playerUUID + ".name", "Unknown");
    }

    // Set the balance of a player
    public void setBalance(UUID playerUUID, String playerName, double amount) {
        balancesConfig.set("balances." + playerUUID + ".name", playerName);
        BigDecimal roundedBalance = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
        balancesConfig.set(playerUUID.toString() + ".balance", roundedBalance.doubleValue());
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
            e.printStackTrace();
            throw new RuntimeException("Failed to save balances.yml!");
        }
    }
}
