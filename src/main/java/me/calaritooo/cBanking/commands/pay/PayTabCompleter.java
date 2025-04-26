package me.calaritooo.cBanking.commands.pay;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PayTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest online player names for the first argument
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        }

        return completions;
    }
}
