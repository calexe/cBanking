package me.github.calaritooo.listeners;

import me.github.calaritooo.cBanking;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class EventHandler implements Listener {

    private final cBanking plugin;

    public EventHandler(cBanking plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(plugin), plugin);

    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }
}