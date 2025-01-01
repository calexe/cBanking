package me.github.calaritooo.listeners;

import me.github.calaritooo.VaultHook;
import me.github.calaritooo.cBanking;
import org.bukkit.block.Vault;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final cBanking plugin;

    public PlayerListener(cBanking plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            double startingBal = plugin.getConfig().getDouble("economy-settings.starting-bal");
            Player player = event.getPlayer();
            VaultHook.deposit(player, startingBal);
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {

        Player player = event.getPlayer();
        double totalBal = VaultHook.getBalance(player);

        if (plugin.getConfig().getBoolean("modules.enable-money-loss-death")) {
            if (plugin.getConfig().getString("economy-settings.money-loss-death-type").equalsIgnoreCase("all")) {
                VaultHook.withdraw(player, totalBal);
            }
            if (plugin.getConfig().getString("economy-settings.money-loss-death-type").equalsIgnoreCase("percentage")) {
                double percentageValue = plugin.getConfig().getDouble("economy-settings.money-loss-death-value");
                double percentageLost = percentageValue/100;
                VaultHook.withdraw(player, totalBal*percentageLost);
            }
            if (plugin.getConfig().getString("economy-settings.money-loss-death-type").equalsIgnoreCase("flat")) {
                double flatDeathFee = plugin.getConfig().getDouble("economy-settings.money-loss-death-value");
                VaultHook.withdraw(player, flatDeathFee);
            }
        }
    }
}
