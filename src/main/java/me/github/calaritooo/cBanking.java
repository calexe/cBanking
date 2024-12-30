package me.github.calaritooo;

import me.github.calaritooo.commands.CommandHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class cBanking extends JavaPlugin {

    private VaultHook vaultHook;
    private MessageHandler messageHandler;
    private String currencySymbol;

    @Override
    public void onEnable() {
        getLogger().info("cBanking is initializing...");

        // Creating and/or load config.yml
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Load messages.yml
        messageHandler = new MessageHandler(this);

        // Check for and initialize Vault
        vaultHook = new VaultHook();
        if (!vaultHook.setupEconomy()) {
            getLogger().severe("Vault not found! Disabling cBanking...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getLogger().info("Vault initialized!");
        }

        // Register commands
        CommandHandler commandExecutor = new CommandHandler(this);
        commandExecutor.registerCommands();

        getLogger().info("cBanking has been successfully enabled!");
    }
    @Override
    public void onDisable() {
        getLogger().info("cBanking has been disabled.");
        // Clean up resources here
    }

    public @NotNull FileConfiguration getConfig() {
        return super.getConfig();
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}
