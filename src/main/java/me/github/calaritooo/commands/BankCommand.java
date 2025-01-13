package me.github.calaritooo.commands;

import me.github.calaritooo.accounts.AccountHandler;
import me.github.calaritooo.banks.BankHandler;
import me.github.calaritooo.cBanking;
import me.github.calaritooo.data.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BankCommand implements CommandExecutor {

    private final cBanking plugin;
    private final BankHandler bankHandler;
    private final PlayerDataHandler playerDataHandler;
    private final AccountHandler accountHandler;
    private final Set<UUID> closeRequests = new HashSet<>();

    public BankCommand(cBanking plugin) {
        this.plugin = plugin;
        this.bankHandler = plugin.getBankHandler();
        this.playerDataHandler = plugin.getPlayerDataHandler();
        this.accountHandler = plugin.getAccountHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        boolean banksEnabled = plugin.getConfig().getBoolean("modules.enable-banks");
        boolean loansEnabled = plugin.getConfig().getBoolean("modules.enable-loans");
        final String pluginPrefix = "§e[§acBanking§e]";
        final String pluginHeader = "§f-+----------+-" + pluginPrefix + " §f-+----------+-";

        if (!banksEnabled) {
            player.sendMessage("§cBanks are not enabled on this server!");
            return true;
        }

        String ownersBankID = bankHandler.getBankIDByName(player.getName());
        if (args.length < 1) {
            if (ownersBankID == null) {
                sender.sendMessage(pluginHeader);
                sender.sendMessage("§7/bank open <name> <bankID>");
            } else {
                String bankHeader = "§f-+----------+-§e[§a" + ownersBankID + "§e]§f-+----------+-";
                sender.sendMessage(bankHeader);
                sender.sendMessage("§7/bank accounts");
                sender.sendMessage("§7/bank loans <approve/reject>");
                sender.sendMessage("§7/bank manage <name/owner> <new name/new owner>");
                sender.sendMessage("§7/bank manage assets <deposit/withdraw> <amount>");
                sender.sendMessage("§7/bank manage <interest_rate/growth_rate> <set/reset> <amount>");
                sender.sendMessage("§7/bank manage <maintenance_fee/new_account_fee> <set/reset> <amount>");
                sender.sendMessage("§7/bank manage <deposit_fee/withdrawal_fee> <set/reset> <amount>");
                sender.sendMessage("§7/bank close");
            }
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
                    player.sendMessage("§7*Open a new bank*");

                    return true;
                }
                if (bankHandler.getBankIDByName(player.getName()) != null) {
                    player.sendMessage("§cYou already own a bank.");
                    return true;
                }
                String newBankName = args[1];
                if (newBankName.length() > 12 || !newBankName.matches("[a-zA-Z0-9_]+")) {
                    player.sendMessage("§cBank name cannot exceed 12 characters and must be alphanumerical!");
                    return true;
                }
                if (bankHandler.bankNameExists(newBankName)) {
                    player.sendMessage("§cBank name already taken!");
                    return true;
                }
                String newBankID = args[2].toUpperCase();
                if (newBankID.length() != 4 || !newBankID.matches("[A-Z0-9]+")) {
                    player.sendMessage("§cBank ID must be a combination of four characters.");
                    return true;
                }
                if (bankHandler.bankExists(newBankID)) {
                    player.sendMessage("§cBank ID already taken!");
                    return true;
                }
                double newBankFee = plugin.getConfig().getDouble("bank-settings.new-bank-fee");
                double newBankAssets = plugin.getConfig().getDouble("bank-settings.new-bank-assets");
                if (accountHandler.hasFunds(player.getName(), newBankFee)) {
                    player.sendMessage("§cInsufficient funds! §7Fees to open a bank:\n- New bank fee: §c" + newBankFee + "\n- Initial assets deposit: §c" + newBankAssets);
                }
                bankHandler.createBank(newBankID, newBankName, player.getName());
                player.sendMessage(pluginPrefix + "§7 The bank, §a" + newBankName + "§7, has been successfully opened with ID: §a" + newBankID + "§7!");
                return true;
            case "close":
                String closeBankID = bankHandler.getBankIDByName(player.getName());
                if (!player.hasPermission("cbanking.bank.close")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                if (closeBankID == null) {
                    player.sendMessage("§cYou do not own a bank to close.");
                    return true;
                }
                if (closeRequests.contains(player.getUniqueId())) {
                    bankHandler.deleteBankAndTransferBalances(closeBankID);
                    player.sendMessage("§7Your bank has been closed and all player account balances have been transferred!");
                    closeRequests.remove(player.getUniqueId());
                } else {
                    closeRequests.add(player.getUniqueId());
                    player.sendMessage("§cAre you sure you want to close your bank? Retype the command within 30 seconds to confirm!");
                    Bukkit.getScheduler().runTaskLater(plugin, () -> closeRequests.remove(player.getUniqueId()), 600L); // 600 ticks = 30 seconds
                }
                return true;

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
                boolean foundAccounts = false;
                String accountsHeader = "§f-+------+------+- §e[§a" + accountsBankID + "§e] §f-+------+------+-";
                sender.sendMessage(accountsHeader);
                for (String playerUUID : playerDataHandler.getPlayerDataConfig().getKeys(false)) {
                    String balancePath = playerUUID + ".accounts." + accountsBankID;
                    if (playerDataHandler.getPlayerDataConfig().contains(balancePath)) {
                        double balance = playerDataHandler.getPlayerDataConfig().getDouble(balancePath);
                        String playerName = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName();
                        player.sendMessage("§7Player: §a" + playerName + "§7\n - Balance: §a" + currencySymbol + balance);
                        foundAccounts = true;
                    }
                }
                if (!foundAccounts) {
                    player.sendMessage("§cNo accounts are open with this bank!");
                }
                return true;
            case "loans":
                if (!player.hasPermission("cbanking.bank.loans")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                if (!loansEnabled) {
                    player.sendMessage("§cLoans are not enabled on this server!");
                    return true;
                }
                String loansBankID = bankHandler.getBankIDByName(player.getName());
                if (loansBankID == null) {
                    player.sendMessage("§cYou do not own a bank.");
                    return true;
                }

                player.sendMessage("§cLoan management is not implemented yet.");
                return true;

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
                    String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                    sender.sendMessage(bankHeader);
                    sender.sendMessage("§7/bank manage <name/owner> <new name/new owner>");
                    sender.sendMessage("§7/bank manage assets <deposit/withdraw> <amount>");
                    sender.sendMessage("§7/bank manage <interest_rate/growth_rate> <set/reset> <amount>");
                    sender.sendMessage("§7/bank manage <maintenance_fee/new_account_fee> <set/reset> <amount>");
                    sender.sendMessage("§7/bank manage <deposit_fee/withdrawal_fee> <set/reset> <amount>");
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
                            String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                            player.sendMessage(bankHeader);
                            player.sendMessage("§7Current bank name: §a" + bankHandler.getBankNameByID(manageBankID));
                        } else {
                            if (value.length() > 12 || !value.matches("[a-zA-Z0-9_]+")) {
                                player.sendMessage("§cBank name cannot exceed 12 characters and must be alphanumerical!");
                                return true;
                            } else {
                                bankHandler.setBankName(manageBankID, value);
                                player.sendMessage("§7Bank name has been updated to §a" + value + "§7.");
                            }
                        }
                        return true;
                    case "owner":
                        if (!player.hasPermission("cbanking.bank.manage.owner")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                            player.sendMessage(bankHeader);
                            player.sendMessage("§7Current bank owner: §a" + bankHandler.getBankOwnerByID(manageBankID));
                        } else {
                            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(value);
                            if (!targetPlayer.hasPlayedBefore()) {
                                player.sendMessage("§cPlayer not found!");
                                return true;
                            }
                            bankHandler.setBankOwner(manageBankID, value);
                            player.sendMessage("§7Bank owner has been updated to §a" + value + "§7.");
                        }
                        return true;

                    case "interest_rate":
                        if (!player.hasPermission("cbanking.bank.manage.interest")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (!loansEnabled) {
                            player.sendMessage("§cLoans are not enabled on this server!");
                            return true;
                        }
                        if (value == null) {
                            String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                            player.sendMessage(bankHeader);
                            player.sendMessage("§7Current interest rate: §a" + bankHandler.getInterestRate(manageBankID) + "%");
                        } else {
                            double amount = Double.parseDouble(value);
                            double minInterestRate = plugin.getConfig().getDouble("loan-settings.min-loan-interest-rate");
                            double maxInterestRate = plugin.getConfig().getDouble("loan-settings.max-loan-interest-rate");
                            if (amount >= minInterestRate && amount <= maxInterestRate) {
                                player.sendMessage("§7Interest rate successfully set to §a" + amount + "%§7.");
                                return true;
                            }
                            player.sendMessage("§cInvalid rate! §7Allowed range: §c" + minInterestRate + "% &7- §c" + maxInterestRate + "%&7.");
                        }
                        return true;

                    case "assets":
                        if (!player.hasPermission("cbanking.bank.manage.assets")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (args.length < 4) {
                            player.sendMessage("§7Usage: /bank manage assets <deposit/withdraw> <amount>");
                            player.sendMessage("§7*Manage your bank's assets pool*");

                            return true;
                        }
                        String action = args[2].toLowerCase();
                        double amount;
                        try {
                            amount = Double.parseDouble(args[3]);
                        } catch (NumberFormatException e) {
                            player.sendMessage("§cInvalid amount.");
                            return true;
                        }
                        double currentAssets = bankHandler.getAssets(manageBankID);
                        switch (action) {
                            case "deposit":
                                if (accountHandler.hasFunds(player.getName(), amount)) {
                                    accountHandler.withdraw(player.getName(), amount);
                                    bankHandler.setAssets(manageBankID, currentAssets + amount);
                                    player.sendMessage("§7Deposited §a" + currencySymbol + amount + "§7 to bank assets. New assets balance: §a" + currencySymbol + (currentAssets + amount) + "§7.");
                                } else {
                                    player.sendMessage("§cInsufficient funds!");
                                }
                                return true;
                            case "withdraw":
                                if (currentAssets >= amount) {
                                    bankHandler.setAssets(manageBankID, currentAssets - amount);
                                    accountHandler.deposit(player.getName(), amount);
                                    player.sendMessage("§7Removed §a" + currencySymbol + amount + "§7 from bank assets. New assets balance: §a" + currencySymbol + (currentAssets - amount) + "§7.");
                                } else {
                                    player.sendMessage("§cInsufficient assets!");
                                }
                                return true;
                            default:
                                player.sendMessage("§7Usage: /bank manage assets <deposit/withdraw> <amount>");
                                player.sendMessage("§7*Manage your bank's assets pool*");
                                return true;
                        }

                    case "maintenance_fee":
                        if (!player.hasPermission("cbanking.bank.manage.maintenance")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            String bankHeader = "§f-+----------+-§e[§a" + manageBankID + "§e]§f-+----------+-";
                            player.sendMessage(bankHeader);
                            if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("percentage")) {
                                player.sendMessage("§7Current maintenance fee rate: §a" + bankHandler.getMaintenanceFeeRate(manageBankID) + "%");
                                return true;
                            }
                            if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("flat")) {
                                player.sendMessage("§7Current maintenance fee rate: §a" + bankHandler.getMaintenanceFeeRate(manageBankID));
                                return true;
                            }
                        } else {
                            if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("percentage")) {
                                bankHandler.setMaintenanceFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Maintenance fee successfully set to §a" + value + "%§7.");
                                return true;
                            } else if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("flat")) {
                                bankHandler.setMaintenanceFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Maintenance fee successfully set to §a" + currencySymbol + value + "§7.");
                                return true;
                            } else {
                                bankHandler.setMaintenanceFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Maintenance fee successfully set to §a" + value + "§7.");
                                return true;
                            }
                        }

                    case "new_account_fee":
                        if (!player.hasPermission("cbanking.bank.manage.accountopeningfee")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                            player.sendMessage(bankHeader);
                            player.sendMessage("§7Current account opening fee: §a" + currencySymbol + bankHandler.getAccountOpeningFee(manageBankID));
                        } else {
                            bankHandler.setAccountOpeningFee(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Account opening fee successfully set to §a" + value + "§7.");
                        }
                        return true;

                    case "growth_rate":
                        if (!player.hasPermission("cbanking.bank.manage.accountgrowth")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                            player.sendMessage(bankHeader);
                            player.sendMessage("§7Current account growth rate: §a" + bankHandler.getAccountGrowthRate(manageBankID) + "%");
                        } else {
                            bankHandler.setAccountGrowthRate(manageBankID, Double.parseDouble(value));
                            player.sendMessage("§7Account growth rate successfully set to §a" + value + "§7%.");
                        }
                        return true;

                    case "deposit_fee":
                        if (!player.hasPermission("cbanking.bank.manage.depositfee")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                            player.sendMessage(bankHeader);
                            if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                player.sendMessage("§7Current deposit fee: §a" + bankHandler.getDepositFeeRate(manageBankID) + "%");
                            } else {
                                player.sendMessage("§7Current deposit fee: §a" + bankHandler.getDepositFeeRate(manageBankID));
                            }
                            return true;
                        } else {
                            if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                bankHandler.setDepositFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Deposit fee successfully set to §a" + value + "%§7.");
                                return true;
                            } else if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("flat")) {
                                bankHandler.setDepositFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Deposit fee successfully set to §a" + currencySymbol + value + "§7.");
                                return true;
                            } else {
                                bankHandler.setDepositFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Deposit fee successfully set to §a" + value + "§7.");
                                return true;
                            }
                        }

                    case "withdrawal_fee":
                        if (!player.hasPermission("cbanking.bank.manage.withdrawalfee")) {
                            player.sendMessage("§cYou do not have access to this command!");
                            return true;
                        }
                        if (value == null) {
                            String bankHeader = "§f-+------+------+-§e[§a" + manageBankID + "§e]§f-+------+------+-";
                            player.sendMessage(bankHeader);
                            if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                player.sendMessage("§7Current withdrawal fee: §a" + bankHandler.getWithdrawalFeeRate(manageBankID) + "%");
                            }
                            if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("flat")) {
                                player.sendMessage("§7Current withdrawal fee: §a" + bankHandler.getWithdrawalFeeRate(manageBankID));
                            }
                            return true;
                        } else {
                            if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                bankHandler.setWithdrawalFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Withdrawal fee successfully set to §a" + value + "%§7.");
                                return true;
                            } else if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("flat")) {
                                bankHandler.setWithdrawalFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Withdrawal fee successfully set to §a" + currencySymbol + value + "§7.");
                                return true;
                            } else {
                                bankHandler.setWithdrawalFeeRate(manageBankID, Double.parseDouble(value));
                                player.sendMessage("§7Withdrawal fee successfully set to §a" + value + "§7.");
                                return true;
                            }
                        }

                    default:
                        player.sendMessage("§cInvalid setting.");
                        return true;
                }

            default:
                String bankHeader = "§f------------------- §e[§a" + ownersBankID + "§e] §f-------------------";
                player.sendMessage(bankHeader);
                sender.sendMessage("§7/bank accounts");
                sender.sendMessage("§7/bank loans <approve/reject>");
                sender.sendMessage("§7/bank manage <name/owner> <new name/new owner>");
                sender.sendMessage("§7/bank manage assets <deposit/withdraw> <amount>");
                sender.sendMessage("§7/bank manage <interest_rate/growth_rate> <set/reset> <amount>");
                sender.sendMessage("§7/bank manage <maintenance_fee/new_account_fee> <set/reset> <amount>");
                sender.sendMessage("§7/bank manage <deposit_fee/withdrawal_fee> <set/reset> <amount>");
                sender.sendMessage("§7/bank close");
                return true;
        }
    }
}