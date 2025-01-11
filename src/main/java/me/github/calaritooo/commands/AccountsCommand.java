package me.github.calaritooo.commands;

import me.github.calaritooo.cBanking;
import me.github.calaritooo.data.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AccountsCommand implements CommandExecutor {
    private final cBanking plugin;
    private final PlayerDataHandler playerDataHandler;

    public AccountsCommand(cBanking plugin) {
        this.plugin = plugin;
        this.playerDataHandler = plugin.getPlayerDataHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        String playerName;

        if (sender instanceof Player player) {
            if (args.length == 0) {
                if (!player.hasPermission("cbanking.accounts")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                playerName = player.getName();
            } else {
                if (!player.hasPermission("cbanking.accounts.other")) {
                    player.sendMessage("§cYou do not have access to this command!");
                    return true;
                }
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
                if (!targetPlayer.hasPlayedBefore()) {
                    player.sendMessage("§cPlayer not found!");
                    return true;
                }
                playerName = targetPlayer.getName();
            }
        } else {
            if (args.length != 1) {
                sender.sendMessage("§7Usage: /accounts <player>");
                return true;
            }
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!targetPlayer.hasPlayedBefore()) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
            playerName = targetPlayer.getName();
        }

        assert playerName != null;
        String playerID = Bukkit.getOfflinePlayer(playerName).getUniqueId().toString();
        Map<String, Double> accountBalances = new HashMap<>();

        if (playerDataHandler.getPlayerDataConfig().contains("players." + playerID + ".accounts")) {
            for (String bankID : playerDataHandler.getPlayerDataConfig().getConfigurationSection("players." + playerID + ".accounts").getKeys(false)) {
                double balance = playerDataHandler.getPlayerDataConfig().getDouble("players." + playerID + ".accounts." + bankID + ".balance");
                accountBalances.put(bankID, balance);
            }
        }

        List<Map.Entry<String, Double>> sortedBalances = accountBalances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        String prefix = "§e[§a" + playerName + "'s Accounts§e]";
        String header = "§f---------------- " + prefix + " §f----------------";
        sender.sendMessage(header);
        for (int i = 0; i < sortedBalances.size(); i++) {
            Map.Entry<String, Double> entry = sortedBalances.get(i);
            sender.sendMessage("§7" + (i + 1) + ". Bank ID: §a" + entry.getKey() + "\n§7  Balance: §a" + entry.getValue());
        }
        return true;
    }
}