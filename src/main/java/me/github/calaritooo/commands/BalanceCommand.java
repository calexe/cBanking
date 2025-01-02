package me.github.calaritooo.commands;

import me.github.calaritooo.ServerEconomy;
import me.github.calaritooo.cBanking;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BalanceCommand implements CommandExecutor {

    private final cBanking plugin;
    private final ServerEconomy economy;

    public BalanceCommand(cBanking plugin) {
        this.plugin = plugin;
        this.economy = ServerEconomy.getInstance();
    }

    private static final String PERM = "cbanking.balance";
    private static final String PERM1 = "cbanking.balance.others";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        Player player = (Player) sender;

        if (!player.hasPermission(PERM)) {
            plugin.getMessageHandler().sendNoPermissionError(sender);
            return true;
        }

        OfflinePlayer targetPlayer;

        if (args.length > 0) {
            if (!player.hasPermission(PERM1)) {
                plugin.getMessageHandler().sendNoPermissionError(sender);
                return true;
            }

            targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!targetPlayer.hasPlayedBefore()) {
                plugin.getMessageHandler().sendInvalidPlayerError(sender);
                return true;
            }

            if (args.length > 1) {
                Map<String, String> placeholders0 = plugin.getMessageHandler().getFixedPlaceholders(0, 0, player.getName());
                Component payUsageMessage = plugin.getMessageHandler().getFormattedMessage("bal-command-usage", placeholders0);
                player.sendMessage(payUsageMessage);
                return true;
            }

        } else {
            targetPlayer = player;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            double bal = economy.getBalance(targetPlayer);
            double amt = 0;

            Map<String, String> placeholders = plugin.getMessageHandler().getFixedPlaceholders(bal, amt, targetPlayer.getName());

            Component message = plugin.getMessageHandler().getFormattedMessage("check-balance", placeholders);

            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(message));
        });

        return true;
    }
}