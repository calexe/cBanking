package me.calaritooo.cBanking.events;

import me.calaritooo.cBanking.cBanking;
import me.calaritooo.cBanking.cBankingCore;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class EventManager implements Listener {

    private final cBanking plugin;

    public EventManager() {
        this.plugin = cBankingCore.getPlugin();
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), plugin);

    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(plugin);
    }
}