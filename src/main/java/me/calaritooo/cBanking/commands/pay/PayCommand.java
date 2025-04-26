package me.calaritooo.cBanking.commands.pay;

import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PayCommand implements CommandExecutor {

    private final EconomyService eco = cBankingCore.getEconomyService();
    private final MessageProvider messages = cBankingCore.getMessageProvider();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            messages.send(sender, Message.ERROR_NOT_PLAYER);
            return true;
        }

        if (!sender.hasPermission("cbanking.pay")) {
            messages.send(sender, Message.ERROR_NO_PERMISSION);
            return true;
        }

        if (args.length != 2) {
            messages.send(player, Message.USAGE_PAY_CMD);
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!eco.exists(target.getUniqueId())) {
            messages.send(player, Message.ERROR_INVALID_PLAYER);
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            messages.send(player, Message.PAY_SAME_PLAYER);
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            messages.send(player, Message.ERROR_INVALID_AMOUNT);
            return true;
        }

        if (amount <= 0) {
            messages.send(player, Message.ERROR_INVALID_AMOUNT);
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();

        if (!eco.hasFunds(playerUUID, amount)) {
            messages.send(player, Message.TRANSACTION_FAIL_INSUFFICIENT_FUNDS);
            return true;
        }

        boolean success = eco.transfer(playerUUID, targetUUID, amount);
        if (!success) {
            messages.send(player, Message.PAY_FAILED);
            return true;
        }

        messages.send(player, Message.PAY_SENT,
                "%amt%", String.valueOf(amount),
                "%recipient%", target.getName());
        if (target.isOnline()) {
            messages.send(Objects.requireNonNull(target.getPlayer()), Message.PAY_RECEIVED,
                    "%amt%", String.valueOf(amount),
                    "%player%", player.getName());
        }
        return true;
    }
}
