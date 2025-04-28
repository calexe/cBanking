package me.calaritooo.cBanking.player;

import me.calaritooo.cBanking.util.money.Money;

import java.util.UUID;

public class PlayerAccount {

    private final PlayerData playerData;

    public PlayerAccount(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void createAccount(UUID playerUUID, Money initialBalance) {
        playerData.savePlayerData(playerUUID, initialBalance.value());
    }

    public void deleteAccount(UUID playerUUID) {
        playerData.deletePlayerData(playerUUID.toString());
    }

    public boolean exists(UUID playerUUID) {
        return playerData.hasPlayerData(playerUUID);
    }

    public boolean hasFunds(UUID playerUUID, Money amount) {
        Money balance = getBalance(playerUUID);
        return balance.greaterOrEqual(amount);
    }

    public Money getBalance(UUID playerUUID) {
        double balance = playerData.getBalance(playerUUID);
        return Money.of(balance);
    }

    public void setBalance(UUID playerUUID, Money amount) {
        playerData.setBalance(playerUUID, amount.value());
    }

    public void deposit(UUID playerUUID, Money amount) {
        Money balance = getBalance(playerUUID);
        setBalance(playerUUID, balance.add(amount));
    }

    public void withdraw(UUID playerUUID, Money amount) {
        Money balance = getBalance(playerUUID);
        if (balance.greaterOrEqual(amount)) {
            setBalance(playerUUID, balance.subtract(amount));
        }
    }
}

