package me.calaritooo.cBanking.commands;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.commands.balance.BalanceCommand;
import me.calaritooo.cBanking.commands.cBanking.cBankingCommand;
import me.calaritooo.cBanking.commands.pay.PayCommand;

public class CommandManager {
    
    private final cBanking plugin;
    private final BalanceCommand balanceCommand;
    private final PayCommand payCommand;
    private final cBankingCommand cBankingCommand;
    
    public CommandManager() {
        this.plugin = cBankingCore.getPlugin();
        this.balanceCommand = new BalanceCommand();
        this.payCommand = new PayCommand();
        this.cBankingCommand = new cBankingCommand();
    }
    
    public void registerCommands() {
        plugin.getCommand("balance").setExecutor(balanceCommand);
        plugin.getCommand("pay").setExecutor(payCommand);
        plugin.getCommand("cbanking").setExecutor(cBankingCommand);
    }
    
    public void registerTabCompleters() {
        plugin.getCommand("balance").setTabCompleter(balanceCommand);
        plugin.getCommand("pay").setTabCompleter(payCommand);
        plugin.getCommand("cbanking").setTabCompleter(cBankingCommand);
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
