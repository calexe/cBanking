package me.calaritooo.cBanking.eco;

import me.calaritooo.cBanking.bank.BankAccount;
import me.calaritooo.cBanking.bank.BankData;
import me.calaritooo.cBanking.bank.BankSetting;
import me.calaritooo.cBanking.player.PlayerAccount;
import me.calaritooo.cBanking.player.PlayerData;
import me.calaritooo.cBanking.util.money.Money;
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

    // ───────── PLAYER ACCOUNTS ─────────

    public boolean exists(UUID uuid) {
        return playerAccount.exists(uuid);
    }

    public Money getBalance(UUID uuid) {
        return playerAccount.getBalance(uuid);
    }

    public boolean deposit(UUID uuid, Money amount) {
        if (!validAmount(amount) || !exists(uuid)) return false;
        playerAccount.deposit(uuid, amount);
        playerData.savePlayerConfig(uuid);
        return true;
    }

    public boolean withdraw(UUID uuid, Money amount) {
        if (!validAmount(amount) || !exists(uuid) || !hasFunds(uuid, amount)) return false;
        playerAccount.withdraw(uuid, amount);
        playerData.savePlayerConfig(uuid);
        return true;
    }

    public boolean transfer(UUID from, UUID to, Money amount) {
        return !from.equals(to) && withdraw(from, amount) && deposit(to, amount);
    }

    public boolean hasFunds(UUID uuid, Money amount) {
        return getBalance(uuid).greaterOrEqual(amount);
    }

    public boolean setBalance(UUID uuid, Money amount) {
        if (!exists(uuid)) return false;
        playerAccount.setBalance(uuid, amount);
        playerData.savePlayerConfig(uuid);
        return true;
    }

    public boolean createAccount(UUID uuid, Money initialBalance) {
        if (exists(uuid)) return false;
        playerAccount.createAccount(uuid, initialBalance);
        playerData.savePlayerConfig(uuid);
        return true;
    }

    // ───────── BANK ACCOUNTS ─────────

    public boolean bankExists(String bankID) {
        return bankData.bankExists(bankID);
    }

    public Set<String> getPlayerBankAccounts(UUID playerUUID) {
        return bankAccount.getBankIDsByPlayer(playerUUID);
    }

    public boolean hasBankAccount(String bankID, UUID uuid) {
        return bankAccount.hasAccount(bankID, uuid);
    }

    public Money getBankBalance(String bankID, UUID uuid) {
        return bankAccount.getBalance(bankID, uuid);
    }

    public boolean depositBank(String bankID, UUID uuid, Money amount) {
        if (!validAmount(amount) || !hasBankAccount(bankID, uuid)) return false;
        bankAccount.deposit(bankID, uuid, amount);
        bankData.saveBankConfig(bankID);
        return true;
    }

    public boolean withdrawBank(String bankID, UUID uuid, Money amount) {
        if (!validAmount(amount) || !hasBankAccount(bankID, uuid) || !hasBankFunds(bankID, uuid, amount)) return false;
        bankAccount.withdraw(bankID, uuid, amount);
        bankData.saveBankConfig(bankID);
        return true;
    }

    public boolean transferBankToBank(String fromBankID, UUID fromUUID, String toBankID, UUID toUUID, Money amount) {
        return withdrawBank(fromBankID, fromUUID, amount) && depositBank(toBankID, toUUID, amount);
    }

    public boolean hasBankFunds(String bankID, UUID uuid, Money amount) {
        return getBankBalance(bankID, uuid).greaterOrEqual(amount);
    }

    public boolean setBankBalance(String bankID, UUID uuid, Money amount) {
        if (!hasBankAccount(bankID, uuid)) return false;
        bankAccount.setBalance(bankID, uuid, amount);
        bankData.saveBankConfig(bankID);
        return true;
    }

    public boolean createBankAccount(String bankID, UUID uuid, Money initialBalance) {
        if (!bankExists(bankID)) return false;
        bankAccount.createAccount(bankID, uuid, initialBalance);
        bankData.saveBankConfig(bankID);
        return true;
    }

    public boolean deleteBankAccount(String bankID, UUID uuid) {
        if (!hasBankAccount(bankID, uuid)) return false;
        bankAccount.deleteAccount(bankID, uuid);
        bankData.saveBankConfig(bankID);
        return true;
    }

    // ───────── BANK MANAGEMENT ─────────

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
        Map<UUID, Money> balances = bankAccount.getAllBalances(bankID);
        if (balances.isEmpty()) return;
        for (Map.Entry<UUID, Money> entry : balances.entrySet()) {
            playerAccount.deposit(entry.getKey(), entry.getValue());
            playerData.savePlayerConfig(entry.getKey());
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
        String uuidString = getBankSetting(bankID, BankSetting.OWNER_UUID, String.class);
        return uuidString != null ? UUID.fromString(uuidString) : null;
    }

    public String getBankOwnerName(String bankID) {
        return getBankSetting(bankID, BankSetting.OWNER_NAME, String.class);
    }

    public boolean bankNameExists(String name) {
        return bankData.bankNameExists(name);
    }

    public void setBankSetting(String bankID, BankSetting setting, Object value) {
        FileConfiguration config = bankData.getBankConfig(bankID);
        if (config == null) return;
        config.set(setting.path(), value);
        bankData.saveBankConfig(bankID);
    }

    public <T> T getBankSetting(String bankID, BankSetting setting, Class<T> type) {
        FileConfiguration config = bankData.getBankConfig(bankID);
        if (config == null) return null;

        if (!config.contains(setting.path())) {
            config.set(setting.path(), setting.defaultValue());
            bankData.saveBankConfig(bankID);
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

    public boolean validAmount(Money amount) {
        return amount != null && amount.greaterOrEqual(Money.zero());
    }
}
