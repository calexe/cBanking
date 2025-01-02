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
        if (plugin.getCommand("balance") != null) {
            plugin.getCommand("balance").setExecutor(new BalanceCommand(plugin));
        } else {
            plugin.getLogger().warning("Command 'balance' not found in plugin.yml!");
        }

        if (plugin.getCommand("pay") != null) {
            plugin.getCommand("pay").setExecutor(new PayCommand(plugin));
        } else {
            plugin.getLogger().warning("Command 'pay' not found in plugin.yml!");
        }
    }

    public void unregisterCommands() {
        unregisterCommand("balance");
        unregisterCommand("pay");
    }

    private void unregisterCommand(String commandName) {
        PluginCommand command = plugin.getCommand(commandName);
        if (command != null) {
            command.setExecutor(null);
        }
    }
}