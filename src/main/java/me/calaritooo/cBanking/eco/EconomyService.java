package me.calaritooo.cBanking.eco;

import me.calaritooo.cBanking.bank.BankAccount;
import me.calaritooo.cBanking.bank.BankData;
import me.calaritooo.cBanking.player.PlayerAccount;
import me.calaritooo.cBanking.cBanking;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EconomyService {

    private static PlayerAccount playerAccount;
    private static BankAccount bankAccount;
    private static BankData bankData;
    private static cBanking plugin;

    public static void init(cBanking pluginInstance) {
        plugin = pluginInstance;
        playerAccount = plugin.getPlayerAccount();
        bankAccount = plugin.getBankAccount();
        bankData = plugin.getBankData();
    }

    // ────────────── PLAYER ECONOMY ──────────────

    public static boolean hasAccount(UUID uuid) {
        return playerAccount.hasAccount(uuid);
    }

    public static double getBalance(UUID uuid) {
        return playerAccount.getBalance(uuid);
    }

    public static boolean deposit(UUID uuid, double amount) {
        if (amount < 0 || !hasAccount(uuid)) return false;
        playerAccount.deposit(uuid, amount);
        return true;
    }

    public static boolean withdraw(UUID uuid, double amount) {
        if (amount < 0 || !hasAccount(uuid)) return false;
        if (!playerAccount.hasFunds(uuid, amount)) return false;
        playerAccount.withdraw(uuid, amount);
        return true;
    }

    public static boolean transfer(UUID from, UUID to, double amount) {
        if (from.equals(to)) return false;
        if (withdraw(from, amount)) {
            return deposit(to, amount);
        }
        return false;
    }

    public static boolean hasFunds(UUID uuid, double amount) {
        return playerAccount.hasFunds(uuid, amount);
    }

    public static boolean setBalance(UUID uuid, double amount) {
        if (!hasAccount(uuid)) return false;
        playerAccount.setBalance(uuid, amount);
        return true;
    }

    public static boolean createAccount(UUID uuid, double initialBalance) {
        if (hasAccount(uuid)) return false;
        playerAccount.createAccount(uuid, initialBalance);
        return true;
    }

    // ────────────── BANK ACCOUNT ──────────────

    public static boolean hasBank(String bankID) {
        return bankData.bankExists(bankID);
    }

    public static boolean hasBankAccount(String bankID, UUID uuid) {
        return bankAccount.hasAccount(bankID, uuid);
    }

    public static double getBankBalance(String bankID, UUID uuid) {
        return bankAccount.getBalance(bankID, uuid);
    }

    public static boolean depositBank(String bankID, UUID uuid, double amount) {
        if (amount < 0 || !hasBankAccount(bankID, uuid)) return false;
        bankAccount.deposit(bankID, uuid, amount);
        return true;
    }

    public static boolean withdrawBank(String bankID, UUID uuid, double amount) {
        if (amount < 0 || !hasBankAccount(bankID, uuid)) return false;
        if (bankAccount.getBalance(bankID, uuid) < amount) return false;
        bankAccount.withdraw(bankID, uuid, amount);
        return true;
    }

    public static boolean transferBankToBank(String fromBankID, UUID fromUUID, String toBankID, UUID toUUID, double amount) {
        if (withdrawBank(fromBankID, fromUUID, amount)) {
            return depositBank(toBankID, toUUID, amount);
        }
        return false;
    }

    public static boolean setBankBalance(String bankID, UUID uuid, double amount) {
        if (!hasBankAccount(bankID, uuid)) return false;
        bankAccount.setBalance(bankID, uuid, amount);
        return true;
    }

    public static boolean createBankAccount(String bankID, UUID uuid, double initialBalance) {
        if (!hasBank(bankID)) return false;
        bankAccount.createAccount(bankID, uuid, initialBalance);
        return true;
    }

    public static boolean deleteBankAccount(String bankID, UUID uuid) {
        if (!hasBankAccount(bankID, uuid)) return false;
        bankAccount.deleteAccount(bankID, uuid);
        return true;
    }

    // ────────────── BANK DATA ──────────────

    public static void createBank(String bankID, String name, String owner) {
        bankData.createBank(bankID, name, owner);
    }

    public static void deleteBank(String bankID) {
        bankData.deleteBank(bankID);
    }

    public static Map<String, Double> deleteBankAndTransferBalances(String bankID) {
        return bankData.deleteBankAndTransferBalances(bankID);
    }

    public static Set<String> getAllBankIDs() {
        return bankData.getBankIDs();
    }

    public static String getBankNameByID(String bankID) {
        return bankData.getBankNameByID(bankID);
    }

    public static String getBankIDByName(String bankName) {
        return bankData.getBankIDByName(bankName);
    }

    public static String getBankIDByOwner(String ownerName) {
        return bankData.getBankIDByOwner(ownerName);
    }

    public static String getBankOwnerByID(String bankID) {
        return bankData.getBankOwnerByID(bankID);
    }

    public static boolean bankNameExists(String bankName) {
        return bankData.bankNameExists(bankName);
    }

    public static void setBankName(String bankID, String name) {
        bankData.setBankName(bankID, name);
    }

    public static void setBankOwner(String bankID, String owner) {
        bankData.setBankOwner(bankID, owner);
    }

    public static double getAssets(String bankID) {
        return bankData.getAssets(bankID);
    }

    public static void setAssets(String bankID, double value) {
        bankData.setAssets(bankID, value);
    }

    public static double getInterestRate(String bankID) {
        return bankData.getInterestRate(bankID);
    }

    public static void setInterestRate(String bankID, double value) {
        bankData.setInterestRate(bankID, value);
    }

    public static double getAccountGrowthRate(String bankID) {
        return bankData.getAccountGrowthRate(bankID);
    }

    public static void setAccountGrowthRate(String bankID, double value) {
        bankData.setAccountGrowthRate(bankID, value);
    }

    public static double getAccountOpeningFee(String bankID) {
        return bankData.getAccountOpeningFee(bankID);
    }

    public static void setAccountOpeningFee(String bankID, double value) {
        bankData.setAccountOpeningFee(bankID, value);
    }

    public static double getMaintenanceFeeRate(String bankID) {
        return bankData.getMaintenanceFeeRate(bankID);
    }

    public static void setMaintenanceFeeRate(String bankID, double value) {
        bankData.setMaintenanceFeeRate(bankID, value);
    }

    public static double getDepositFeeRate(String bankID) {
        return bankData.getDepositFeeRate(bankID);
    }

    public static void setDepositFeeRate(String bankID, double value) {
        bankData.setDepositFeeRate(bankID, value);
    }

    public static double getWithdrawalFeeRate(String bankID) {
        return bankData.getWithdrawalFeeRate(bankID);
    }

    public static void setWithdrawalFeeRate(String bankID, double value) {
        bankData.setWithdrawalFeeRate(bankID, value);
    }
}
