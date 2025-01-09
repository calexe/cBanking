package me.github.calaritooo.commands;

import me.github.calaritooo.accounts.AccountHandler;
import me.github.calaritooo.banks.BankHandler;
import me.github.calaritooo.cBanking;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CBankingCommand implements CommandExecutor {

    private final cBanking plugin;
    private final AccountHandler accountHandler;
    private final BankHandler bankHandler;

    public CBankingCommand(cBanking plugin) {
        this.plugin = plugin;
        this.accountHandler = new AccountHandler(plugin);
        this.bankHandler = new BankHandler(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        final String pluginPrefix = ("§e[§acBanking§e] ");
        final String pluginHeader = "§f------------------- " + pluginPrefix + " §f-------------------";
        final boolean notifyPlayer = plugin.getConfig().getBoolean("plugin-settings.notifications.enable-admin-messages");
        final String currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");
        String permAdmin = "cbanking.admin";

        if (sender.hasPermission(permAdmin)) {

            if (args.length == 0) {
                sender.sendMessage(pluginHeader);
                sender.sendMessage("§7/balance <player>");
                sender.sendMessage("§7/pay <player> <amount>");
                sender.sendMessage("§7/accounts");
                sender.sendMessage("§7/account <bankID> <deposit/withdraw> <amount>");
                sender.sendMessage("§7/loans");
                sender.sendMessage("§7/loan <bankID> <amount> <days>");
                sender.sendMessage("§7/banks");
                sender.sendMessage("§7/bank <open/close/accounts/loans/manage>");
                sender.sendMessage("§7/cbanking <admin/version/debug>");
                return true;
            }

            // Checking arg 0 ----------------------------------------------------------------------

            if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
                String version = plugin.getPluginMeta().getVersion();
                sender.sendMessage(pluginPrefix + "§7Running version: §a" + version);
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("admin")) {
                sender.sendMessage(pluginHeader);
                sender.sendMessage("§7/cbanking admin player <player>");
                sender.sendMessage("§7/cbanking admin bank <bank>");
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
                sender.sendMessage(pluginHeader);
                sender.sendMessage("§7Command check 1 passed!");
                return true;
            }

            // Player management > -------------------------------------------------------------------------------------------------------------------ADMIN PLAYER MANAGEMENT
            if (args.length >= 2 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("player")) {
                if (args.length >= 3) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                    if (!player.hasPlayedBefore()) {
                        sender.sendMessage("§cPlayer not found!");
                        return true;
                    }
                    // /cbanking admin player <player> balance -------------------------------------------------------------------------------------- PLAYER BALANCE MANAGEMENT
                    if (args.length >= 4 && args[3].equalsIgnoreCase("balance")) {
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        String playerName = player.getName();
                                        double amt = amtBigDecimal.doubleValue();
                                        accountHandler.setBalance(playerName, amt);
                                        if (notifyPlayer && player.isOnline()) {
                                            Player onlinePlayer = player.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§7Your balance has been set to §a" + currencySymbol + amt + "§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... player <player> balance set <amount>");
                            return true;
                        }

                        // /cbanking admin player <player> balance give
                        if (args.length >= 5 && args[4].equalsIgnoreCase("give")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        String playerName = player.getName();
                                        double amt = amtBigDecimal.doubleValue();
                                        accountHandler.deposit(playerName, amt);
                                        if (notifyPlayer && player.isOnline()) {
                                            Player onlinePlayer = player.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§7You have received §a" + currencySymbol + amt + "§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... player <player> balance give <amount>");
                            return true;
                        }

                        // /cbanking admin player <player> balance take
                        if (args.length >= 5 && args[4].equalsIgnoreCase("take")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        String playerName = player.getName();
                                        double amt = amtBigDecimal.doubleValue();
                                        accountHandler.withdraw(playerName, amt);
                                        if (notifyPlayer && player.isOnline()) {
                                            Player onlinePlayer = player.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§a" + currencySymbol + amt + "§7 has been taken from your balance.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... player <player> balance take <amount>");
                            return true;
                        }
                        String playerName = player.getName();
                        double playerBal = accountHandler.getBalance(playerName);
                        sender.sendMessage("§7" + playerName + "'s balance: §a" + currencySymbol + playerBal);
                        return true;
                    }

                    // /cbanking admin player account ----------------------------------------------------------------------------------------- PLAYER ACCOUNT MANAGEMENT
                    if (args.length >= 4 && args[3].equalsIgnoreCase("account")) {
                        if (args.length >= 5) {
                            String bankID = args[4];
                            if (!accountHandler.hasAccount(player.getName(), bankID) || bankID == null) {
                                sender.sendMessage("§cThis player does not have an account with that bank!");
                                return true;
                            }
                            if (args.length >= 6 && args[5].equalsIgnoreCase("set")) {
                                if (args.length == 7) {
                                    try {
                                        BigDecimal amtBigDecimal = new BigDecimal(args[6]).setScale(2, RoundingMode.HALF_UP);
                                        if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                            String playerName = player.getName();
                                            double amt = amtBigDecimal.doubleValue();
                                            accountHandler.setBalance(playerName, bankID, amt);
                                            if (notifyPlayer && player.isOnline()) {
                                                Player onlinePlayer = player.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7Your account balance with §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                                return true;
                                            }
                                        } else {
                                            sender.sendMessage("§cInvalid amount.");
                                            return true;
                                        }
                                        return true;
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                }
                                sender.sendMessage("§7Usage: /... player <player> account <bankID> set <amount>");
                                return true;
                            }

                            // /cbanking admin player <player> account <account> give
                            if (args.length >= 6 && args[5].equalsIgnoreCase("give")) {
                                if (args.length == 7) {
                                    try {
                                        BigDecimal amtBigDecimal = new BigDecimal(args[6]).setScale(2, RoundingMode.HALF_UP);
                                        if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                            String playerName = player.getName();
                                            double amt = amtBigDecimal.doubleValue();
                                            accountHandler.deposit(playerName, bankID, amt);
                                            if (notifyPlayer && player.isOnline()) {
                                                Player onlinePlayer = player.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§a" + currencySymbol + amt + "§7has been added to your account balance with §e[§a" + bankID + "§e]§7.");
                                                return true;
                                            }
                                        } else {
                                            sender.sendMessage("§cInvalid amount.");
                                        }
                                        return true;
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                }
                                sender.sendMessage("§7Usage: /... player <player> account <bankID> give <amount>");
                                return true;
                            }

                            // /cbanking admin player <player> account <account> take
                            if (args.length >= 6 && args[5].equalsIgnoreCase("take")) {
                                if (args.length == 7) {
                                    try {
                                        BigDecimal amtBigDecimal = new BigDecimal(args[6]).setScale(2, RoundingMode.HALF_UP);
                                        if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                            String playerName = player.getName();
                                            double amt = amtBigDecimal.doubleValue();
                                            accountHandler.withdraw(playerName, bankID, amt);
                                            if (notifyPlayer && player.isOnline()) {
                                                Player onlinePlayer = player.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§c" + currencySymbol + amt + "§7has been taken from your account balance with §e[§a" + bankID + "§e]§7.");
                                                return true;
                                            }
                                        } else {
                                            sender.sendMessage("§cInvalid amount.");
                                        }
                                        return true;
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                }
                                sender.sendMessage("§7Usage: /... player <player> account <bankID> take <amount>");
                                return true;
                            }
                            String playerName = player.getName();
                            double accountBal = accountHandler.getBalance(playerName, bankID);
                            sender.sendMessage("§7" + playerName + "'s account balance with §e[§a" + bankID + "§e]§7: §a" + currencySymbol + accountBal);
                            return true;
                        }
                    }
                }

                // /cbanking admin player DIRECTORY
                sender.sendMessage(pluginHeader);
                sender.sendMessage("§7/... player <player> balance <set/give/take> <amount>");
                sender.sendMessage("§7/... player <player> account <bankID> <set/give/take> <amount>");
                sender.sendMessage("§7/... player <player> loan <loan> <cancel/finish/credit> ");
                return true;
            }

            if (args.length >= 2 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("bank")) {
                if (args.length >= 3) {
                    String bankID = args[2];
                    if (!bankHandler.bankExists(bankID)) {
                        sender.sendMessage("§cBank not found!");
                        return true;
                    }
                    // /cbanking admin bank <bank> assets -------------------------------------------------------------------------------------- BANK MANAGEMENT
                    if (args.length >= 4 && args[3].equalsIgnoreCase("assets")) {
                        double assetsBal = bankHandler.getAssets(bankID);
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        bankHandler.setAssets(bankID, amt);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        sender.sendMessage("§7The assets balance of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (notifyPlayer && bankOwner.isOnline()) {
                                            Player onlinePlayer = bankOwner.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§7The assets balance of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> assets set <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("give")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        double assets = bankHandler.getAssets(bankID);
                                        double newAssets = assets + amt;
                                        bankHandler.setAssets(bankID, newAssets);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (notifyPlayer && bankOwner.isOnline()) {
                                            Player onlinePlayer = bankOwner.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§a" + currencySymbol + amt + "§7has been added to the assets balance of §e[§a" + bankID + "§e]§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> assets give <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("take")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        double assets = bankHandler.getAssets(bankID);
                                        double newAssets = assets - amt;
                                        bankHandler.setAssets(bankID, newAssets);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (notifyPlayer && bankOwner.isOnline()) {
                                            Player onlinePlayer = bankOwner.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§c" + currencySymbol + amt + "§7has been removed from the assets balance of §e[§a" + bankID + "§e]§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> assets take <amount>");
                            return true;
                        }
                        sender.sendMessage("§7[§a" + bankID + "§7] has an assets balance of: §a" + currencySymbol + assetsBal);
                        return true;
                    }
                    if (args.length >= 4 && args[3].equalsIgnoreCase("interest")) {
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        bankHandler.setInterestRate(bankID, amt);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        sender.sendMessage("§7The interest rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (notifyPlayer && bankOwner.isOnline()) {
                                            Player onlinePlayer = bankOwner.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§7The interest rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> interest set <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("reset")) {
                            double defaultInterestRate = plugin.getConfig().getDouble("loan-settings.default-interest-rate");
                            bankHandler.setInterestRate(bankID, defaultInterestRate);
                            String ownerByID = bankHandler.getBankOwnerByID(bankID);
                            sender.sendMessage("§7The interest rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultInterestRate + "%§7.");
                            OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                            if (notifyPlayer && bankOwner.isOnline()) {
                                Player onlinePlayer = bankOwner.getPlayer();
                                assert onlinePlayer != null;
                                onlinePlayer.sendMessage("§7The interest rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultInterestRate + "%§7.");
                                return true;
                            }
                            return true;
                        }
                        sender.sendMessage("§7Usage: /... bank <bankID> interest reset <amount>");
                        return true;
                    }
                    if (args.length >= 4 && args[3].equalsIgnoreCase("growth")) {
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        bankHandler.setAccountGrowthRate(bankID, amt);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        sender.sendMessage("§7The account growth rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (notifyPlayer && bankOwner.isOnline()) {
                                            Player onlinePlayer = bankOwner.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§7The account growth rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> growth set <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("reset")) {
                            double defaultGrowthRate = plugin.getConfig().getDouble("bank-settings.default-acct-growth-rate");
                            bankHandler.setAccountGrowthRate(bankID, defaultGrowthRate);
                            String ownerByID = bankHandler.getBankOwnerByID(bankID);
                            sender.sendMessage("§7The account growth rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultGrowthRate + "%§7.");
                            OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                            if (notifyPlayer && bankOwner.isOnline()) {
                                Player onlinePlayer = bankOwner.getPlayer();
                                assert onlinePlayer != null;
                                onlinePlayer.sendMessage("§7The account growth rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultGrowthRate + "%§7.");
                                return true;
                            }
                            return true;
                        }
                        sender.sendMessage("§7Usage: /... bank <bankID> growth reset <amount>");
                        return true;
                    }
                    if (args.length >= 4 && args[3].equalsIgnoreCase("newaccountfee")) {
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        bankHandler.setAccountOpeningFee(bankID, amt);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        sender.sendMessage("§7The account opening fee of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (notifyPlayer && bankOwner.isOnline()) {
                                            Player onlinePlayer = bankOwner.getPlayer();
                                            assert onlinePlayer != null;
                                            onlinePlayer.sendMessage("§7The account opening fee of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> newaccountfee set <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("reset")) {
                            double defaultOpeningFee = plugin.getConfig().getDouble("bank-settings.default-acct-opening-fee");
                            bankHandler.setAccountOpeningFee(bankID, defaultOpeningFee);
                            String ownerByID = bankHandler.getBankOwnerByID(bankID);
                            sender.sendMessage("§7The account opening fee of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultOpeningFee + "§7.");
                            OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                            if (notifyPlayer && bankOwner.isOnline()) {
                                Player onlinePlayer = bankOwner.getPlayer();
                                assert onlinePlayer != null;
                                onlinePlayer.sendMessage("§7The account opening fee of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultOpeningFee + "§7.");
                                return true;
                            }
                            return true;
                        }
                        sender.sendMessage("§7Usage: /... bank <bankID> newaccountfee reset <amount>");
                        return true;
                    }
                    if (args.length >= 4 && args[3].equalsIgnoreCase("maintenance")) {
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        bankHandler.setMaintenanceFeeRate(bankID, amt);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("flat")) {
                                            sender.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                            }
                                            return true;
                                        } else if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("percentage")) {
                                            sender.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            }
                                            return true;
                                        } else {
                                            sender.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                            }
                                            return true;
                                        }
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> maintenance set <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("reset")) {
                            double defaultMaintenanceFee = plugin.getConfig().getDouble("bank-settings.default-maintenance-fee-rate");
                            bankHandler.setMaintenanceFeeRate(bankID, defaultMaintenanceFee);
                            String ownerByID = bankHandler.getBankOwnerByID(bankID);
                            OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                            if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("flat")) {
                                sender.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + defaultMaintenanceFee + "§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + defaultMaintenanceFee + "§7.");
                                }
                            } else if (plugin.getConfig().getString("bank-settings.maintenance-fee-type").equalsIgnoreCase("percentage")) {
                                sender.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultMaintenanceFee + "%§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultMaintenanceFee + "%§7.");
                                }
                            } else {
                                sender.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultMaintenanceFee + "§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The maintenance fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultMaintenanceFee + "§7.");
                                }
                            }
                            return true;
                        }
                        sender.sendMessage("§7Usage: /... bank <bankID> maintenance reset <amount>");
                        return true;
                    }
                    if (args.length >= 4 && args[3].equalsIgnoreCase("withdrawalfee")) {
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        bankHandler.setWithdrawalFeeRate(bankID, amt);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("flat")) {
                                            sender.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                            }
                                        } else if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                            sender.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            }
                                        } else {
                                            sender.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                            }
                                        }
                                        return true;
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> withdrawalfee set <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("reset")) {
                            double defaultWithdrawalFee = plugin.getConfig().getDouble("bank-settings.default-withdrawal-fee-rate");
                            bankHandler.setWithdrawalFeeRate(bankID, defaultWithdrawalFee);
                            String ownerByID = bankHandler.getBankOwnerByID(bankID);
                            OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                            if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("flat")) {
                                sender.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + defaultWithdrawalFee + "§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + defaultWithdrawalFee + "§7.");
                                }
                            } else if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                sender.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultWithdrawalFee + "%§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultWithdrawalFee + "%§7.");
                                }
                            } else {
                                sender.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultWithdrawalFee + "§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The withdrawal fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultWithdrawalFee + "§7.");
                                }
                            }
                            return true;
                        }
                        sender.sendMessage("§7Usage: /... bank <bankID> withdrawalfee reset <amount>");
                        return true;
                    }

                    if (args.length >= 4 && args[3].equalsIgnoreCase("depositfee")) {
                        if (args.length >= 5 && args[4].equalsIgnoreCase("set")) {
                            if (args.length == 6) {
                                try {
                                    BigDecimal amtBigDecimal = new BigDecimal(args[5]).setScale(2, RoundingMode.HALF_UP);
                                    if (amtBigDecimal.compareTo(BigDecimal.ZERO) > 0) {
                                        double amt = amtBigDecimal.doubleValue();
                                        bankHandler.setDepositFeeRate(bankID, amt);
                                        String ownerByID = bankHandler.getBankOwnerByID(bankID);
                                        OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                                        if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("flat")) {
                                            sender.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                            }
                                        } else if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                            sender.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "%§7.");
                                            }
                                        } else {
                                            sender.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                            if (notifyPlayer && bankOwner.isOnline()) {
                                                Player onlinePlayer = bankOwner.getPlayer();
                                                assert onlinePlayer != null;
                                                onlinePlayer.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + amt + "§7.");
                                            }
                                        }
                                        return true;
                                    } else {
                                        sender.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            sender.sendMessage("§7Usage: /... bank <bankID> depositfee set <amount>");
                            return true;
                        }
                        if (args.length >= 5 && args[4].equalsIgnoreCase("reset")) {
                            double defaultDepositFee = plugin.getConfig().getDouble("bank-settings.default-deposit-fee-rate");
                            bankHandler.setDepositFeeRate(bankID, defaultDepositFee);
                            String ownerByID = bankHandler.getBankOwnerByID(bankID);
                            OfflinePlayer bankOwner = Bukkit.getOfflinePlayer(ownerByID);
                            if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("flat")) {
                                sender.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + defaultDepositFee + "§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + currencySymbol + defaultDepositFee + "§7.");
                                }
                            } else if (plugin.getConfig().getString("bank-settings.transaction-fee-type").equalsIgnoreCase("percentage")) {
                                sender.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultDepositFee + "%§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultDepositFee + "%§7.");
                                }
                            } else {
                                sender.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultDepositFee + "§7.");
                                if (notifyPlayer && bankOwner.isOnline()) {
                                    Player onlinePlayer = bankOwner.getPlayer();
                                    assert onlinePlayer != null;
                                    onlinePlayer.sendMessage("§7The deposit fee rate of §e[§a" + bankID + "§e]§7 has been set to §a" + defaultDepositFee + "§7.");
                                }
                            }
                            return true;
                        }
                        sender.sendMessage("§7Usage: /... bank <bankID> depositfee reset <amount>");
                        return true;
                    }
                    if (args.length >= 4 && args[3].equalsIgnoreCase("close")) {
                        sender.sendMessage("§7[&a" + bankID + "]§7has been §cpermanently closed §7as well as relative player accounts.");
                        bankHandler.deleteBankAndTransferBalances(bankID);
                        return true;
                    }
                }
            }

            // /cbanking admin bank DIRECTORY
            sender.sendMessage(pluginHeader);
            sender.sendMessage("§7/... bank <bankID> assets <set/give/take> <amount>");
            sender.sendMessage("§7/... bank <bankID> interest <reset/set> <amount>");
            sender.sendMessage("§7/... bank <bankID> growth <reset/set> <amount> ");
            sender.sendMessage("§7/... bank <bankID> newaccountfee <reset/set> <amount> ");
            sender.sendMessage("§7/... bank <bankID> maintenance <reset/set> <amount> ");
            sender.sendMessage("§7/... bank <bankID> withdrawalfee <reset/set> <amount> ");
            sender.sendMessage("§7/... bank <bankID> depositfee <reset/set> <amount> ");
            sender.sendMessage("§7/... bank <bankID> close");

        } else {
            plugin.getMessageHandler().sendNoPermissionError(sender);
        }
        return true;
    }
}
