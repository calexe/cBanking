package me.github.calaritooo.commands;

import me.github.calaritooo.cBanking;
import org.bukkit.command.PluginCommand;

import java.util.Objects;

public class CommandHandler {

    private final cBanking plugin;

    public CommandHandler(cBanking plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        Objects.requireNonNull(plugin.getCommand("balance")).setExecutor(new BalanceCommand(plugin));
        Objects.requireNonNull(plugin.getCommand("pay")).setExecutor(new PayCommand(plugin));
    }

    public void unregisterCommands() {
        PluginCommand balanceCommand = plugin.getCommand("balance");
        if (balanceCommand != null) {
            balanceCommand.setExecutor(null);
        }

        PluginCommand payCommand = plugin.getCommand("pay");
        if (payCommand != null) {
            payCommand.setExecutor(null);
        }
    }
}