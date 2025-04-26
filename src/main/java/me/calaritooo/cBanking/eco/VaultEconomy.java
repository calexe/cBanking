package me.calaritooo.cBanking.eco;

import me.calaritooo.cBanking.bank.BankSetting;
import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VaultEconomy extends BaseEconomy {

    private final cBanking plugin;
    private final EconomyService eco = cBankingCore.getEconomyService();

    public VaultEconomy(cBanking plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "cBanking";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String currencyNameSingular() {
        return plugin.getConfig().getString("economy-settings.currency-name", "Coin");
    }

    @Override
    public String currencyNamePlural() {
        return plugin.getConfig().getString("economy-settings.currency-name-plural", "Coins");
    }

    @Override
    public String format(double amount) {
        String symbol = plugin.getConfig().getString("economy-settings.currency-symbol", "ȼ");
        return symbol + String.format("%.2f", amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return eco.exists(player.getUniqueId());
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return eco.createAccount(player.getUniqueId(), plugin.getConfig().getDouble("economy-settings.starting-bal", 0.0));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return round(eco.getBalance(player.getUniqueId()));
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return eco.hasFunds(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return failure("Cannot deposit negative funds.");
        boolean result = eco.deposit(player.getUniqueId(), amount);
        double newBalance = eco.getBalance(player.getUniqueId());
        return result ? success(amount, newBalance) : failure("Deposit failed.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return failure("Cannot withdraw negative funds.");
        boolean result = eco.withdraw(player.getUniqueId(), amount);
        double newBalance = eco.getBalance(player.getUniqueId());
        return result ? success(amount, newBalance) : failure("Insufficient funds.");
    }

    // Vault bank support (coming soon)
    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public EconomyResponse createBank(String bankID, OfflinePlayer owner) {
        if (eco.createBank(bankID, owner.getUniqueId())) {
            return emptySuccess();
        } else {
            return failure("Bank already exists.");
        }
    }

    @Override
    public EconomyResponse deleteBank(String bankID) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        eco.deleteBank(bankID);
        return emptySuccess();
    }

    @Override
    public EconomyResponse bankBalance(String bankID) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class);
        return emptySuccess();
    }

    @Override
    public EconomyResponse bankHas(String bankID, double amount) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        if (eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class) < amount) {
            return failure("Insufficient funds.");
        }
        return emptySuccess();
    }

    @Override
    public EconomyResponse bankWithdraw(String bankID, double amount) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        if (eco.validAmount(amount)) {
            return failure("Cannot withdraw negative funds.");
        }
        double assets = eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class);
        if (assets < 0 || assets < amount) {
            return failure("Insufficient funds.");
        }
        double newAssets = assets - amount;
        eco.setBankSetting(bankID, BankSetting.ASSETS, newAssets);
        return success(newAssets, eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class));
    }

    @Override
    public EconomyResponse bankDeposit(String bankID, double amount) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        if (eco.validAmount(amount)) {
            return failure("Cannot deposit negative funds.");
        }
        double assets = eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class);
        double newAssets = assets + amount;

        eco.setBankSetting(bankID, BankSetting.ASSETS, newAssets);
        return success(newAssets, eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class));
    }

    @Override
    public EconomyResponse isBankOwner(String bankID, OfflinePlayer player) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        if (eco.getBankOwnerUUID(bankID) != player.getUniqueId()) {
            return failure("That player is not the bank owner.");
        }
        return emptySuccess();
    }

    @Override
    public EconomyResponse isBankMember(String bankID, OfflinePlayer player) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        if (!eco.hasBankAccount(bankID, player.getUniqueId())) {
            return failure("That player is not the bank member.");
        }
        return emptySuccess();
    }

    @Override
    public java.util.List<String> getBanks() {
        return eco.getAllBankIDs().stream().toList();
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
