package me.calaritooo.cBanking.bank;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BankData {

    private final cBanking plugin;
    private final ConfigurationProvider configProvider;
    private final File banksFolder;

    public BankData() {
        this.plugin = cBankingCore.getPlugin();
        this.configProvider = cBankingCore.getConfigurationProvider();
        this.banksFolder = new File(plugin.getDataFolder(), "banks");
        if (!banksFolder.exists()) banksFolder.mkdirs();
    }

    public File getBankFolder(String bankID) {
        File folder = new File(banksFolder, bankID);
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    public File getSettingsFile(String bankID) {
        return new File(getBankFolder(bankID), bankID + ".yml");
    }

    public FileConfiguration getBankConfig(String bankID) {
        return YamlConfiguration.loadConfiguration(getSettingsFile(bankID));
    }

    public void saveBankConfig(String bankID, FileConfiguration config) {
        try {
            config.save(getSettingsFile(bankID));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save configuration file for bank '" + bankID + "'");
            e.printStackTrace();
        }
    }

    public Set<String> getBankIDs() {
        File[] files = banksFolder.listFiles();
        Set<String> ids = new HashSet<>();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) ids.add(file.getName());
            }
        }
        return ids;
    }

    public boolean bankExists(String bankID) {
        return getSettingsFile(bankID).exists();
    }

    public boolean bankNameExists(String name) {
        return getBankIDByName(name) != null;
    }

    public String getBankIDByName(String name) {
        for (String id : getBankIDs()) {
            FileConfiguration config = getBankConfig(id);
            if (name.equalsIgnoreCase(config.getString("bankName"))) return id;
        }
        return null;
    }

    public void createBank(String bankID, String bankName, UUID ownerUUID) {
        FileConfiguration config = new YamlConfiguration();
        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);

        config.set("bankName", bankName);
        config.set("ownerUUID", ownerUUID.toString());
        config.set("ownerName", owner.getName());
        config.set("assets", configProvider.getDouble(ConfigurationOption.BANK_CREATION_ASSETS));
        config.set("interestRate", configProvider.getDouble(ConfigurationOption.LOAN_DEFAULT_INTEREST_RATE));
        config.set("accountGrowthRate", configProvider.getDouble(ConfigurationOption.BANK_DEFAULT_ACCOUNT_GROWTH));
        config.set("accountOpeningFee", configProvider.getDouble(ConfigurationOption.BANK_DEFAULT_ACCOUNT_OPENING_FEE));
        config.set("maintenanceFeeRate", configProvider.getDouble(ConfigurationOption.BANK_DEFAULT_MAINTENANCE_FEE));
        config.set("depositFeeRate", configProvider.getDouble(ConfigurationOption.BANK_DEFAULT_DEPOSIT_FEE));
        config.set("withdrawalFeeRate", configProvider.getDouble(ConfigurationOption.BANK_DEFAULT_WITHDRAWAL_FEE));

        saveBankConfig(bankID, config);
    }

    public void deleteBank(String bankID) {
        File bankFolder = getBankFolder(bankID);
        if (!bankFolder.exists()) return;

        for (File file : Objects.requireNonNull(bankFolder.listFiles())) {
            file.delete();
        }
        bankFolder.delete();
    }
}