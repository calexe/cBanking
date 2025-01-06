package me.github.calaritooo.data;

import me.github.calaritooo.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BankDataHandler {
    private final cBanking plugin;
    private FileConfiguration banksConfig = null;
    private File banksConfigFile = null;

    public BankDataHandler(cBanking plugin) {
        this.plugin = plugin;
        saveDefaultBanksConfig();
    }

    public void reloadBanksConfig() {
        if (banksConfigFile == null) {
            banksConfigFile = new File(plugin.getDataFolder(), "banks.yml");
        }
        banksConfig = YamlConfiguration.loadConfiguration(banksConfigFile);
    }

    public FileConfiguration getBanksConfig() {
        if (banksConfig == null) {
            reloadBanksConfig();
        }
        return banksConfig;
    }

    public void saveBanksConfig() {
        if (banksConfig == null || banksConfigFile == null) {
            return;
        }
        try {
            getBanksConfig().save(banksConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + banksConfigFile, ex);
        }
    }

    public void saveDefaultBanksConfig() {
        if (banksConfigFile == null) {
            banksConfigFile = new File(plugin.getDataFolder(), "banks.yml");
        }
        if (!banksConfigFile.exists()) {
            plugin.saveResource("banks.yml", false);
        }
    }

    public void saveBankData(String bankID, String bankName, String ownerName, double assets, double interestRate, double accountGrowthRate, double accountOpeningFee, double maintenanceFeeRate, double depositFeeRate, double withdrawalFeeRate) {
        getBanksConfig().set("banks." + bankID + ".bankName", bankName);
        getBanksConfig().set("banks." + bankID + ".ownerName", ownerName);
        getBanksConfig().set("banks." + bankID + ".assets", assets);
        getBanksConfig().set("banks." + bankID + ".interestRate", interestRate);
        getBanksConfig().set("banks." + bankID + ".accountGrowthRate", accountGrowthRate);
        getBanksConfig().set("banks." + bankID + ".accountOpeningFee", accountOpeningFee);
        getBanksConfig().set("banks." + bankID + ".maintenanceFeeRate", maintenanceFeeRate);
        getBanksConfig().set("banks." + bankID + ".depositFeeRate", depositFeeRate);
        getBanksConfig().set("banks." + bankID + ".withdrawalFeeRate", withdrawalFeeRate);
        saveBanksConfig();
    }

    public void deleteBankData(String bankID) {
        getBanksConfig().set("banks." + bankID, null);
        saveBanksConfig();
    }
}