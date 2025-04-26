package me.calaritooo.cBanking;

import me.calaritooo.cBanking.commands.*;
import me.calaritooo.cBanking.util.EconomyManager;
import me.calaritooo.cBanking.listeners.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class cBanking extends JavaPlugin {

    private EconomyManager economyManager;
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

        CommandManager.registerCommands();
        CommandManager.registerTabCompleters();

        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");

        saveConfig();

        CommandManager.unregisterCommands();

        if (eventHandler != null) {
            eventHandler.unregisterEvents();
        }

        Bukkit.getScheduler().cancelTasks(this);

        if (economyManager != null) {
            economyManager.unregister();
        }

        getLogger().info("Successfully disabled!");
    }
}
