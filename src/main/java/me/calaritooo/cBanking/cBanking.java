package me.calaritooo.cBanking;

import me.calaritooo.cBanking.commands.*;
import me.calaritooo.cBanking.util.EconomyManager;
import me.calaritooo.cBanking.events.EventManager;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class cBanking extends JavaPlugin {

    private int autosaveTask = -1;

    private EconomyManager economyManager;
    private CommandManager commandManager;
    private EventManager eventManager;

    @Override
    public void onEnable() {

        economyManager = new EconomyManager(this);
        if (!economyManager.setup()) return;

        cBankingCore.initialize(this);

        economyManager.checkEconomyOverride();

        eventManager = new EventManager();
        eventManager.registerEvents();

        commandManager = new CommandManager();
        commandManager.registerCommands();
        commandManager.registerTabCompleters();

        int autosaveInternal = cBankingCore.getConfigurationProvider().get(ConfigurationOption.AUTOSAVE_INTERVAL);
        if (autosaveInternal > 0) {
            autosaveTask = 1;
            long autosaveTicks = autosaveInternal * 1200L;
            startAutosave(autosaveTicks);
        } else {
            getLogger().warning("Autosave is disabled! Saving will only occur on server start.");
        }

        getLogger().info("Successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");

        stopAutosave();
        cBankingCore.saveData();

        saveConfig();


        if (eventManager != null) {
            eventManager.unregisterEvents();
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
