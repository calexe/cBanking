package me.calaritooo.cBanking.commands.balance;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class BalanceTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("cbanking.balance.others")) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        }

        return completions;
    }
}
