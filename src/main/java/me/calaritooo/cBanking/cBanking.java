package me.calaritooo.cBanking;

import me.calaritooo.cBanking.bank.BankAccount;
import me.calaritooo.cBanking.commands.*;
import me.calaritooo.cBanking.eco.EconomyManager;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.listeners.EventHandler;
import me.calaritooo.cBanking.player.PlayerAccount;
import me.calaritooo.cBanking.player.PlayerData;
import me.calaritooo.cBanking.bank.BankData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class cBanking extends JavaPlugin {

    private EconomyManager economyManager;
    private PlayerAccount playerAccount;
    private PlayerData playerData;
    private BankAccount bankAccount;
    private BankData bankData;
    private EventHandler eventHandler;

    @Override
    public void onEnable() {
        getLogger().info("cBanking is initializing...");

        economyManager = new EconomyManager(this);
        if (!economyManager.setup()) return;

        economyManager.checkEconomyOverride();

        // Setup internal economy API
        EconomyService.init(this);

        // Init handlers and configs
        initializeHandlers();

        // Register events and commands
        eventHandler = new EventHandler(this);
        eventHandler.registerEvents();

        getLogger().info("cBanking has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("cBanking is disabling...");

        saveConfig();

        CommandHandler.unregisterCommands();

        if (eventHandler != null) {
            HandlerList.unregisterAll(this);
        }

        Bukkit.getScheduler().cancelTasks(this);

        if (economyManager != null) {
            economyManager.unregister();
        }

        getLogger().info("cBanking has been successfully disabled!");
    }

    private void initializeHandlers() {
        playerData = new PlayerData(this);
        getLogger().info("Loaded playerdata.yml");

        bankData = new BankData(this);
        getLogger().info("Loaded banks.yml");

        playerAccount = new PlayerAccount(this);
        bankAccount = new BankAccount(this);

        CommandHandler.registerCommands();
        CommandHandler.registerTabCompleters();
    }

    // GETTERS

    @Override
    public @NotNull FileConfiguration getConfig() {
        return super.getConfig();
    }

    public PlayerAccount getPlayerAccount() {
        return playerAccount;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public BankData getBankData() {
        return bankData;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }
}
