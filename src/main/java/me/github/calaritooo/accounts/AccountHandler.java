package me.github.calaritooo.accounts;

import me.github.calaritooo.cBanking;
import me.github.calaritooo.data.BankDataHandler;
import me.github.calaritooo.data.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class AccountHandler implements AccountInterface {

    private final cBanking plugin;
    private PlayerDataHandler playerDataHandler;
    private BankDataHandler bankDataHandler;

    public AccountHandler(cBanking plugin) {
        this.plugin = plugin;
    }

    public void initializeHandlers(PlayerDataHandler playerDataHandler, BankDataHandler bankDataHandler) {
        this.playerDataHandler = plugin.getPlayerDataHandler();
        this.bankDataHandler = plugin.getBankDataHandler();
    }

    @Override
    public void createAccount(String playerName, String playerID, double initialBalance) {
        playerID = getPlayerUUID(playerName).toString();
        if (playerID != null) {
            playerDataHandler.savePlayerData(playerName, playerID, initialBalance);
        }
    }

    @Override
    public void createAccount(String playerName, String playerID, String bankID, double initialBalance) {
        playerID = getPlayerUUID(playerName).toString();
        double playerBalance = getBalance(playerName);
        if (playerID != null) {
            long creationTime = System.currentTimeMillis();
            playerDataHandler.savePlayerNewBankAccountData(playerName, playerID, playerBalance, bankID, creationTime, initialBalance);
        }
    }

    @Override
    public void deleteAccount(String playerName) {
        String playerUUID = getPlayerUUID(playerName).toString();
        if (playerUUID != null) {
            playerDataHandler.deletePlayerData(playerUUID);
        }
    }

    @Override
    public void deleteAccount(String playerName, String bankID) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        playerDataHandler.getPlayerDataConfig().set("players." + playerID + ".accounts." + bankID, null);
        playerDataHandler.savePlayerDataConfig();
    }

    @Override
    public boolean hasAccount(String playerName) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        return playerDataHandler.getPlayerDataConfig().getConfigurationSection("players." + playerID) != null;
    }

    @Override
    public boolean hasAccount(String playerName, String bankID) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        return playerDataHandler.getPlayerDataConfig().getConfigurationSection("players." + playerID + ".accounts." + bankID) != null;
    }

    @Override
    public boolean hasFunds(String playerName, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        double balance = playerDataHandler.getPlayerDataConfig().getDouble("players." + playerUUID + ".balance");
        return balance >= amount;
    }

    @Override
    public boolean hasFunds(String playerName, String bankID, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        double balance = playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".accounts." + bankID + ".balance");
        return balance >= amount;
    }

    @Override
    public double getBalance(String playerName) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        playerDataHandler.reloadPlayerDataConfig();
        return playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".balance");
    }

    @Override
    public double getBalance(String playerName, String bankID) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        bankDataHandler.reloadBanksConfig();
        return playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".accounts." + bankID + ".balance");
    }

    @Override
    public void setBalance(String playerName, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        playerDataHandler.getPlayerDataConfig().set("players." + playerID + ".balance", amount);
        playerDataHandler.savePlayerDataConfig();
    }

    @Override
    public void setBalance(String playerName, String bankID, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        playerDataHandler.getPlayerDataConfig().set("players." + playerID + ".accounts." + bankID + ".balance", amount);
        playerDataHandler.savePlayerDataConfig();
    }

    @Override
    public void deposit(String playerName, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        double balance = playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".balance");
        playerDataHandler.getPlayerDataConfig().set("players." + playerID + ".balance", balance + amount);
        playerDataHandler.savePlayerDataConfig();
    }

    @Override
    public void deposit(String playerName, String bankID, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        double balance = playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".accounts." + bankID + ".balance");
        playerDataHandler.getPlayerDataConfig().set("players." + playerUUID + ".accounts." + bankID + ".balance", balance + amount);
        playerDataHandler.savePlayerDataConfig();
    }

    @Override
    public void withdraw(String playerName, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        double balance = playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".balance");
        if (balance >= amount) {
            playerDataHandler.getPlayerDataConfig().set("players." + playerUUID + ".balance", balance - amount);
            playerDataHandler.savePlayerDataConfig();
        }
    }

    @Override
    public void withdraw(String playerName, String bankID, double amount) {
        UUID playerUUID = getPlayerUUID(playerName);
        String playerID = playerUUID.toString();
        double balance = playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".accounts." + bankID + ".balance");
        if (balance >= amount) {
            playerDataHandler.getPlayerDataConfig().set("players." + playerUUID + ".accounts." + bankID + ".balance", balance - amount);
            playerDataHandler.savePlayerDataConfig();
        }
    }

    private UUID getPlayerUUID(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return player.getUniqueId();
    }
}