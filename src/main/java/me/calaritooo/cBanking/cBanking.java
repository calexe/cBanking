package me.calaritooo.cBanking;

import me.calaritooo.cBanking.accounts.AccountHandler;
import me.calaritooo.cBanking.banks.BankHandler;
import me.calaritooo.cBanking.commands.*;
import me.calaritooo.cBanking.data.BankDataHandler;
import me.calaritooo.cBanking.data.PlayerDataHandler;
import me.calaritooo.cBanking.listeners.EventHandler;
import me.calaritooo.cBanking.utils.MessageHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class cBanking extends JavaPlugin {

    private static cBanking plugin;
    private ServerEconomy economy;
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

        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        economy = new ServerEconomy(this);
        getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Highest);
        getLogger().info("Economy provider registered.");


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

        getServer().getServicesManager().unregister(Economy.class, economy);

        getLogger().info("cBanking has been successfully disabled!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            return rsp != null;
        }
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
        bankHandler = new BankHandler(this);
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
        return economy;
    }
    public static cBanking getInstance() {
        return plugin;
    }
}
