package me.calaritooo.cBanking.commands;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.commands.balance.BalanceCommand;
import me.calaritooo.cBanking.commands.balance.BalanceTabCompleter;
import me.calaritooo.cBanking.commands.pay.PayCommand;
import me.calaritooo.cBanking.commands.pay.PayTabCompleter;

public class CommandHandler {
    
    private static cBanking plugin;
    
    public CommandHandler(cBanking pluginInstance) {
        plugin = pluginInstance;
        registerCommands();
        registerTabCompleters();
    }
    
    public static void registerCommands() {
        plugin.getCommand("cbanking").setExecutor(new CBankingCommand());
        plugin.getCommand("balance").setExecutor(new BalanceCommand());
        plugin.getCommand("pay").setExecutor(new PayCommand());
        plugin.getCommand("account").setExecutor(new AccountCommand());
        plugin.getCommand("accounts").setExecutor(new AccountsCommand());
        plugin.getCommand("banks").setExecutor(new BanksCommand());
        plugin.getCommand("bank").setExecutor(new BankCommand());
    }
    
    public static void registerTabCompleters() {
        plugin.getCommand("pay").setTabCompleter(new PayTabCompleter());
        plugin.getCommand("balance").setTabCompleter(new BalanceTabCompleter());
    }

    public static void unregisterCommands() {
        plugin.getCommand("cbanking").setExecutor(null);
        plugin.getCommand("balance").setExecutor(null);
        plugin.getCommand("pay").setExecutor(null);
        plugin.getCommand("account").setExecutor(null);
        plugin.getCommand("accounts").setExecutor(null);
        plugin.getCommand("banks").setExecutor(null);
        plugin.getCommand("bank").setExecutor(null);
    }
}
