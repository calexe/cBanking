package me.github.calaritooo.commands;

import me.github.calaritooo.cBanking;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler {

    private final JavaPlugin plugin;

    public CommandHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        plugin.getCommand("balance").setExecutor(new BalanceCommand((cBanking) plugin));
    }
}