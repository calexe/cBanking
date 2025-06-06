package me.calaritooo.cBanking.bank;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.util.money.Money;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BankAccount {

    private final cBanking plugin;
    private final BankData bankData;

    public BankAccount(BankData bankData) {
        this.plugin = cBankingCore.getPlugin();
        this.bankData = bankData;
    }

    public File getAccountFolder(String bankID) {
        File folder = new File(bankData.getBankFolder(bankID), "accounts");
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    public File getAccountFile(String bankID, UUID playerUUID) {
        return new File(getAccountFolder(bankID), playerUUID.toString() + ".yml");
    }

    public FileConfiguration getAccountConfig(String bankID, UUID playerUUID) {
        return YamlConfiguration.loadConfiguration(getAccountFile(bankID, playerUUID));
    }

    public void saveAccountConfig(String bankID, UUID playerUUID, FileConfiguration config) {
        try {
            config.save(getAccountFile(bankID, playerUUID));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save account for " + playerUUID + " in bank " + bankID);
            e.printStackTrace();
        }
    }

    public boolean hasAccount(String bankID, UUID playerUUID) {
        return getAccountFile(bankID, playerUUID).exists();
    }

    public Money getBalance(String bankID, UUID playerUUID) {
        double balance = getAccountConfig(bankID, playerUUID).getDouble("balance", 0.0);
        return Money.of(balance);
    }

    public void setBalance(String bankID, UUID playerUUID, Money amount) {
        FileConfiguration config = getAccountConfig(bankID, playerUUID);
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);

        config.set("balance", amount.value());
        config.set("uuid", playerUUID.toString());
        config.set("username", player.getName());

        saveAccountConfig(bankID, playerUUID, config);
    }

    public void deposit(String bankID, UUID playerUUID, Money amount) {
        Money current = getBalance(bankID, playerUUID);
        setBalance(bankID, playerUUID, current.add(amount));
    }

    public void withdraw(String bankID, UUID playerUUID, Money amount) {
        Money current = getBalance(bankID, playerUUID);
        setBalance(bankID, playerUUID, current.subtract(amount));
    }

    public void createAccount(String bankID, UUID playerUUID, Money initialBalance) {
        if (hasAccount(bankID, playerUUID)) return;
        setBalance(bankID, playerUUID, initialBalance);
    }

    public void deleteAccount(String bankID, UUID playerUUID) {
        File file = getAccountFile(bankID, playerUUID);
        if (file.exists()) file.delete();
    }

    public Set<UUID> getAllAccountUUIDs(String bankID) {
        File[] files = getAccountFolder(bankID).listFiles((dir, name) -> name.endsWith(".yml"));
        Set<UUID> uuids = new HashSet<>();
        if (files != null) {
            for (File file : files) {
                try {
                    uuids.add(UUID.fromString(file.getName().replace(".yml", "")));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return uuids;
    }

    public Set<String> getBankIDsByPlayer(UUID playerUUID) {
        Set<String> bankIDs = new HashSet<>();
        for (String bankID : bankData.getBankIDs()) {
            if (hasAccount(bankID, playerUUID)) {
                bankIDs.add(bankID);
            }
        }
        return bankIDs;
    }

    public Map<UUID, Money> getAllBalances(String bankID) {
        Map<UUID, Money> balances = new HashMap<>();
        for (UUID uuid : getAllAccountUUIDs(bankID)) {
            balances.put(uuid, getBalance(bankID, uuid));
        }
        return balances;
    }

    public void deleteAllAccounts(String bankID) {
        File folder = getAccountFolder(bankID);
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            file.delete();
        }
    }
}
