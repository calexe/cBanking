package me.calaritooo.cBanking.commands.balance;

import me.calaritooo.cBanking.eco.EconomyService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BalanceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /balance — show own balance
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can check their own balance.");
                return true;
            }

            UUID uuid = player.getUniqueId();
            double balance = EconomyService.getBalance(uuid);

            sender.sendMessage("§eYour balance: §a$" + String.format("%.2f", balance));
            return true;
        }

        // /balance <player> — check another player's balance
        if (args.length == 1) {
            if (!sender.hasPermission("cbanking.balance.others")) {
                sender.sendMessage("§cYou don't have permission to check other players' balances.");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            UUID targetUUID = target.getUniqueId();

            if (!EconomyService.hasAccount(targetUUID)) {
                sender.sendMessage("§cThat player does not have an account.");
                return true;
            }

            double balance = EconomyService.getBalance(targetUUID);
            sender.sendMessage("§e" + target.getName() + "'s balance: §a$" + String.format("%.2f", balance));
            return true;
        }

        sender.sendMessage("§cUsage: /balance [player]");
        return true;
    }
}
