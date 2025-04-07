package me.calaritooo.cBanking.bank;

import me.calaritooo.cBanking.cBanking;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class BankAccount {

    private final cBanking plugin;

    public BankAccount(cBanking plugin) {
        this.plugin = plugin;
    }

    private File getBankFile(String bankID) {
        return new File(plugin.getDataFolder(), "banks/" + bankID + ".yml");
    }

    private FileConfiguration loadBankConfig(String bankID) {
        return YamlConfiguration.loadConfiguration(getBankFile(bankID));
    }

    private void saveBankConfig(String bankID, FileConfiguration config) {
        try {
            config.save(getBankFile(bankID));
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save bank file: " + bankID);
        }
    }

    public boolean hasAccount(String bankID, UUID uuid) {
        return loadBankConfig(bankID).contains("accounts." + uuid.toString());
    }

    public double getBalance(String bankID, UUID uuid) {
        return loadBankConfig(bankID).getDouble("accounts." + uuid.toString() + ".balance", 0.0);
    }

    public void setBalance(String bankID, UUID uuid, double amount) {
        FileConfiguration config = loadBankConfig(bankID);
        config.set("accounts." + uuid.toString() + ".balance", amount);
        saveBankConfig(bankID, config);
    }

    public void deposit(String bankID, UUID uuid, double amount) {
        double current = getBalance(bankID, uuid);
        setBalance(bankID, uuid, current + amount);
    }

    public void withdraw(String bankID, UUID uuid, double amount) {
        double current = getBalance(bankID, uuid);
        if (current >= amount) {
            setBalance(bankID, uuid, current - amount);
        }
    }

    public void createAccount(String bankID, UUID uuid, double initialBalance) {
        if (!hasAccount(bankID, uuid)) {
            setBalance(bankID, uuid, initialBalance);
        }
    }

    public void deleteAccount(String bankID, UUID uuid) {
        FileConfiguration config = loadBankConfig(bankID);
        config.set("accounts." + uuid.toString(), null);
        saveBankConfig(bankID, config);
    }
}
