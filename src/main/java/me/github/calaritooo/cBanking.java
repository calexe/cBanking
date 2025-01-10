package me.github.calaritooo;

import me.github.calaritooo.accounts.AccountHandler;
import me.github.calaritooo.banks.BankHandler;
import me.github.calaritooo.commands.*;
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
    private VaultHook vaultHook;
    private MessageHandler messageHandler;
    private AccountHandler accountHandler;
    private BankHandler bankHandler;
    private EventHandler eventHandler;
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

        initializeHandlers();


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
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("account").setExecutor(new AccountCommand(this));
        getCommand("accounts").setExecutor(new AccountsCommand(this));
        getCommand("banks").setExecutor(new BanksCommand(this));
        getCommand("bank").setExecutor(new BankCommand(this));

        getLogger().info("cBanking has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("cBanking is disabling...");

        saveConfig();

        if (eventHandler != null) {
            HandlerList.unregisterAll(this);
        }

        getCommand("cbanking").setExecutor(null);
        getCommand("balance").setExecutor(null);
        getCommand("pay").setExecutor(null);
        getCommand("account").setExecutor(null);
        getCommand("accounts").setExecutor(null);
        getCommand("banks").setExecutor(null);
        getCommand("bank").setExecutor(null);

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

    private void initializeHandlers() {
        messageHandler = new MessageHandler(this);

        playerDataHandler = new PlayerDataHandler(this);
        playerDataHandler.reloadPlayerDataConfig();
        getLogger().info("Successfully loaded playerdata.yml!");

        bankDataHandler = new BankDataHandler(this);
        bankDataHandler.reloadBanksConfig();
        getLogger().info("Successfully loaded banks.yml!");

        accountHandler = new AccountHandler(this);
        accountHandler.initializeHandlers(playerDataHandler, bankDataHandler);

        bankHandler = new BankHandler(this);
        bankHandler.initializeHandlers(bankDataHandler);
    }

    // GETTERS //

    public @NotNull FileConfiguration getConfig() {
        return super.getConfig();
    }
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
    public AccountHandler getAccountHandler() {
        return accountHandler;
    }
    public BankHandler getBankHandler() {
        return bankHandler;
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
