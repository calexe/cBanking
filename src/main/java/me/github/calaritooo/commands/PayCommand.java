package me.github.calaritooo.commands;

import me.github.calaritooo.ServerEconomy;
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

import java.util.Map;

public class PayCommand implements CommandExecutor {

    private final cBanking plugin;
    private final ServerEconomy economy;
    private final TransactionLogger transactionLogger;
    private static final String PERMISSION = "cbanking.pay";

    public PayCommand(cBanking plugin) {
        this.plugin = plugin;
        this.economy = ServerEconomy.getInstance();
        boolean loggingEnabled = plugin.getConfig().getBoolean("plugin-settings.logging.enable-log-transactions");
        this.transactionLogger = new TransactionLogger(plugin.getDataFolder(), loggingEnabled);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof Player player) {

            if (!player.hasPermission(PERMISSION)) {
                plugin.getMessageHandler().sendNoPermissionError(sender);
                return true;
            }

            if (args.length < 1) {
                Map<String, String> placeholders0 = plugin.getMessageHandler().getFixedPlaceholders(0, 0, player.getName());
                Component payUsageMessage = plugin.getMessageHandler().getFormattedMessage("pay-command-usage", placeholders0);
                player.sendMessage(payUsageMessage);
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

            if (args.length > 2) {
                Map<String, String> placeholders = plugin.getMessageHandler().getFixedPlaceholders(0, 0, player.getName());
                Component payUsageMessage = plugin.getMessageHandler().getFormattedMessage("pay-command-usage", placeholders);
                player.sendMessage(payUsageMessage);
                return true;
            }

            double amt = Double.parseDouble(args[1]);
            double bal = economy.getBalance(player);
            if (amt > bal) {
                plugin.getMessageHandler().sendInsufficientFundsError(sender);
                return true;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                String transactionType = "payment";

                Map<String, String> placeholders = plugin.getMessageHandler().getFixedPlaceholders(bal, amt, player.getName());
                placeholders.put("{recipient}", recipient.getName());

                Component paymentSentMessage = plugin.getMessageHandler().getFormattedMessage("payment-sent", placeholders);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    economy.withdrawPlayer(player, amt);
                    economy.depositPlayer(recipient, amt);

                    player.sendMessage(paymentSentMessage);
                    if (recipient.isOnline()) {
                        Player onlineRecipient = recipient.getPlayer();
                        if (onlineRecipient != null) {
                            Component paymentReceivedMessage = plugin.getMessageHandler().getFormattedMessage("payment-received", placeholders);
                            onlineRecipient.sendMessage(paymentReceivedMessage);
                        }
                    }

                    if (plugin.getConfig().getBoolean("plugin-settings.logging.enable-log-transactions")) {
                        transactionLogger.logTransaction(player.getName(), recipient.getName(), amt, transactionType);
                    }
                });
            });
        }

        return true;
    }
}