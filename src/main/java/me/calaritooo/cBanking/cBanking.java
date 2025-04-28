package me.calaritooo.cBanking;

import me.calaritooo.cBanking.commands.*;
import me.calaritooo.cBanking.util.EconomyManager;
import me.calaritooo.cBanking.listeners.EventHandler;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class cBanking extends JavaPlugin {

    private int autosaveTask = 0;

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

        int autosaveInternal = cBankingCore.getConfigurationProvider().get(ConfigurationOption.AUTOSAVE_INTERVAL);
        if (autosaveInternal > 0) {
            long autosaveTicks = autosaveInternal * 1200L;
            autosaveTask = 1;
            startAutosave(autosaveTicks);
        }
        if (autosaveTask != 1) {
            getLogger().warning("Autosave is not enabled! Player and bank data files will only save upon restart.");
        }

        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");

        stopAutosave();
        cBankingCore.saveData();

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

    private void startAutosave(long interval) {
        autosaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            getLogger().info("Autosaving...");
            cBankingCore.saveData();
            getLogger().info("Autosave complete!");
        }, interval, interval);
    }

    private void stopAutosave() {
        if (autosaveTask == 1) {
            getServer().getScheduler().cancelTask(autosaveTask);
            getLogger().info("Autosave disabled for shutdown!");
        }
    }


}
