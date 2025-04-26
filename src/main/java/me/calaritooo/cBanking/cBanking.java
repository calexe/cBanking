package me.calaritooo.cBanking;

import me.calaritooo.cBanking.commands.*;
import me.calaritooo.cBanking.util.EconomyManager;
import me.calaritooo.cBanking.listeners.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class cBanking extends JavaPlugin {

    private EconomyManager economyManager;
    private CommandManager commandManager;
    private EventHandler eventHandler;

    @Override
    public void onEnable() {
        getLogger().info("Enabling...");

        // Initialize vault hook and set provider
        economyManager = new EconomyManager(this);
        if (!economyManager.setup()) return;

        cBankingCore.initialize(this);

        economyManager.checkEconomyOverride();

        // Register events and commands
        eventHandler = new EventHandler();
        eventHandler.registerEvents();

        commandManager = new CommandManager();
        commandManager.registerCommands();
        commandManager.registerTabCompleters();

        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");

        saveConfig();


        if (eventHandler != null) {
            eventHandler.unregisterEvents();
        }

        if (commandManager != null) {
            commandManager.unregisterCommands();
            commandManager.unregisterTabCompleters();
        }

        if (economyManager != null) {
            economyManager.unregister();
        }

        Bukkit.getScheduler().cancelTasks(this);

        getLogger().info("Successfully disabled!");
    }
}
