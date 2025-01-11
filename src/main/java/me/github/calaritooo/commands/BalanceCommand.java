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

public class BalanceCommand implements CommandExecutor {

    private final cBanking plugin;
    private final AccountHandler accountHandler;

    public BalanceCommand(cBanking plugin) {
        this.plugin = plugin;
        this.accountHandler = plugin.getAccountHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        final String currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");
        final String balancePerm = "cbanking.balance";
        final String balanceOthersPerm = "cbanking.balance.others";

        if (sender instanceof Player player) {

            if (!player.hasPermission(balancePerm)) {
                player.sendMessage("§cYou do not have access to this command!");
                return true;
            }

            if (args.length == 0) {
                String playerName = player.getName();
                double playerBal = accountHandler.getBalance(playerName);

                player.sendMessage("§7Your balance: §a" + currencySymbol + playerBal);
                return true;
            }
            if (player.hasPermission(balanceOthersPerm)) {
                if (args.length == 1) {
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (targetPlayer.hasPlayedBefore()) {
                        String targetName = targetPlayer.getName();
                        double targetBalance = accountHandler.getBalance(targetName);
                        player.sendMessage("§7" + targetName + "'s balance: §a" + currencySymbol + targetBalance);
                        return true;
                    } else {
                        player.sendMessage("§cPlayer not found!");
                        return true;
                    }
                }
                player.sendMessage("§7Usage: /balance <player>");
                return true;
            }
            player.sendMessage("§cYou do not have access to this command!");
            return true;
        }

        if (args.length == 1) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (targetPlayer.hasPlayedBefore()) {
                String targetName = targetPlayer.getName();
                double targetBalance = accountHandler.getBalance(targetName);
                sender.sendMessage("§7" + targetName + "'s balance: §a" + currencySymbol + targetBalance);
            } else {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
        }
        sender.sendMessage("§7Usage: /balance <player>");
        return true;
    }
}