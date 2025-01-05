package me.github.calaritooo.accounts;

import java.util.UUID;

public interface AccountInterface {

    void createAccount(String playerName, String playerUUID, double initialBalance);
    void createAccount(String playerName, String playerUUID, String bankID, double initialBalance);
    void deleteAccount(String playerName);
    void deleteAccount(String playerName, String bankID);
    boolean hasAccount(String playerName);
    boolean hasAccount(String playerName, String bankID);
    boolean hasFunds(String playerName, double amount);
    boolean hasFunds(String playerName, String bankID, double amount);
    double getBalance(String playerName);
    double getBalance(String playerName, String bankID);
    void setBalance(String playerName, double amount);
    void setBalance(String playerName, String bankID, double amount);
    void deposit(String playerName, double amount);
    void deposit(String playerName, String bankID, double amount);
    void withdraw(String playerName, double amount);
    void withdraw(String playerName, String bankID, double amount);
}
