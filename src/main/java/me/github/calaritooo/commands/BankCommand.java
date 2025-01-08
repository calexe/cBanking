package me.github.calaritooo.commands;

import me.github.calaritooo.accounts.AccountHandler;
import me.github.calaritooo.banks.BankHandler;
import me.github.calaritooo.cBanking;
import me.github.calaritooo.data.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BankCommand implements CommandExecutor {
    private final cBanking plugin;
    private final BankHandler bankHandler;
    private final PlayerDataHandler playerDataHandler;
    private final AccountHandler accountHandler;

    public BankCommand(cBanking plugin) {
        this.plugin = plugin;
        this.bankHandler = new BankHandler(plugin);
        this.playerDataHandler = new PlayerDataHandler(plugin);
        this.accountHandler = new AccountHandler(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§7Usage: /bank <open/close/accounts/loans/manage>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        String currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");

        switch (subCommand) {
            case "open":
                if (!player.hasPermission("cbanking.bank.open")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                if (args.length != 3) {
                    player.sendMessage("§7Usage: /bank open <name> <ID>");
                    return true;
                }
                if (bankHandler.getBankIDByName(player.getName()) != null) {
                    player.sendMessage("§cYou already own a bank.");
                    return true;
                }
                String newBankName = args[1];
                String newBankID = args[2];
                bankHandler.createBank(newBankID, newBankName, player.getName());
                player.sendMessage("§7Bank §a" + newBankName + "§7 with ID §a" + newBankID + "§7 has been opened.");
                break;

            case "close":
                if (!player.hasPermission("cbanking.bank.close")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                String closeBankID = bankHandler.getBankIDByName(player.getName());
                if (closeBankID == null) {
                    player.sendMessage("§cYou do not own a bank to close.");
                    return true;
                }
                bankHandler.deleteBank(closeBankID);
                player.sendMessage("§7Your bank has been closed.");
                break;

            case "accounts":
                if (!player.hasPermission("cbanking.bank.accounts")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                String accountsBankID = bankHandler.getBankIDByName(player.getName());
                if (accountsBankID == null) {
                    player.sendMessage("§cYou do not own a bank.");
                    return true;
                }
                for (String playerUUID : playerDataHandler.getPlayerDataConfig().getKeys(false)) {
                    if (playerDataHandler.getPlayerDataConfig().isConfigurationSection(playerUUID + ".accounts")) {
                        String balancePath = playerUUID + ".accounts." + accountsBankID;
                        if (playerDataHandler.getPlayerDataConfig().contains(balancePath)) {
                            double balance = playerDataHandler.getPlayerDataConfig().getDouble(balancePath);
                            String playerName = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName();
                            player.sendMessage("§aPlayer: " + playerName + "§7, Balance: §a" + currencySymbol + balance);
                        }
                    }
                }
                break;

            case "loans":
                if (!player.hasPermission("cbanking.bank.loans")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                String loansBankID = bankHandler.getBankIDByName(player.getName());
                if (loansBankID == null) {
                    player.sendMessage("§cYou do not own a bank.");
                    return true;
                }
                player.sendMessage("§7Loan management is not implemented yet.");
                break;

            case "manage":
                if (!player.hasPermission("cbanking.bank.manage")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                String manageBankID = bankHandler.getBankIDByName(player.getName());
                if (manageBankID == null) {
                    player.sendMessage("§cYou do not own a bank.");
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage("§7Usage: /bank manage <setting> [value]");
                    return true;
                }
                String setting = args[1].toLowerCase();
                String value = args.length > 2 ? args[2] : null;

                switch (setting) {
                    case "name":
                        if (!player.hasPermission("cbanking.bank.manage.name")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current bank name: §a" + bankHandler.getBankNameByID(manageBankID));
                        } else {
                            bankHandler.setBankName(manageBankID, value);
                            player.sendMessage("§7Bank name has been updated to §a" + value + "§7.");
                        }
                        break;

                    case "owner":
                        if (!player.hasPermission("cbanking.bank.manage.owner")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current bank owner: §a" + bankHandler.getBankOwnerByID(manageBankID));
                        } else {
                            bankHandler.setBankOwner(manageBankID, value);
                            player.sendMessage("§7Bank owner has been updated to §a" + value + "§7.");
                        }
                        break;

                    case "rate":
                        if (!player.hasPermission("cbanking.bank.manage.rate")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current interest rate: §a" + bankHandler.getInterestRate(manageBankID));
                        } else {
                            bankHandler.setInterestRate(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Interest rate has been updated to §a" + value + "§7%.");
                        }
                        break;

                    case "assets":
                        if (!player.hasPermission("cbanking.bank.manage.assets")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current assets: §a" + bankHandler.getAssets(manageBankID));
                        } else {
                            double currentAssets = bankHandler.getAssets(manageBankID);
                            double amount = Double.parseDouble(value);
                            if (args.length > 3) {
                                String action = args[2].toLowerCase();
                                switch (action) {
                                    case "add":
                                        if (accountHandler.hasFunds(player.getName(), amount)) {
                                            accountHandler.withdraw(player.getName(), amount);
                                            bankHandler.setAssets(manageBankID, currentAssets + amount);
                                            player.sendMessage("§7Added §a" + amount + "§7 to bank assets. New assets balance: §a" + currencySymbol + (currentAssets + amount) + "§7.");
                                            break;
                                        } else {
                                            player.sendMessage("§cInsufficient funds!");
                                            break;
                                        }
                                    case "remove":
                                        if (currentAssets >= amount) {
                                            bankHandler.setAssets(manageBankID, currentAssets - amount);
                                            player.sendMessage("§7Removed §a" + amount + "§7 from bank assets. New assets balance: §a" + currencySymbol + (currentAssets - amount) + "§7.");
                                            break;
                                        }
                                        player.sendMessage("§cInsufficient assets!");
                                        break;
                                    default:
                                        player.sendMessage("§7Usage: /bank manage assets <add/remove> <amount>");
                                        break;
                                }
                            } else {
                                player.sendMessage("§7Usage: /bank manage assets <add/remove> <amount>");
                            }
                        }
                        break;

                    case "maintenance":
                        if (!player.hasPermission("cbanking.bank.manage.maintenance")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current maintenance fee rate: §a" + bankHandler.getMaintenanceFeeRate(manageBankID));
                        } else {
                            bankHandler.setMaintenanceFeeRate(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Maintenance fee rate has been updated to §a" + value + "§7.");
                        }
                        break;

                    case "openingfee":
                        if (!player.hasPermission("cbanking.bank.manage.accountopeningfee")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current account opening fee: §a" + bankHandler.getAccountOpeningFee(manageBankID));
                        } else {
                            bankHandler.setAccountOpeningFee(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Account opening fee has been updated to §a" + value + "§7.");
                        }
                        break;

                    case "growth":
                        if (!player.hasPermission("cbanking.bank.manage.accountgrowth")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current account growth rate: §a" + bankHandler.getAccountGrowthRate(manageBankID));
                        } else {
                            bankHandler.setAccountGrowthRate(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Account growth rate has been updated to §a" + value + "§7%.");
                        }
                        break;

                    case "deposit":
                        if (!player.hasPermission("cbanking.bank.manage.depositfee")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current deposit fee: §a" + bankHandler.getDepositFeeRate(manageBankID));
                        } else {
                            bankHandler.setDepositFeeRate(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Deposit fee has been updated to §a" + value + "§7.");
                        }
                        break;

                    case "withdrawal":
                        if (!player.hasPermission("cbanking.bank.manage.withdrawalfee")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            player.sendMessage("§7Current withdrawal fee: §a" + bankHandler.getWithdrawalFeeRate(manageBankID));
                        } else {
                            bankHandler.setWithdrawalFeeRate(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Withdrawal fee has been updated to §a" + value + "§7.");
                        }
                        break;

                    default:
                        player.sendMessage("§cInvalid setting.");
                        return true;
                }
                break;

            default:
                player.sendMessage("§7Usage: /bank <open/close/accounts/loans/manage>");
                break;
        }
        return true;
    }
}