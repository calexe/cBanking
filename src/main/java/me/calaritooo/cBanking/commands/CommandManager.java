package me.calaritooo.cBanking.commands;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.commands.balance.BalanceCommand;
import me.calaritooo.cBanking.commands.balance.BalanceTabCompleter;
import me.calaritooo.cBanking.commands.pay.PayCommand;
import me.calaritooo.cBanking.commands.pay.PayTabCompleter;

public class CommandManager {
    
    private final cBanking plugin;
    
    public CommandManager() {
        this.plugin = cBankingCore.getPlugin();
    }
    
    public void registerCommands() {
        plugin.getCommand("balance").setExecutor(new BalanceCommand());
        plugin.getCommand("pay").setExecutor(new PayCommand());
    }
    
    public void registerTabCompleters() {
        plugin.getCommand("pay").setTabCompleter(new PayTabCompleter());
        plugin.getCommand("balance").setTabCompleter(new BalanceTabCompleter());
    }

    public void unregisterCommands() {
        plugin.getCommand("balance").setExecutor(null);
        plugin.getCommand("pay").setExecutor(null);
    }

    public void unregisterTabCompleters() {
        plugin.getCommand("balance").setTabCompleter(null);
        plugin.getCommand("pay").setTabCompleter(null);
    }
}
