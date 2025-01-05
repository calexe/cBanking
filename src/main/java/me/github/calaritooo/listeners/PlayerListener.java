package me.github.calaritooo.listeners;

import me.github.calaritooo.accounts.AccountHandler;
import me.github.calaritooo.cBanking;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerListener implements Listener {

    private final cBanking plugin;
    private final AccountHandler accountHandler;

    public PlayerListener(cBanking plugin) {
        this.plugin = plugin;
        this.accountHandler = new AccountHandler(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (!accountHandler.hasAccount(playerName)) {
            String playerUUID = player.getUniqueId().toString();
            double initialBalance = plugin.getConfig().getDouble("economy-settings.starting-bal");
            accountHandler.createAccount(playerName, playerUUID, initialBalance);
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        double totalBal = accountHandler.getBalance(player.getName());

        if (plugin.getConfig().getBoolean("modules.enable-money-loss-death")) {
            if (Objects.equals(plugin.getConfig().getString("economy-settings.money-loss-death-type"), "all")) {
                accountHandler.withdraw(player.getName(), totalBal);
            }
            if (Objects.equals(plugin.getConfig().getString("economy-settings.money-loss-death-type"), "percentage")) {
                double percentageValue = plugin.getConfig().getDouble("economy-settings.money-loss-death-value");
                double percentageLost = percentageValue / 100;
                accountHandler.withdraw(player.getName(), totalBal * percentageLost);
            }
            if (Objects.equals(plugin.getConfig().getString("economy-settings.money-loss-death-type"), "flat")) {
                double flatDeathFee = plugin.getConfig().getDouble("economy-settings.money-loss-death-value");
                accountHandler.withdraw(player.getName(), flatDeathFee);
            }
        }
    }
}