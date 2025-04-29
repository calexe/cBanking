package me.calaritooo.cBanking;

import me.calaritooo.cBanking.bank.BankAccount;
import me.calaritooo.cBanking.bank.BankData;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.ResourceUpdater;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.money.MoneyProvider;
import me.calaritooo.cBanking.player.PlayerAccount;
import me.calaritooo.cBanking.player.PlayerData;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class cBankingCore {

    private static cBanking plugin;
    private static FileConfiguration config;
    private static FileConfiguration messages;
    private static ConfigurationProvider configurationProvider;
    private static MessageProvider messageProvider;
    private static MoneyProvider moneyProvider;
    private static PlayerData playerData;
    private static BankData bankData;
    private static EconomyService economyService;

    private cBankingCore() {
    }

    public static void initialize(cBanking pluginInstance) {
        plugin = pluginInstance;

        plugin.saveDefaultConfig();

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (configFile.exists()) {
            ResourceUpdater.update(plugin, "config.yml", ConfigurationOption.class);
        }
        if (messagesFile.exists()) {
            ResourceUpdater.update(plugin, "messages.yml", Message.class);
        }

        config = plugin.getConfig();
        configurationProvider = new ConfigurationProvider(config);

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        messageProvider = new MessageProvider(messages, configurationProvider);

        moneyProvider = new MoneyProvider(configurationProvider);

        playerData = new PlayerData();
        plugin.getLogger().info("Loaded player data!");

        bankData = new BankData();
        plugin.getLogger().info("Loaded bank data");

        PlayerAccount playerAccount = new PlayerAccount(playerData);
        BankAccount bankAccount = new BankAccount(bankData);

        economyService = new EconomyService(playerAccount, playerData, bankAccount, bankData);
        plugin.getLogger().info("Loaded economy service!");
    }

    public static void saveData() {
        playerData.saveAll();
        bankData.saveAll();
        configurationProvider.saveIfModified();
    }

    public static void reload() {
        plugin.getLogger().info("Reloading...");
        plugin.reloadConfig();

        ResourceUpdater.update(plugin, "config.yml", ConfigurationOption.class);
        ResourceUpdater.update(plugin, "messages.yml", Message.class);

        config = plugin.getConfig();
        configurationProvider = new ConfigurationProvider(config);

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        messageProvider = new MessageProvider(messages, configurationProvider);

        playerData.saveAll();
        bankData.saveAll();

        plugin.getLogger().info("Reloaded successfully!");
    }


    public static cBanking getPlugin() {
        return plugin;
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getMessages() {
        return messages;
    }

    public static EconomyService getEconomyService() {
        return economyService;
    }

    public static MoneyProvider getMoneyProvider() {
        return moneyProvider;
    }

    public static ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public static MessageProvider getMessageProvider() {
        return messageProvider;
    }
}

