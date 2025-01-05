package me.github.calaritooo.commands;

import me.github.calaritooo.accounts.AccountHandler;
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

    public CBankingCommand(cBanking plugin) {
        this.plugin = plugin;
        this.accountHandler = new AccountHandler(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        final String pluginPrefix = ("§e[§7acBanking§7e]");
        final String pluginHeader = "§f------------------- §acBanking §f-------------------";
        final boolean notifyPlayer = plugin.getConfig().getBoolean("plugin-settings.notifications.enable-admin-messages");
        final String currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");

        if (sender instanceof Player admin) {

            // Permission check ----------------------------------------------------------------------

            String permAdmin = "cbanking.admin";
            if (!admin.hasPermission(permAdmin)) {
                plugin.getMessageHandler().sendNoPermissionError(sender);
                return true;
            }

            // Command usage ----------------------------------------------------------------------

            if (args.length == 0) {
                sender.sendMessage("§7Usage: /cbanking <version | admin | debug>");
                return true;
            }

            // Checking arg 0 ----------------------------------------------------------------------

            if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
                String version = plugin.getDescription().getVersion();
                admin.sendMessage(pluginPrefix + "§7Running version: " + version);
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("admin")) {
                admin.sendMessage(pluginHeader);
                admin.sendMessage("§7/cbanking admin player <player>");
                admin.sendMessage("§7/cbanking admin bank <bank>");
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
                admin.sendMessage(pluginHeader);
                admin.sendMessage("§7Command check 1 passed!");
                return true;
            }

            // Player management > -------------------------------------------------------------------------------------------------------------------ADMIN PLAYER MANAGEMENT
            if (args.length >= 2 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("player")) {
                if (args.length >= 3) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                    if (!player.hasPlayedBefore()) {
                        admin.sendMessage("§Player not found!");
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
                                        admin.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    admin.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            admin.sendMessage("§7Usage: /cbanking admin player <player> balance set <amount>");
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
                                        admin.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    admin.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            admin.sendMessage("§7Usage: /cbanking admin player <player> balance give <amount>");
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
                                        admin.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    admin.sendMessage("§cInvalid amount.");
                                    return true;
                                }
                            }
                            admin.sendMessage("§7Usage: /cbanking admin player <player> balance take <amount>");
                            return true;
                        }
                        String playerName = player.getName();
                        double playerBal = accountHandler.getBalance(playerName);
                        admin.sendMessage("§7" + playerName + "'s balance: §a" + currencySymbol + playerBal);
                        return true;
                    }

                    // /cbanking admin player account ----------------------------------------------------------------------------------------- PLAYER ACCOUNT MANAGEMENT
                    if (args.length >= 4 && args[3].equalsIgnoreCase("account")) {
                        if (args.length >= 5) {
                            String bankID = args[4];
                            if (!accountHandler.hasAccount(player.getName(), bankID) || bankID == null) {
                                admin.sendMessage("§cThis player does not have an account with that bank!");
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
                                                onlinePlayer.sendMessage("§7Your account balance with §a[" + bankID + "]§7 has been set to §a" + currencySymbol + amt + "§7.");
                                                return true;
                                            }
                                        } else {
                                            admin.sendMessage("§cInvalid amount.");
                                            return true;
                                        }
                                        return true;
                                    } catch (NumberFormatException e) {
                                        admin.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                }
                                admin.sendMessage("§7Usage: /cbanking admin player <player> account <bankID> set <amount>");
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
                                                onlinePlayer.sendMessage("§a" + currencySymbol + amt + "§7has been added to your account balance with §a[" + bankID + "]§7.");
                                                return true;
                                            }
                                        } else {
                                            admin.sendMessage("§cInvalid amount.");
                                        }
                                        return true;
                                    } catch (NumberFormatException e) {
                                        admin.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                }
                                admin.sendMessage("§7Usage: /cbanking admin player <player> account <bankID> give <amount>");
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
                                                onlinePlayer.sendMessage("§a" + currencySymbol + amt + "§7has been taken from your account balance with §a[" + bankID + "]§7.");
                                                return true;
                                            }
                                        } else {
                                            admin.sendMessage("§cInvalid amount.");
                                        }
                                        return true;
                                    } catch (NumberFormatException e) {
                                        admin.sendMessage("§cInvalid amount.");
                                        return true;
                                    }
                                }
                                admin.sendMessage("§7Usage: /cbanking admin player <player> account <bankID> take <amount>");
                                return true;
                            }
                            String playerName = player.getName();
                            double accountBal = accountHandler.getBalance(playerName, bankID);
                            admin.sendMessage("§7" + playerName + "'s account balance with [§a" + bankID + "§7]: §a" + currencySymbol + accountBal);
                            return true;
                        }
                    }
                }

                // /cbanking admin player DIRECTORY
                admin.sendMessage(pluginHeader);
                admin.sendMessage("§7/cbanking admin player <player> balance <set | give | take> <amount>");
                admin.sendMessage("§7/cbanking admin player <player> account <bankID> <set | give | take> <amount>");
                admin.sendMessage("§7/cbanking admin player <player> loan <loan> <cancel | finish | credit> ");
                return true;
            }

            if (args.length >= 2 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("bank")) {
                admin.sendMessage(pluginHeader);
                admin.sendMessage("§7/cbanking admin bank <bankID> assets <set | give | take> <amount>");
                admin.sendMessage("§7/cbanking admin bank <bankID> interest <reset | set> <amount>");
                admin.sendMessage("§7/cbanking admin bank <bankID> growth <reset | set> <amount> ");
                admin.sendMessage("§7/cbanking admin bank <bankID> maintenance <reset | set> <amount> ");
                return true;
            }

                admin.sendMessage("§7Usage: /cbanking <version | admin | debug>");
            return true;

        } else {
            sender.sendMessage("Usage: /cbanking <version | admin | debug>");
        }
        return true;
    }
}
