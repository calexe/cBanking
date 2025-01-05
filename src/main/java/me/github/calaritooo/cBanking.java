package me.github.calaritooo;

import me.github.calaritooo.commands.CBankingCommand;
import me.github.calaritooo.data.BankDataHandler;
import me.github.calaritooo.data.PlayerDataHandler;
import me.github.calaritooo.listeners.EventHandler;
import me.github.calaritooo.utils.MessageHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class cBanking extends JavaPlugin {

    private static cBanking plugin;
    private MessageHandler messageHandler;
    private EventHandler eventHandler;
    private VaultHook vaultHook;
    private BankDataHandler bankDataHandler;
    private PlayerDataHandler playerDataHandler;

    @Override
    public void onEnable() {
        getLogger().info("cBanking is initializing...");

        if (plugin != null) {
            getLogger().severe("Plugin instance already exists! Aborting initialization.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        plugin = this;

        saveDefaultConfig();
        FileConfiguration config = getConfig();

        messageHandler = new MessageHandler(this);

        playerDataHandler = new PlayerDataHandler(this);
        playerDataHandler.reloadPlayerDataConfig();
        getLogger().info("Successfully loaded playerdata.yml!");

        bankDataHandler = new BankDataHandler(this);
        bankDataHandler.reloadBanksConfig();
        getLogger().info("Successfully loaded banks.yml!");

        vaultHook = new VaultHook();
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            ServerEconomy serverEconomy = new ServerEconomy(this);
            serverEconomy.register();
        } else {
            getLogger().severe("Vault is not installed! Disabling cBanking.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        eventHandler = new EventHandler(this);
        eventHandler.registerEvents();

        getCommand("cbanking").setExecutor(new CBankingCommand(this));

        getLogger().info("cBanking has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("cBanking is disabling...");

        saveConfig();

        if (eventHandler != null) {
            HandlerList.unregisterAll(this);
        }

        // Unregister commands
        getCommand("cbanking").setExecutor(null);


        Bukkit.getScheduler().cancelTasks(this);

        RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registration != null) {
            Economy registeredEconomy = registration.getProvider();
            if (registeredEconomy instanceof ServerEconomy) {
                Bukkit.getServicesManager().unregister(registeredEconomy);
                plugin.getLogger().info("Successfully unregistered cBanking from Vault.");
            } else {
                plugin.getLogger().warning("Another Economy provider is registered. No action taken.");
            }
        } else {
            plugin.getLogger().warning("No Economy provider found to unregister.");
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

    public PlayerDataHandler getPlayerDataHandler() {
        return playerDataHandler;
    }

    public BankDataHandler getBankDataHandler() {
        return bankDataHandler;
    }

    public Economy getEconomy() {
        return vaultHook.getEconomy();
    }

    public static cBanking getInstance() {
        return plugin;
    }
}
