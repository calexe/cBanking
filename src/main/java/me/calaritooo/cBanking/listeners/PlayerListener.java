package me.calaritooo.cBanking.listeners;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final cBanking plugin;
    private final EconomyService eco;
    private final MessageProvider messages;
    private final ConfigurationProvider config;

    public PlayerListener() {
        this.plugin = cBankingCore.getPlugin();
        this.eco = cBankingCore.getEconomyService();
        this.messages = cBankingCore.getMessageProvider();
        this.config = cBankingCore.getConfigurationProvider();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        if (!eco.exists(player)) {
            double initialBalance = config.getDouble(ConfigurationOption.ECONOMY_STARTING_BALANCE);
            eco.createAccount(player, initialBalance);
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerUUID = event.getEntity().getUniqueId();
        double totalBal = eco.getBalance(playerUUID);

        if (config.getBoolean(ConfigurationOption.MODULE_DEATH_LOSS)) {
            switch (config.getString(ConfigurationOption.ECONOMY_DEATH_LOSS_TYPE)) {
                case "all" -> {
                    if (eco.withdraw(playerUUID, totalBal)) {
                        messages.send(player, Message.GENERAL_DEATH_LOSS_ALL,
                                "%amt%", String.valueOf(totalBal));
                    }
                }
                case "percentage" -> {
                    double percentageValue = config.getDouble(ConfigurationOption.ECONOMY_DEATH_LOSS_VALUE);
                    double percentageLost = percentageValue / 100;
                    if (eco.withdraw(playerUUID, totalBal * percentageLost)) {
                        messages.send(player, Message.GENERAL_DEATH_LOSS_PERCENTAGE,
                                "%amt%", String.valueOf(totalBal * percentageLost),
                                "%percentage%", String.valueOf(percentageValue));
                    }
                }
                case "flat" -> {
                    double flatValue = config.getDouble(ConfigurationOption.ECONOMY_DEATH_LOSS_VALUE);
                    if (totalBal < flatValue) {
                        if (eco.withdraw(playerUUID, totalBal)) {
                            messages.send(player, Message.GENERAL_DEATH_LOSS_FLAT,
                                    "%amt%", String.valueOf(totalBal));
                        }
                    } else {
                        if (eco.withdraw(playerUUID, totalBal - flatValue)) {
                            messages.send(player, Message.GENERAL_DEATH_LOSS_FLAT,
                                    "%amt%", String.valueOf(totalBal - flatValue));
                        }
                    }
                }
                default -> plugin.getLogger().warning("Invalid death loss type provided! Please specify 'all', 'percentage', or 'flat' in the config.yml!");
            }
        }
    }
}