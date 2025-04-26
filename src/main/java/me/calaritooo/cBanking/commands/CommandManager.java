package me.calaritooo.cBanking.commands;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.commands.balance.BalanceCommand;
import me.calaritooo.cBanking.commands.balance.BalanceTabCompleter;
import me.calaritooo.cBanking.commands.pay.PayCommand;
import me.calaritooo.cBanking.commands.pay.PayTabCompleter;

public class CommandManager {
    
    private static cBanking plugin;
    
    public CommandManager(cBanking pluginInstance) {
        plugin = pluginInstance;
        registerCommands();
        registerTabCompleters();
    }
    
    public static void registerCommands() {
        plugin.getCommand("balance").setExecutor(new BalanceCommand());
        plugin.getCommand("pay").setExecutor(new PayCommand());
    }
    
    public static void registerTabCompleters() {
        plugin.getCommand("pay").setTabCompleter(new PayTabCompleter());
        plugin.getCommand("balance").setTabCompleter(new BalanceTabCompleter());
    }

    public static void unregisterCommands() {
        plugin.getCommand("balance").setExecutor(null);
        plugin.getCommand("pay").setExecutor(null);
    }
}
