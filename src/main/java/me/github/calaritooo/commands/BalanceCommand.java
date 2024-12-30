package me.github.calaritooo.commands;

import me.github.calaritooo.MessageHandler;
import me.github.calaritooo.cBanking;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BalanceCommand implements CommandExecutor {

    private final MessageHandler messageHandler;
    private final cBanking plugin;

    public BalanceCommand(cBanking plugin) {
        this.plugin = plugin;
        this.messageHandler = plugin.getMessageHandler();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Player targetPlayer = player;

        if (args.length > 0) {
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage("Player not found!");
                return true;
            }
        }

        double balance = plugin.getVaultHook().getBalance(targetPlayer);
        String currencySymbol = plugin.getConfig().getString("currency-symbol");

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{currency-symbol}", currencySymbol);
        placeholders.put("{balance}", String.format("%.2f", balance));

        Component message = messageHandler.getFormattedMessage("check-balance", placeholders);
        player.sendMessage(message);

        return true;
    }
}