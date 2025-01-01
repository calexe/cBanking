package me.github.calaritooo;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {

    private static Economy economy = null;
    private static final cBanking plugin = cBanking.getInstance();

    public VaultHook() {
    }

    public static boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public static boolean hasEconomy() {
        return economy == null;
    }

    public static double getBalance(OfflinePlayer target) {
        if (hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        double balance = economy.getBalance(target);
        plugin.getPlayerDataManager().setBalance(target, balance);
        return balance;
    }

    public static String withdraw(OfflinePlayer target, double amount) {
        if (hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        EconomyResponse response = economy.withdrawPlayer(target, amount);
        if (response.transactionSuccess()) {
            plugin.getPlayerDataManager().updateBalanceFromVault(target);
            return null;
        } else {
            return response.errorMessage;
        }
    }

    public static String deposit(OfflinePlayer target, double amount) {
        if (hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        EconomyResponse response = economy.depositPlayer(target, amount);
        if (response.transactionSuccess()) {
            plugin.getPlayerDataManager().updateBalanceFromVault(target);
            return null;
        } else {
            return response.errorMessage;
        }
    }

    public static String formatCurrencySymbol(double amount) {
        if (hasEconomy())
            throw new UnsupportedOperationException("Vault Economy not found, call hasEconomy() to check it first.");

        return economy.format(amount);
        //return amount + " " + (((int) amount) == 1 ? economy.currencyNameSingular() : economy.currencyNamePlural());
    }


    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
        }
    }

    public Economy getEconomy() {
        return economy;
    }
}