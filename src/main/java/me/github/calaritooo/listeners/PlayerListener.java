package me.github.calaritooo.listeners;

import me.github.calaritooo.ServerEconomy;
import me.github.calaritooo.cBanking;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerListener implements Listener {

    private final cBanking plugin;
    private final ServerEconomy economy;

    public PlayerListener(cBanking plugin) {
        this.plugin = plugin;
        this.economy = ServerEconomy.getInstance();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            double startingBal = plugin.getConfig().getDouble("economy-settings.starting-bal");
            Player player = event.getPlayer();
            economy.depositPlayer(player, startingBal);
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {

        Player player = event.getPlayer();
        double totalBal = economy.getBalance(player);

        if (plugin.getConfig().getBoolean("modules.enable-money-loss-death")) {
            if (Objects.equals(plugin.getConfig().getString("economy-settings.money-loss-death-type"), "all")) {
                economy.withdrawPlayer(player, totalBal);
            }
            if (Objects.equals(plugin.getConfig().getString("economy-settings.money-loss-death-type"), "percentage")) {
                double percentageValue = plugin.getConfig().getDouble("economy-settings.money-loss-death-value");
                double percentageLost = percentageValue/100;
                economy.withdrawPlayer(player, totalBal*percentageLost);
            }
            if (Objects.equals(plugin.getConfig().getString("economy-settings.money-loss-death-type"), "flat")) {
                double flatDeathFee = plugin.getConfig().getDouble("economy-settings.money-loss-death-value");
                economy.withdrawPlayer(player, flatDeathFee);
            }
        }
    }
}
