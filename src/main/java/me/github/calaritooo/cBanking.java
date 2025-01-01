package me.github.calaritooo;

import me.github.calaritooo.commands.CommandHandler;
import me.github.calaritooo.listeners.EventHandler;
import me.github.calaritooo.utils.MessageHandler;
import me.github.calaritooo.utils.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class cBanking extends JavaPlugin {

    private static final cBanking plugin = getInstance();
    private MessageHandler messageHandler;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        getLogger().info("cBanking is initializing...");

        // Creating and/or load config.yml
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Load messages.yml
        messageHandler = new MessageHandler(this);

        // Initialize PlayerDataManager
        playerDataManager = new PlayerDataManager(this);

        // Check for and initialize Vault
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            ServerEconomy.register();
        }

        // Register commands
        CommandHandler commandExecutor = new CommandHandler(plugin);
        commandExecutor.registerCommands();

        // Register the listeners
        EventHandler eventHandler = new EventHandler(plugin);
        eventHandler.registerEvents();

        getLogger().info("cBanking has been successfully enabled!");
    }
    @Override
    public void onDisable() {
        getLogger().info("cBanking is disabling...");

        saveConfig();

        Bukkit.getScheduler().cancelTasks(this);

        getLogger().info("cBanking has been successfully disabled!");
    }

    // GETTERS //

    public @NotNull FileConfiguration getConfig() {
        return super.getConfig();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public static cBanking getInstance() {
        return plugin;
    }
}
