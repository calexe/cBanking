package me.calaritooo.cBanking;

import me.calaritooo.cBanking.bank.BankAccount;
import me.calaritooo.cBanking.bank.BankData;
import me.calaritooo.cBanking.eco.EconomyService;
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
    private static EconomyService economyService;

    private cBankingCore() {
    }

    public static void initialize(cBanking pluginInstance) {
        plugin = pluginInstance;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        configurationProvider = new ConfigurationProvider(config);

        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        messageProvider = new MessageProvider(messages, configurationProvider);

        PlayerData playerData = new PlayerData();
        plugin.getLogger().info("Loaded player data!");

        BankData bankData = new BankData();
        plugin.getLogger().info("Loaded banks.yml!");

        PlayerAccount playerAccount = new PlayerAccount(playerData);
        BankAccount bankAccount = new BankAccount(bankData);

        economyService = new EconomyService(playerAccount, playerData, bankAccount, bankData);

        plugin.getLogger().info("Loaded economy service!");
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

    public static ConfigurationProvider getConfigurationProvider() {
        return configurationProvider;
    }

    public static MessageProvider getMessageProvider() {
        return messageProvider;
    }
}

