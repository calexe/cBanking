package me.calaritooo.cBanking.player;

import java.util.UUID;

public class PlayerAccount {

    private final PlayerData playerData;

    public PlayerAccount(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void createAccount(UUID playerUUID, double initialBalance) {
        playerData.savePlayerData(playerUUID, initialBalance);
    }

    public void deleteAccount(UUID playerUUID) {
        playerData.deletePlayerData(playerUUID.toString());
    }

    public boolean exists(UUID playerUUID) {
        return playerData.hasPlayerData(playerUUID);
    }

    public boolean hasFunds(UUID playerUUID, double amount) {
        double balance = getBalance(playerUUID);
        return balance >= amount;
    }

    public double getBalance(UUID playerUUID) {
        return playerData.getBalance(playerUUID);
    }

    public void setBalance(UUID playerUUID, double amount) {
        playerData.setBalance(playerUUID, amount);
    }

    public void deposit(UUID playerUUID, double amount) {
        double balance = getBalance(playerUUID);
        setBalance(playerUUID, balance + amount);
    }

    public void withdraw(UUID playerUUID, double amount) {
        double balance = getBalance(playerUUID);
        if (balance >= amount) {
            setBalance(playerUUID, balance - amount);
        }
    }
}
