package me.github.calaritooo;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {

    private static Economy economy = null;
    private final cBanking plugin = cBanking.getInstance();

    public VaultHook() {
    }

    private static void setupEconomy() {
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (rsp != null)
            economy = rsp.getProvider();
    }

    public Economy getEconomy() {
        return economy;
    }

    static {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupEconomy();
        }
    }
}