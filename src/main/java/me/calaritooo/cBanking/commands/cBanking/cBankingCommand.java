package me.calaritooo.cBanking.commands.cBanking;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class cBankingCommand implements CommandExecutor, TabCompleter {

    private final List<String> subCommands = Arrays.asList("version", "player", "bank", "reload");

    private final cBanking plugin = cBankingCore.getPlugin();
    private final ConfigurationProvider config = cBankingCore.getConfigurationProvider();
    private final MessageProvider messages = cBankingCore.getMessageProvider();
    private final EconomyService eco = cBankingCore.getEconomyService();
    private final cBankingPlayerSubCommand playerSubCommand = new cBankingPlayerSubCommand(config, eco, messages);
    private final cBankingBankSubCommand bankSubCommand = new cBankingBankSubCommand(config, eco, messages);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            messages.send(sender, Message.USAGE_CBANKING);
            return true;
        }

        if (!sender.hasPermission("cbanking.admin")) {
            messages.send(sender, Message.ERROR_NO_PERMISSION);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "version" -> sender.sendMessage("§acBanking §7is currently running version: §6" + plugin.getPluginMeta().getVersion());
            case "reload" -> {
                sender.sendMessage("§6Reloading cBanking config.yml...");
                plugin.reloadConfig();
                sender.sendMessage("§aReloaded cBanking config.yml!");
            }
            case "player" -> playerSubCommand.handle(sender, Arrays.copyOfRange(args, 1, args.length));
            case "bank" -> bankSubCommand.handle(sender, Arrays.copyOfRange(args, 1, args.length));
            default -> messages.send(sender, Message.USAGE_CBANKING);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("cbanking.admin")) {
            return subCommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        } else if (args.length > 1 && sender.hasPermission("cbanking.admin")) {
            String sub = args[0].toLowerCase();
            if (sub.equals("player")) {
                return playerSubCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            } else if (sub.equals("bank")) {
                return bankSubCommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return Collections.emptyList();
    }
}
