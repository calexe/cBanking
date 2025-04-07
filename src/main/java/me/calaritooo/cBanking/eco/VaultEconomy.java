package me.calaritooo.cBanking.eco;

import me.calaritooo.cBanking.cBanking;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class VaultEconomy extends BaseEconomy {

    private final cBanking plugin;

    public VaultEconomy(cBanking plugin) {
        this.plugin = plugin;
        EconomyService.init(plugin);
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
        String symbol = plugin.getConfig().getString("economy-settings.currency-symbol", "$");
        return symbol + String.format("%.2f", amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return EconomyService.hasAccount(player.getUniqueId());
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return EconomyService.createAccount(player.getUniqueId(), plugin.getConfig().getDouble("economy-settings.starting-bal", 0.0));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return round(EconomyService.getBalance(player.getUniqueId()));
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return EconomyService.hasFunds(player.getUniqueId(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return failure("Cannot deposit negative funds.");
        boolean result = EconomyService.deposit(player.getUniqueId(), amount);
        double newBalance = EconomyService.getBalance(player.getUniqueId());
        return result ? success(amount, newBalance) : failure("Deposit failed.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return failure("Cannot withdraw negative funds.");
        boolean result = EconomyService.withdraw(player.getUniqueId(), amount);
        double newBalance = EconomyService.getBalance(player.getUniqueId());
        return result ? success(amount, newBalance) : failure("Insufficient funds.");
    }

    // Vault bank support (coming soon)
    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public EconomyResponse createBank(String bankID, OfflinePlayer owner) {
        return notSupported();
    }

    @Override
    public EconomyResponse deleteBank(String bankID) {
        return notSupported();
    }

    @Override
    public EconomyResponse bankBalance(String bankID) {
        return notSupported();
    }

    @Override
    public EconomyResponse bankHas(String bankID, double amount) {
        return notSupported();
    }

    @Override
    public EconomyResponse bankWithdraw(String bankID, double amount) {
        return notSupported();
    }

    @Override
    public EconomyResponse bankDeposit(String bankID, double amount) {
        return notSupported();
    }

    @Override
    public EconomyResponse isBankOwner(String bankID, OfflinePlayer player) {
        return notSupported();
    }

    @Override
    public EconomyResponse isBankMember(String bankID, OfflinePlayer player) {
        return notSupported();
    }

    @Override
    public java.util.List<String> getBanks() {
        return new java.util.ArrayList<>();
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
