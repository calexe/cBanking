package me.calaritooo.cBanking.commands.pay;

import me.calaritooo.cBanking.eco.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§cUsage: /pay <player> <amount>");
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || target.getName() == null) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§cYou can't pay yourself.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount.");
            return true;
        }

        if (amount <= 0) {
            player.sendMessage("§cAmount must be greater than zero.");
            return true;
        }

        UUID senderUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        if (!EconomyService.hasFunds(senderUUID, amount)) {
            player.sendMessage("§cYou don't have enough funds.");
            return true;
        }

        boolean success = EconomyService.transfer(senderUUID, targetUUID, amount);
        if (!success) {
            player.sendMessage("§cTransaction failed.");
            return true;
        }

        player.sendMessage("§aYou paid §e" + target.getName() + " §a$" + String.format("%.2f", amount) + ".");
        if (target.isOnline()) {
            target.getPlayer().sendMessage("§aYou received §a$" + String.format("%.2f", amount) + " §afrom §e" + player.getName() + ".");
        }

        return true;
    }
}
