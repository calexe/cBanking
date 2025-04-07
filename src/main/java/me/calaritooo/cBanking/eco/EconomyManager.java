package me.calaritooo.cBanking.eco;

import me.calaritooo.cBanking.cBanking;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class EconomyManager {

    private final cBanking plugin;
    private VaultEconomy economy;

    public EconomyManager(cBanking plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().severe("Vault not found! Disabling plugin.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }

        economy = new VaultEconomy(plugin);
        plugin.getServer().getServicesManager().register(Economy.class, economy, plugin, ServicePriority.Highest);
        plugin.getLogger().info("cBanking successfully registered as economy provider!");

        return true;
    }

    public void checkEconomyOverride() {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Economy registered = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
            if (registered != economy) {
                plugin.getLogger().warning("⚠ Another plugin has overridden cBanking as the economy provider!");
                plugin.getLogger().warning("✔ If this is intended, ignore this message.");
            } else {
                plugin.getLogger().info("cBanking remains the active Vault economy provider.");
            }
        }, 40L); // Wait 2 seconds
    }

    public void unregister() {
        if (economy != null) {
            plugin.getLogger().info("Unregistering economy from Vault...");
            plugin.getServer().getServicesManager().unregister(Economy.class, economy);
            economy = null;
        }
    }

    public Economy economy() {
        return economy;
    }
}
