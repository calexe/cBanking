package me.calaritooo.cBanking;

import me.calaritooo.cBanking.accounts.AccountHandler;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ServerEconomy extends AbstractEconomy {

    private final cBanking plugin;
    private final AccountHandler accountHandler;
    private final String currencySymbol;
    private final double startingBal;

    public ServerEconomy(cBanking plugin) {
        this.plugin = plugin;
        this.accountHandler = plugin.getAccountHandler();

        this.currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");
        this.startingBal = plugin.getConfig().getDouble("economy-settings.starting-bal");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "cBanking";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amt) {
        return String.format("%s%.2f", currencySymbol, amt);
    }

    @Override
    public String currencyNamePlural() {
        return plugin.getConfig().getString("economy-settings.currency-name-plural");
    }

    @Override
    public String currencyNameSingular() {
        return plugin.getConfig().getString("economy-settings.currency-name");
    }

    @Override
    public boolean hasAccount(String s) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return accountHandler.getBalance(offlinePlayer.getName()) > 0;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer);
    }

    @Override
    public double getBalance(String s) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        double rawBalance = accountHandler.getBalance(offlinePlayer.getName());
        BigDecimal roundedBalance = new BigDecimal(rawBalance).setScale(2, RoundingMode.HALF_UP);
        return roundedBalance.doubleValue();
    }

    @Override
    public double getBalance(String s, String s1) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return has(player, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return getBalance(offlinePlayer) >= v;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return has(player, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return withdrawPlayer(player, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, null);
        }

        String playerName = player.getName();
        double currentBalance = accountHandler.getBalance(playerName);

        if (currentBalance < amount) {
            return new EconomyResponse(0, currentBalance, EconomyResponse.ResponseType.FAILURE, null);
        }

        double newBalance = currentBalance - amount;
        BigDecimal roundedBalance = new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP);

        accountHandler.setBalance(playerName, roundedBalance.doubleValue());

        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return withdrawPlayer(player, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return depositPlayer(player, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, null);
        }

        String playerName = player.getName();
        double currentBalance = accountHandler.getBalance(playerName);
        double newBalance = currentBalance + amount;
        BigDecimal roundedBalance = new BigDecimal(newBalance).setScale(2, RoundingMode.HALF_UP);

        accountHandler.setBalance(playerName, roundedBalance.doubleValue());

        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return depositPlayer(player, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null);
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        String playerUUID = player.getUniqueId().toString();
        String playerName = player.getName();

        if (accountHandler.getBalance(playerName) > 0) {
            return false; // Account already exists
        }

        accountHandler.createAccount(playerName, playerUUID, startingBal);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(s);
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer);
    }
}