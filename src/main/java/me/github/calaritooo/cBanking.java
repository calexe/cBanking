package me.github.calaritooo;

import me.github.calaritooo.commands.CommandHandler;
import me.github.calaritooo.listeners.EventHandler;
import me.github.calaritooo.utils.MessageHandler;
import me.github.calaritooo.utils.PlayerDataManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class cBanking extends JavaPlugin {

    private static final cBanking plugin = getInstance();
    private MessageHandler messageHandler;
    private PlayerDataManager playerDataManager;
    private CommandHandler commandHandler;
    private EventHandler eventHandler;

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
            if (!VaultHook.setupEconomy()) {
                ServerEconomy.register();
            } else {
                getLogger().severe("Vault is installed but no economy plugin was found!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } else {
            getLogger().severe("Vault plugin not found! Disabling cBanking...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands
        commandHandler = new CommandHandler(this);
        commandHandler.registerCommands();

        // Register the listeners
        eventHandler = new EventHandler(this);
        eventHandler.registerEvents();

        getLogger().info("cBanking has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("cBanking is disabling...");

        saveConfig();

        Bukkit.getScheduler().cancelTasks(this);

        if (eventHandler != null) {
            HandlerList.unregisterAll(eventHandler);
        }

        // Unregister commands
        if (commandHandler != null) {
            commandHandler.unregisterCommands();
        }

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                rsp.getProvider();
                getServer().getServicesManager().unregister(rsp.getProvider());
            }
        }

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
