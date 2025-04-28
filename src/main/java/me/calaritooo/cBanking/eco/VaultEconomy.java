package me.calaritooo.cBanking.eco;

import me.calaritooo.cBanking.bank.BankSetting;
import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.money.Money;
import me.calaritooo.cBanking.util.money.MoneyProvider;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultEconomy extends BaseEconomy {

    private final cBanking plugin;
    private final EconomyService eco = cBankingCore.getEconomyService();
    private final ConfigurationProvider config = cBankingCore.getConfigurationProvider();
    private final MoneyProvider moneyProvider = cBankingCore.getMoneyProvider();

    public VaultEconomy(cBanking plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String currencyNameSingular() {
        return config.get(ConfigurationOption.ECONOMY_CURRENCY_NAME);
    }

    @Override
    public String currencyNamePlural() {
        return config.get(ConfigurationOption.ECONOMY_CURRENCY_NAME_PLURAL);
    }

    @Override
    public String format(double amount) {
        String symbol = moneyProvider.getCurrencySymbol();
        return symbol + String.format("%.2f", amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return eco.exists(player.getUniqueId());
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        double startAmount = config.get(ConfigurationOption.ECONOMY_STARTING_BALANCE);
        return eco.createAccount(player.getUniqueId(), Money.of(startAmount));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return eco.getBalance(player.getUniqueId()).value();
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return eco.hasFunds(player.getUniqueId(), Money.of(amount));
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return failure("Cannot deposit negative funds.");

        boolean result = eco.deposit(player.getUniqueId(), Money.of(amount));
        double newBalance = eco.getBalance(player.getUniqueId()).value();

        return result ? success(amount, newBalance) : failure("Deposit failed.");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return failure("Cannot withdraw negative funds.");

        boolean result = eco.withdraw(player.getUniqueId(), Money.of(amount));
        double newBalance = eco.getBalance(player.getUniqueId()).value();

        return result ? success(amount, newBalance) : failure("Insufficient funds.");
    }

    // ───── BANK SUPPORT ─────

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
        Money assets = Money.of(eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class));
        return new EconomyResponse(assets.value(), assets.value(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankHas(String bankID, double amount) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        Money assets = Money.of(eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class));
        if (assets.value() < amount) {
            return failure("Insufficient funds.");
        }
        return emptySuccess();
    }

    @Override
    public EconomyResponse bankWithdraw(String bankID, double amount) {
        if (!eco.bankExists(bankID)) return failure("Bank does not exist.");
        if (amount < 0) return failure("Cannot withdraw negative funds.");

        Money assets = Money.of(eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class));
        if (assets.value() < amount) return failure("Insufficient funds.");

        Money newAssets = Money.of(assets.value() - amount);
        eco.setBankSetting(bankID, BankSetting.ASSETS, newAssets.value());
        return success(amount, newAssets.value());
    }

    @Override
    public EconomyResponse bankDeposit(String bankID, double amount) {
        if (!eco.bankExists(bankID)) return failure("Bank does not exist.");
        if (amount < 0) return failure("Cannot deposit negative funds.");

        Money assets = Money.of(eco.getBankSetting(bankID, BankSetting.ASSETS, Double.class));
        Money newAssets = Money.of(assets.value() + amount);
        eco.setBankSetting(bankID, BankSetting.ASSETS, newAssets.value());

        return success(amount, newAssets.value());
    }

    @Override
    public EconomyResponse isBankOwner(String bankID, OfflinePlayer player) {
        if (!eco.bankExists(bankID)) {
            return failure("Bank does not exist.");
        }
        if (!player.getUniqueId().equals(eco.getBankOwnerUUID(bankID))) {
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
            return failure("That player is not a bank member.");
        }
        return emptySuccess();
    }

    @Override
    public List<String> getBanks() {
        return eco.getAllBankIDs().stream().toList();
    }
}
