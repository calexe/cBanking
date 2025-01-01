package me.github.calaritooo.commands;

import me.github.calaritooo.VaultHook;
import me.github.calaritooo.cBanking;
import me.github.calaritooo.utils.TransactionLogger;
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

public class PayCommand implements CommandExecutor {

    private final cBanking plugin;
    private final TransactionLogger transactionLogger;
    private static final String PERMISSION = "cbanking.pay";

    public PayCommand(cBanking plugin) {
        this.plugin = plugin;
        boolean loggingEnabled = plugin.getConfig().getBoolean("plugin-settings.logging.enable-log-transactions");
        this.transactionLogger = new TransactionLogger(plugin.getDataFolder(), loggingEnabled);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        Player player = (Player) sender;

        if (!player.hasPermission(PERMISSION)) {
            plugin.getMessageHandler().sendNoPermissionError(sender);
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageHandler().sendInvalidPlayerError(sender);
            return true;
        }

        OfflinePlayer recipient = Bukkit.getOfflinePlayer(args[0]);

        if (!recipient.hasPlayedBefore()) {
            plugin.getMessageHandler().sendInvalidPlayerError(sender);
            return true;
        }

        if (args.length < 2) {
            plugin.getMessageHandler().sendInvalidAmountError(sender);
            return true;
        }

        if (!plugin.getMessageHandler().isValidAmount(args[1], sender)) {
            return true;
        }

        double amt = Double.parseDouble(args[1]);
        double playerBal = VaultHook.getBalance(player);
        if (amt > playerBal) {
            plugin.getMessageHandler().sendInsufficientFundsError(sender);
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String transactionType = "payment";
            String currencySymbol = plugin.getConfig().getString("currency-symbol");

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{currency-symbol}", currencySymbol);
            placeholders.put("{amount}", String.format("%.2f", amt));
            placeholders.put("{recipient}", recipient.getName());
            placeholders.put("{sender}", player.getName());

            Component sentMessage = plugin.getMessageHandler().getFormattedMessage("payment-sent", placeholders);

            Bukkit.getScheduler().runTask(plugin, () -> {
                VaultHook.withdraw(player, amt);
                VaultHook.deposit(recipient, amt);

                player.sendMessage(sentMessage);
                if (recipient.isOnline()) {
                    Player onlineRecipient = recipient.getPlayer();
                    if (onlineRecipient != null) {
                        Component receivedMessage = plugin.getMessageHandler().getFormattedMessage("payment-received", placeholders);
                        onlineRecipient.sendMessage(receivedMessage);
                    }
                }

                if (plugin.getConfig().getBoolean("plugin-settings.logging.enable-log-transactions")) {
                    transactionLogger.logTransaction(player.getName(), recipient.getName(), amt, transactionType);
                }
            });
        });

        return true;
    }
}