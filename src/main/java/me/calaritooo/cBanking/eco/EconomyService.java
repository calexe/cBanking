package me.calaritooo.cBanking.eco;

import me.calaritooo.cBanking.bank.BankAccount;
import me.calaritooo.cBanking.bank.BankData;
import me.calaritooo.cBanking.bank.BankSetting;
import me.calaritooo.cBanking.player.PlayerAccount;
import me.calaritooo.cBanking.player.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EconomyService {

    private final PlayerAccount playerAccount;
    private final PlayerData playerData;
    private final BankAccount bankAccount;
    private final BankData bankData;

    public EconomyService(PlayerAccount playerAccount, PlayerData playerData,
                          BankAccount bankAccount, BankData bankData) {
        this.playerAccount = playerAccount;
        this.playerData = playerData;
        this.bankAccount = bankAccount;
        this.bankData = bankData;
    }

    // ────────────── PLAYER ACCOUNTS ──────────────

    public boolean exists(UUID uuid) {
        return playerAccount.exists(uuid);
    }

    public double getBalance(UUID uuid) {
        return playerAccount.getBalance(uuid);
    }

    public boolean deposit(UUID uuid, double amount) {
        if (!validAmount(amount) || !exists(uuid)) return false;
        playerAccount.deposit(uuid, amount);
        return true;
    }

    public boolean withdraw(UUID uuid, double amount) {
        if (!validAmount(amount) || !exists(uuid) || !hasFunds(uuid, amount)) return false;
        playerAccount.withdraw(uuid, amount);
        return true;
    }

    public boolean transfer(UUID from, UUID to, double amount) {
        return !from.equals(to) && withdraw(from, amount) && deposit(to, amount);
    }

    public boolean hasFunds(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public boolean setBalance(UUID uuid, double amount) {
        if (!exists(uuid)) return false;
        playerAccount.setBalance(uuid, amount);
        return true;
    }

    public boolean createAccount(UUID uuid, double initialBalance) {
        if (exists(uuid)) return false;
        playerAccount.createAccount(uuid, initialBalance);
        return true;
    }

    // ────────────── BANK ACCOUNTS ──────────────

    public boolean bankExists(String bankID) {
        return bankData.bankExists(bankID);
    }

    public boolean hasBankAccount(String bankID, UUID uuid) {
        return bankAccount.hasAccount(bankID, uuid);
    }

    public double getBankBalance(String bankID, UUID uuid) {
        return bankAccount.getBalance(bankID, uuid);
    }

    public boolean depositBank(String bankID, UUID uuid, double amount) {
        if (!validAmount(amount) || !hasBankAccount(bankID, uuid)) return false;
        bankAccount.deposit(bankID, uuid, amount);
        return true;
    }

    public boolean withdrawBank(String bankID, UUID uuid, double amount) {
        if (!validAmount(amount) || !hasBankAccount(bankID, uuid) || !hasBankFunds(bankID, uuid, amount)) return false;
        bankAccount.withdraw(bankID, uuid, amount);
        return true;
    }

    public boolean transferBankToBank(String fromBankID, UUID fromUUID, String toBankID, UUID toUUID, double amount) {
        return withdrawBank(fromBankID, fromUUID, amount) && depositBank(toBankID, toUUID, amount);
    }

    public boolean hasBankFunds(String bankID, UUID uuid, double amount) {
        return getBankBalance(bankID, uuid) >= amount;
    }

    public boolean setBankBalance(String bankID, UUID uuid, double amount) {
        if (!hasBankAccount(bankID, uuid)) return false;
        bankAccount.setBalance(bankID, uuid, amount);
        return true;
    }

    public boolean createBankAccount(String bankID, UUID uuid, double initialBalance) {
        if (!bankExists(bankID)) return false;
        bankAccount.createAccount(bankID, uuid, initialBalance);
        return true;
    }

    public boolean deleteBankAccount(String bankID, UUID uuid) {
        if (!hasBankAccount(bankID, uuid)) return false;
        bankAccount.deleteAccount(bankID, uuid);
        return true;
    }

    // ────────────── BANK MANAGEMENT ──────────────

    public boolean createBank(String name, UUID ownerUUID) {
        String id = generateBankId(name);
        if (bankData.bankExists(id)) return false;
        bankData.createBank(id, name, ownerUUID);
        return true;
    }

    public void deleteBank(String bankID) {
        bankData.deleteBank(bankID);
    }

    public void deleteBankAndTransferBalances(String bankID) {
        Map<UUID, Double> balances = bankAccount.getAllBalances(bankID);
        if (balances.isEmpty()) return;
        for (UUID uuid : balances.keySet()) {
            playerAccount.deposit(uuid, balances.get(uuid));
        }
        deleteBank(bankID);
    }

    public Set<String> getAllBankIDs() {
        return bankData.getBankIDs();
    }

    public String getBankName(String bankID) {
        return getBankSetting(bankID, BankSetting.NAME, String.class);
    }

    public String getBankIDByName(String name) {
        return bankData.getBankIDByName(name);
    }

    public UUID getBankOwnerUUID(String bankID) {
        return UUID.fromString(getBankSetting(bankID, BankSetting.OWNER_UUID, String.class));
    }

    public String getBankOwnerName(String bankID) {
        return getBankSetting(bankID, BankSetting.OWNER_NAME, String.class);
    }

    public boolean bankNameExists(String name) {
        return bankData.bankNameExists(name);
    }

    public void setBankSetting(String bankID, BankSetting setting, Object value) {
        FileConfiguration config = bankData.getBankConfig(bankID);
        config.set(setting.path(), value);
        bankData.saveBankConfig(bankID, config);
    }

    public <T> T getBankSetting(String bankID, BankSetting setting, Class<T> type) {
        FileConfiguration config = bankData.getBankConfig(bankID);

        if (!config.contains(setting.path())) {
            config.set(setting.path(), setting.defaultValue());
            bankData.saveBankConfig(bankID, config);
        }

        return config.getObject(setting.path(), type);
    }

    public void setBankOwner(String bankID, UUID ownerUUID) {
        setBankSetting(bankID, BankSetting.OWNER_UUID, ownerUUID.toString());
        setBankSetting(bankID, BankSetting.OWNER_NAME, playerData.getName(ownerUUID));
    }

    public String generateBankId(String name) {
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    }

    public boolean validAmount(double amount) {
        return amount >= 0;
    }
}
