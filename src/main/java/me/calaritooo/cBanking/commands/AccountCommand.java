package me.calaritooo.cBanking.commands;

import me.calaritooo.cBanking.player.PlayerAccount;
import me.calaritooo.cBanking.bank.BankHandler;
import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.bank.BankData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AccountCommand implements CommandExecutor {

    private final cBanking plugin;
    private final BankHandler bankHandler;
    private final PlayerAccount playerAccount;
    private final BankData bankData;

    public AccountCommand(cBanking plugin) {
        this.plugin = plugin;
        this.bankHandler = plugin.getBankHandler();
        this.playerAccount = plugin.getPlayerAccount();
        this.bankData = plugin.getBankDataHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§7Usage: /acc <bankID> <open/close/deposit/withdraw> <amount>");
            player.sendMessage("§7*Manage your bank account*");
            return true;
        }

        String bankID = args[0];

        if (args.length < 2 && bankHandler.bankExists(bankID)) {
            player.sendMessage("§7Usage: /acc <bankID> <open/close/deposit/withdraw> <amount>");
            player.sendMessage("§7*Manage your bank account*");
            return true;
        }

        if (!bankHandler.bankExists(bankID)) {
            player.sendMessage("§cBank not found!");
            return true;
        }

        String action = args[1].toLowerCase();
        
        String currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");

        switch (action) {
            case "open":
                if (playerAccount.hasAccount(player.getName(), bankID)) {
                    player.sendMessage("§cYou already have an account with this bank.");
                    return true;
                }
                if (bankHandler.getBankOwnerByID(bankID).equalsIgnoreCase(player.getName())) {
                    player.sendMessage("§cYou may not open an account with your own bank!");
                    return true;
                }
                double accountOpeningFee = bankHandler.getAccountOpeningFee(bankID);
                if (!playerAccount.hasFunds(player.getName(), accountOpeningFee)) {
                    player.sendMessage("§cYou do not have enough funds to open an account. Required: §a" + currencySymbol + accountOpeningFee);
                } else {
                    playerAccount.withdraw(player.getName(), accountOpeningFee);
                    playerAccount.createAccount(player.getName(), bankID, 0.0);
                    player.sendMessage("§7Account created with bank §e[§a" + bankID + "§e]§7. Opening fee: §a" + currencySymbol + accountOpeningFee);
                }
                return true;

            case "close":
                if (!playerAccount.hasAccount(player.getName(), bankID)) {
                    player.sendMessage("§cYou do not have an account with this bank.");
                } else {
                    double accountBalance = playerAccount.getBalance(player.getName(), bankID);
                    playerAccount.deleteAccount(player.getName(), bankID);
                    if (accountBalance != 0) {
                        playerAccount.deposit(player.getName(), accountBalance);
                    }
                    player.sendMessage("§7Account with bank §e[§a" + bankID + "§e]§7 has been closed and the balance has been transferred to you.");
                }
                return true;

            case "deposit":
                if (args.length < 3) {
                    player.sendMessage("§7Usage: /acc <bankID> <deposit> <amount>");
                    player.sendMessage("§7*Manage your bank account*");
                }
                else {
                    if (!playerAccount.hasAccount(player.getName(), bankID)) {
                        player.sendMessage("§cYou do not have an account with this bank.");
                    } else {
                        try {
                            BigDecimal amount = new BigDecimal(args[2]).setScale(2, RoundingMode.HALF_UP);
                            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                                player.sendMessage("§cInvalid amount.");
                                return true;
                            }

                            double transactionFee = calculateTransactionFee(bankID, amount.doubleValue(), action);
                            double totalAmount = amount.doubleValue() + transactionFee;

                            if (!playerAccount.hasFunds(player.getName(), totalAmount)) {
                                player.sendMessage("§cYou do not have enough funds. Required: §a" + currencySymbol + totalAmount);
                                return true;
                            }

                            playerAccount.withdraw(player.getName(), totalAmount);
                            playerAccount.deposit(player.getName(), bankID, amount.doubleValue());
                            player.sendMessage("§7Deposited §a" + currencySymbol + amount + "§7 into your account with bank §e[§a" + bankID + "§e]§7. Transaction fee: §a" + currencySymbol + transactionFee);
                        } catch (NumberFormatException e) {
                            player.sendMessage("§cInvalid amount.");
                        }
                    }
                }
                return true;

            case "withdraw":
                if (args.length < 3) {
                    player.sendMessage("§7Usage: /acc <bankID> <withdraw> <amount>");
                    player.sendMessage("§7*Manage your bank account*");
                    return true;
                }
                else if (!playerAccount.hasAccount(player.getName(), bankID)) {
                    player.sendMessage("§cYou do not have an account with this bank.");
                } else {
                    try {
                        BigDecimal amount = new BigDecimal(args[2]).setScale(2, RoundingMode.HALF_UP);
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            player.sendMessage("§cInvalid amount.");
                            return true;
                        }

                        double transactionFee = calculateTransactionFee(bankID, amount.doubleValue(), action);
                        double totalAmount = amount.doubleValue() + transactionFee;

                        if (!playerAccount.hasFunds(player.getName(), bankID, totalAmount)) {
                            player.sendMessage("§cYou do not have enough funds in your account. Required: §a" + currencySymbol + totalAmount);
                            return true;
                        }

                        playerAccount.withdraw(player.getName(), bankID, totalAmount);
                        playerAccount.deposit(player.getName(), amount.doubleValue());
                        player.sendMessage("§7Withdrew §a" + currencySymbol + amount + "§7 from your account with bank §e[§a" + bankID + "§e]§7. Transaction fee: §a" + currencySymbol + transactionFee);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cInvalid amount.");
                    }
                }
                return true;

            default:
                player.sendMessage("§7Usage: /acc <bankID> <open/close/deposit/withdraw> <amount>");
                player.sendMessage("§7*Manage your bank account*");
                return true;
        }
    }

    private double calculateTransactionFee(String bankID, double amount, String action) {
        String feeType = plugin.getConfig().getString("bank-settings.transaction-fee-type");
        double feeValue = bankData.getBanksConfig().getDouble("banks." + bankID + "." + action + "FeeRate");

        if ("percentage".equalsIgnoreCase(feeType)) {
            return amount * (feeValue / 100);
        } else {
            return feeValue;
        }
    }
}