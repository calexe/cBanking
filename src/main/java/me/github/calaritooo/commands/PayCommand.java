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

public class PayCommand implements CommandExecutor {

    private final cBanking plugin;
    private final AccountHandler accountHandler;

    public PayCommand(cBanking plugin) {
        this.plugin = plugin;
        this.accountHandler = plugin.getAccountHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (!player.hasPermission("cbanking.pay")) {
            player.sendMessage("§cYou do not have access to this command!");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§7Usage: /pay <player> <amount>");
            player.sendMessage("§7*Send money from your personal balance*");
            return true;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!targetPlayer.hasPlayedBefore()) {
            player.sendMessage("§cPlayer not found!");
            return true;
        }

        try {
            BigDecimal amount = new BigDecimal(args[1]).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                player.sendMessage("§cInvalid amount.");
                return true;
            }

            double amountDouble = amount.doubleValue();
            if (accountHandler.getBalance(player.getName()) < amountDouble) {
                player.sendMessage("§cInsufficient funds!");
                return true;
            }

            accountHandler.withdraw(player.getName(), amountDouble);
            accountHandler.deposit(targetPlayer.getName(), amountDouble);
            String currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");

            player.sendMessage("§7You have sent §a" + currencySymbol + amount + "§7 to §a" + targetPlayer.getName() + "§7.");
            if (targetPlayer.isOnline()) {
                Player onlineTarget = targetPlayer.getPlayer();
                assert onlineTarget != null;
                onlineTarget.sendMessage("§7You have received §a" + currencySymbol + amount + "§7 from §a" + player.getName() + "§7.");
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount.");
        }

        return true;
    }
}