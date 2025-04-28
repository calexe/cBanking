package me.calaritooo.cBanking.commands.balance;

import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import me.calaritooo.cBanking.util.money.Money;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BalanceCommand implements CommandExecutor, TabCompleter {

    private final EconomyService eco = cBankingCore.getEconomyService();
    private final MessageProvider messages = cBankingCore.getMessageProvider();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                messages.send(sender, Message.ERROR_NOT_PLAYER);
                return true;
            }

            if (!sender.hasPermission("cbanking.balance")) {
                messages.send(sender, Message.ERROR_NO_PERMISSION);
                return true;
            }

            Money balance = eco.getBalance(player.getUniqueId());
            messages.send(player, Message.BALANCE_SELF,
                    "%amt%", balance.toString(),
                    "%player%", player.getName());
            return true;
        }

        if (args.length == 1) {
            if (!sender.hasPermission("cbanking.balance.others")) {
                messages.send(sender, Message.ERROR_NO_PERMISSION);
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!eco.exists(target.getUniqueId())) {
                messages.send(sender, Message.ERROR_INVALID_PLAYER);
                return true;
            }

            Money balance = eco.getBalance(target.getUniqueId());
            messages.send(sender, Message.BALANCE_OTHER,
                    "%amt%", balance.toString(),
                    "%player%", target.getName());
            return true;
        }

        messages.send(sender, Message.USAGE_BALANCE);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("cbanking.balance.others")) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        }

        return completions;
    }
}
