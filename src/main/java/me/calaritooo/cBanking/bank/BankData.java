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
import java.util.*;

public class BankData {

    private final cBanking plugin;
    private final ConfigurationProvider configProvider;
    private final File banksFolder;

    private final Map<String, FileConfiguration> bankCache = new HashMap<>();

    public BankData() {
        this.plugin = cBankingCore.getPlugin();
        this.configProvider = cBankingCore.getConfigurationProvider();
        this.banksFolder = new File(plugin.getDataFolder(), "banks");
        if (!banksFolder.exists()) banksFolder.mkdirs();
    }

    protected File getBankFolder(String bankID) {
        return new File(banksFolder, bankID);
    }

    private File getSettingsFile(String bankID) {
        return new File(getBankFolder(bankID), bankID + ".yml");
    }

    public FileConfiguration getBankConfig(String bankID) {
        if (!bankCache.containsKey(bankID)) {
            File file = getSettingsFile(bankID);
            if (!file.exists()) {
                plugin.getLogger().warning("Tried to load missing bank: " + bankID);
                return null;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            bankCache.put(bankID, config);
        }
        return bankCache.get(bankID);
    }

    public void saveBankConfig(String bankID) {
        FileConfiguration config = bankCache.get(bankID);
        if (config != null) {
            try {
                config.save(getSettingsFile(bankID));
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save configuration file for bank '" + bankID + "'");
                e.printStackTrace();
            }
        }
    }

    public void saveAll() {
        for (String bankID : bankCache.keySet()) {
            saveBankConfig(bankID);
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
            if (config != null && name.equalsIgnoreCase(config.getString("bankName"))) {
                return id;
            }
        }
        return null;
    }

    public void createBank(String bankID, String bankName, UUID ownerUUID) {
        if (bankExists(bankID)) {
            plugin.getLogger().warning("Bank already exists: " + bankID);
            return;
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
        FileConfiguration config = new YamlConfiguration();

        config.set("bank-name", bankName);
        config.set("owner-uuid", ownerUUID.toString());
        config.set("owner-name", owner.getName());
        config.set("assets", configProvider.get(ConfigurationOption.BANK_CREATION_ASSETS));
        config.set("interest-rate", configProvider.get(ConfigurationOption.LOAN_DEFAULT_INTEREST_RATE));
        config.set("account-growth-rate", configProvider.get(ConfigurationOption.BANK_DEFAULT_ACCOUNT_GROWTH));
        config.set("account-opening-fee", configProvider.get(ConfigurationOption.BANK_DEFAULT_ACCOUNT_OPENING_FEE));
        config.set("maintenance-fee-rate", configProvider.get(ConfigurationOption.BANK_DEFAULT_MAINTENANCE_FEE));
        config.set("deposit-fee-rate", configProvider.get(ConfigurationOption.BANK_DEFAULT_DEPOSIT_FEE));
        config.set("withdrawal-fee-rate", configProvider.get(ConfigurationOption.BANK_DEFAULT_WITHDRAWAL_FEE));

        bankCache.put(bankID, config);
        saveBankConfig(bankID);

        plugin.getLogger().info("Created new bank: " + bankID);
    }

    public void deleteBank(String bankID) {
        bankCache.remove(bankID);

        File bankFolder = getBankFolder(bankID);
        if (!bankFolder.exists()) return;

        File[] files = bankFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        bankFolder.delete();
    }
}
