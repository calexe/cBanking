package me.calaritooo.cBanking.events;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import me.calaritooo.cBanking.eco.EconomyService;
import me.calaritooo.cBanking.util.configuration.ConfigurationOption;
import me.calaritooo.cBanking.util.configuration.ConfigurationProvider;
import me.calaritooo.cBanking.util.messages.Message;
import me.calaritooo.cBanking.util.messages.MessageProvider;
import me.calaritooo.cBanking.util.money.Money;
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
            double initialBalance = config.get(ConfigurationOption.ECONOMY_STARTING_BALANCE);
            eco.createAccount(player, Money.of(initialBalance));
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player p = event.getEntity();
        UUID player = p.getUniqueId();
        Money totalBal = eco.getBalance(player);

        if (config.get(ConfigurationOption.MODULE_DEATH_LOSS)) {
            String deathLossType = config.get(ConfigurationOption.ECONOMY_DEATH_LOSS_TYPE);
            switch (deathLossType.toLowerCase()) {
                case "all" -> {
                    if (eco.withdraw(player, totalBal)) {
                        messages.send(p, Message.GENERAL_DEATH_LOSS_ALL,
                                "%amt%", totalBal.toString());
                    }
                }
                case "percentage" -> {
                    double percentageValue = config.get(ConfigurationOption.ECONOMY_DEATH_LOSS_VALUE);
                    double percentageLost = percentageValue / 100;
                    double amount = totalBal.value() * percentageLost;
                    if (totalBal.value() <= 0.01) {
                        eco.setBalance(player, Money.zero());
                        messages.send(p, Message.GENERAL_DEATH_LOSS_ALL,
                                "%amt%", eco.getBalance(player).toString());
                    }
                    if (eco.withdraw(player, Money.of(amount))) {
                        messages.send(p, Message.GENERAL_DEATH_LOSS_PERCENTAGE,
                                "%percentage%", String.valueOf(percentageValue));
                    }
                }
                case "flat" -> {
                    double flatValue = config.get(ConfigurationOption.ECONOMY_DEATH_LOSS_VALUE);
                    if (totalBal.value() < flatValue) {
                        if (eco.withdraw(player, totalBal)) {
                            messages.send(p, Message.GENERAL_DEATH_LOSS_FLAT,
                                    "%amt%", totalBal.toString());
                        }
                    } else {
                        double amount = totalBal.value() - flatValue;
                        if (eco.withdraw(player, Money.of(amount))) {
                            messages.send(p, Message.GENERAL_DEATH_LOSS_FLAT,
                                    "%amt%", String.valueOf(amount));
                        }
                    }
                }
                default -> plugin.getLogger().warning("Invalid death loss type provided! Please specify 'all', 'percentage', or 'flat' in the config.yml!");
            }
        }
    }
}