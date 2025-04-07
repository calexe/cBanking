package me.calaritooo.cBanking.bank;

import me.calaritooo.cBanking.cBanking;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BankData {

    private final cBanking plugin;
    private final File file;
    private final FileConfiguration config;

    public BankData(cBanking plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "banks.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save banks.yml!");
            e.printStackTrace();
        }
    }

    public boolean bankExists(String bankID) {
        return config.contains("banks." + bankID);
    }

    public void createBank(String bankID, String bankName, String ownerName) {
        config.set("banks." + bankID + ".bankName", bankName);
        config.set("banks." + bankID + ".ownerName", ownerName);
        config.set("banks." + bankID + ".assets", plugin.getConfig().getDouble("bank-settings.new-bank-assets"));
        config.set("banks." + bankID + ".interestRate", plugin.getConfig().getDouble("loan-settings.default-interest-rate"));
        config.set("banks." + bankID + ".accountGrowthRate", plugin.getConfig().getDouble("bank-settings.default-acct-growth-rate"));
        config.set("banks." + bankID + ".accountOpeningFee", plugin.getConfig().getDouble("bank-settings.default-acct-opening-fee"));
        config.set("banks." + bankID + ".maintenanceFeeRate", plugin.getConfig().getDouble("bank-settings.default-maintenance-fee"));
        config.set("banks." + bankID + ".depositFeeRate", plugin.getConfig().getDouble("bank-settings.default-deposit-fee-rate"));
        config.set("banks." + bankID + ".withdrawalFeeRate", plugin.getConfig().getDouble("bank-settings.default-withdrawal-fee-rate"));
        save();
    }

    public void deleteBank(String bankID) {
        config.set("banks." + bankID, null);
        save();
    }

    public Map<String, Double> deleteBankAndTransferBalances(String bankID) {
        Map<String, Double> transferred = new HashMap<>();
        ConfigurationSection accounts = config.getConfigurationSection("banks." + bankID + ".accounts");
        if (accounts != null) {
            for (String uuid : accounts.getKeys(false)) {
                double balance = accounts.getDouble(uuid + ".balance", 0.0);
                transferred.put(uuid, balance);
            }
        }
        deleteBank(bankID);
        return transferred;
    }

    public Set<String> getBankIDs() {
        ConfigurationSection section = config.getConfigurationSection("banks");
        return section == null ? Collections.emptySet() : section.getKeys(false);
    }

    public String getBankIDByName(String name) {
        for (String bankID : getBankIDs()) {
            if (name.equalsIgnoreCase(config.getString("banks." + bankID + ".bankName"))) {
                return bankID;
            }
        }
        return null;
    }

    public String getBankIDByOwner(String ownerName) {
        for (String bankID : getBankIDs()) {
            if (ownerName.equalsIgnoreCase(config.getString("banks." + bankID + ".ownerName"))) {
                return bankID;
            }
        }
        return null;
    }

    public boolean bankNameExists(String bankName) {
        return getBankIDByName(bankName) != null;
    }

    public String getBankNameByID(String bankID) {
        return config.getString("banks." + bankID + ".bankName");
    }

    public String getBankOwnerByID(String bankID) {
        return config.getString("banks." + bankID + ".ownerName");
    }

    public void setBankName(String bankID, String name) {
        config.set("banks." + bankID + ".bankName", name);
        save();
    }

    public void setBankOwner(String bankID, String owner) {
        config.set("banks." + bankID + ".ownerName", owner);
        save();
    }

    public double getAssets(String bankID) {
        return config.getDouble("banks." + bankID + ".assets");
    }

    public void setAssets(String bankID, double value) {
        config.set("banks." + bankID + ".assets", value);
        save();
    }

    public double getInterestRate(String bankID) {
        return config.getDouble("banks." + bankID + ".interestRate");
    }

    public void setInterestRate(String bankID, double value) {
        config.set("banks." + bankID + ".interestRate", value);
        save();
    }

    public double getAccountGrowthRate(String bankID) {
        return config.getDouble("banks." + bankID + ".accountGrowthRate");
    }

    public void setAccountGrowthRate(String bankID, double value) {
        config.set("banks." + bankID + ".accountGrowthRate", value);
        save();
    }

    public double getAccountOpeningFee(String bankID) {
        return config.getDouble("banks." + bankID + ".accountOpeningFee");
    }

    public void setAccountOpeningFee(String bankID, double value) {
        config.set("banks." + bankID + ".accountOpeningFee", value);
        save();
    }

    public double getMaintenanceFeeRate(String bankID) {
        return config.getDouble("banks." + bankID + ".maintenanceFeeRate");
    }

    public void setMaintenanceFeeRate(String bankID, double value) {
        config.set("banks." + bankID + ".maintenanceFeeRate", value);
        save();
    }

    public double getDepositFeeRate(String bankID) {
        return config.getDouble("banks." + bankID + ".depositFeeRate");
    }

    public void setDepositFeeRate(String bankID, double value) {
        config.set("banks." + bankID + ".depositFeeRate", value);
        save();
    }

    public double getWithdrawalFeeRate(String bankID) {
        return config.getDouble("banks." + bankID + ".withdrawalFeeRate");
    }

    public void setWithdrawalFeeRate(String bankID, double value) {
        config.set("banks." + bankID + ".withdrawalFeeRate", value);
        save();
    }
}

