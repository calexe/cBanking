package me.github.calaritooo.commands;

import me.github.calaritooo.VaultHook;
import me.github.calaritooo.cBanking;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BalanceCommand implements CommandExecutor {

    private final cBanking plugin;

    public BalanceCommand(cBanking plugin) {
        this.plugin = plugin;
    }

    private static final String PERMISSION = "cbanking.balance";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        Player player = (Player) sender;

        if (!player.hasPermission(PERMISSION)) {
            plugin.getMessageHandler().sendNoPermissionError(sender);
            return true;
        }

        OfflinePlayer targetPlayer;

        if (args.length > 0) {
            targetPlayer = Bukkit.getOfflinePlayer(args[0]);
            if (!targetPlayer.hasPlayedBefore()) {
                plugin.getMessageHandler().sendInvalidPlayerError(sender);
                return true;
            }
        } else {
            targetPlayer = player;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            double balance = VaultHook.getBalance(targetPlayer);
            String currencySymbol = plugin.getConfig().getString("currency-symbol");

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{currency-symbol}", currencySymbol);
            placeholders.put("{balance}", String.format("%.2f", balance));

            Component message = plugin.getMessageHandler().getFormattedMessage("check-balance", placeholders);

            Bukkit.getScheduler().runTask(plugin, () -> player.sendMessage(message));
        });

        return true;
    }
}