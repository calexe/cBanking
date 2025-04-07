package me.calaritooo.cBanking.commands;

import me.calaritooo.cBanking.bank.BankHandler;
import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BanksCommand implements CommandExecutor {

    private final cBanking plugin;
    private final BankHandler bankHandler;
    private final PlayerData playerData;

    public BanksCommand(cBanking plugin) {
        this.plugin = plugin;
        this.bankHandler = plugin.getBankHandler();
        this.playerData = plugin.getPlayerDataHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("cbanking.banks")) {
                player.sendMessage("§cYou do not have access to this command!");
                return true;
            }
        }

        Set<String> bankIDs = bankHandler.getBankIDs();
        if (bankIDs.isEmpty()) {
            sender.sendMessage("§cThere are currently no existing banks!");
            return true;
        }

        String prefix = "§e[§aList of Banks§e]";
        String header = "§f-+--------+-" + prefix + "§f-+--------+-";
        sender.sendMessage(header);

        int bankNumber = 1;
        for (String bankID : bankIDs) {
            int accountCount = 0;
            for (String playerID : playerData.getPlayerDataConfig().getConfigurationSection("players").getKeys(false)) {
                if (playerData.getPlayerDataConfig().contains("players." + playerID + ".accounts." + bankID)) {
                    accountCount++;
                }
            }
            if (accountCount == 1) {
                sender.sendMessage("§7" + bankNumber + ". §a" + bankID + "§7: " + accountCount + " account");
            } else {
                sender.sendMessage("§7" + bankNumber + ". §a" + bankID + "§7: " + accountCount + " accounts");
            }
            sender.sendMessage("§7   Owner: §7" + bankHandler.getBankOwnerByID(bankID));
            bankNumber++;
        }
        return true;
    }
}