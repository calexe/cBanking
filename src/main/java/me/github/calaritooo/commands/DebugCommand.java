package me.github.calaritooo.commands;

import me.github.calaritooo.ServerEconomy;
import me.github.calaritooo.cBanking;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DebugCommand implements CommandExecutor {

    private final cBanking plugin;
    private final ServerEconomy economy;

    public DebugCommand(cBanking plugin) {
        this.plugin = plugin;
        this.economy = ServerEconomy.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        Bukkit.getLogger().info("Debug command triggered"); // Debugging line

        if (sender instanceof Player player) {
            // Debugging
            Bukkit.getLogger().info("Sender is a player: " + player.getName());

            String PERM = "cbanking.debug";
            if (!player.hasPermission(PERM)) {
                plugin.getMessageHandler().sendNoPermissionError(sender);
                return true;
            }

            // Example debug actions
            player.sendMessage("§7[Debug] Your current balance: " + economy.getBalance(player));
            player.sendMessage("§7[Debug] Your current location: " + player.getLocation());
            player.sendMessage("§7[Debug] Your current world: " + player.getWorld().getName());
        } else {
            // Console sender debug actions
            sender.sendMessage("[Debug] Server TPS: " + Bukkit.getTPS()[0]);
            sender.sendMessage("[Debug] Online players: " + Bukkit.getOnlinePlayers().size());

        }
        return true;
    }
}
