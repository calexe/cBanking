package me.github.calaritooo;

import me.github.calaritooo.commands.CommandHandler;
import me.github.calaritooo.listeners.EventHandler;
import me.github.calaritooo.utils.BalancesHandler;
import me.github.calaritooo.utils.MessageHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class cBanking extends JavaPlugin {

    private static final cBanking plugin = getInstance();
    private MessageHandler messageHandler;
    private CommandHandler commandHandler;
    private EventHandler eventHandler;
    private BalancesHandler balancesHandler;
    private VaultHook vaultHook;

    @Override
    public void onEnable() {
        getLogger().info("cBanking is initializing...");

        // Creating and/or load config.yml
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Load messages.yml
        messageHandler = new MessageHandler(this);

        // Initialize balances.yml
        balancesHandler = new BalancesHandler(getDataFolder());

        vaultHook = new VaultHook();

        // Check for and initialize Vault
        if (getServer().getPluginManager().getPlugin("Vault") != null)
            ServerEconomy.register();

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

        balancesHandler.saveBalancesFile();

        if (vaultHook != null && vaultHook.getEconomy() != null) {
            Bukkit.getServicesManager().unregister(vaultHook.getEconomy());
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

    public BalancesHandler getBalancesHandler() {
        return balancesHandler;
    }

    public Economy getEconomy() {
        return vaultHook.getEconomy();
    }

    public static cBanking getInstance() {
        return plugin;
    }
}
