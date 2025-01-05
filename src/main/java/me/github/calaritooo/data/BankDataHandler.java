package me.github.calaritooo.data;

import me.github.calaritooo.banks.BankHandler;
import me.github.calaritooo.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    public void saveAllBanks(Map<String, BankHandler> banks) {
        for (Map.Entry<String, BankHandler> entry : banks.entrySet()) {
            String bankID = entry.getKey();
            BankHandler bank = entry.getValue();
            saveBankData(bankID, bank);
        }
        saveBanksConfig();
    }

    public Map<String, BankHandler> loadAllBanks() {
        Map<String, BankHandler> banks = new HashMap<>();
        FileConfiguration config = getBanksConfig();
        if (config.contains("banks")) {
            for (String bankID : config.getConfigurationSection("banks").getKeys(false)) {
                BankHandler bank = loadBankData(bankID);
                banks.put(bankID, bank);
            }
        }
        return banks;
    }

    public void saveBankData(String bankID, BankHandler bank) {
        getBanksConfig().set("banks." + bankID + ".bankName", bank.getBankName());
        getBanksConfig().set("banks." + bankID + ".ownerName", bank.getOwnerName());
        getBanksConfig().set("banks." + bankID + ".assets", bank.getAssets());
        getBanksConfig().set("banks." + bankID + ".interestRate", bank.getInterestRate());
        getBanksConfig().set("banks." + bankID + ".accountGrowthRate", bank.getAccountGrowthRate());
        getBanksConfig().set("banks." + bankID + ".maintenanceFeeRate", bank.getMaintenanceFeeRate());
        getBanksConfig().set("banks." + bankID + ".depositFeeRate", bank.getDepositFeeRate());
        getBanksConfig().set("banks." + bankID + ".withdrawalFeeRate", bank.getWithdrawalFeeRate());
        saveBanksConfig();
    }

    public BankHandler loadBankData(String bankID) {
        String bankName = getBanksConfig().getString("banks." + bankID + ".bankName");
        String ownerName = getBanksConfig().getString("banks." + bankID + ".ownerName");
        double assets = getBanksConfig().getDouble("banks." + bankID + ".assets");
        double interestRate = getBanksConfig().getDouble("banks." + bankID + ".interestRate");
        double accountGrowthRate = getBanksConfig().getDouble("banks." + bankID + ".accountGrowthRate");
        double maintenanceFeeRate = getBanksConfig().getDouble("banks." + bankID + ".maintenanceFeeRate");
        double depositFeeRate = getBanksConfig().getDouble("banks." + bankID + ".depositFeeRate");
        double withdrawalFeeRate = getBanksConfig().getDouble("banks." + bankID + ".withdrawalFeeRate");

        BankHandler bank = new BankHandler(plugin, bankID, bankName, ownerName);
        bank.setAssets(assets);
        bank.setInterestRate(interestRate);
        bank.setAccountGrowthRate(accountGrowthRate);
        bank.setMaintenanceFeeRate(maintenanceFeeRate);
        bank.setDepositFeeRate(depositFeeRate);
        bank.setWithdrawalFeeRate(withdrawalFeeRate);

        return bank;
    }

    public void deleteBankData(String bankID) {
        getBanksConfig().set("banks." + bankID, null);
        saveBanksConfig();
    }
}