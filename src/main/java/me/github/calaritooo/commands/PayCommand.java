package me.github.calaritooo.commands;

import me.github.calaritooo.ServerEconomy;
import me.github.calaritooo.cBanking;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class PayCommand implements CommandExecutor {

    private final cBanking plugin;
    private final ServerEconomy economy;

    public PayCommand(cBanking plugin) {
        this.plugin = plugin;
        this.economy = ServerEconomy.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§7Usage: /pay <player> <amount>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            player.sendMessage("§cPlayer not found!");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount.");
            return true;
        }

        if (economy.getBalance(player) < amount) {
            player.sendMessage("§cThe specified amount exceeds your available balance!");
            return true;
        }

        final String currencySymbol = plugin.getConfig().getString("economy-settings.currency-symbol");
        economy.withdrawPlayer(player, amount);
        economy.depositPlayer(target, amount);

        player.sendMessage("§7You sent §a" + currencySymbol + amount + "§7 to §a" + target.getName() + "§7.");
        if (target.isOnline()) {
            Player onlinePlayer = target.getPlayer();
            if (onlinePlayer != null) {
                onlinePlayer.sendMessage("§7You have received §a" + currencySymbol + amount + "§7 from §a" + player.getName() + "§7.");
            }
        }
        return true;
    }
}