package me.github.calaritooo.listeners;

import me.github.calaritooo.cBanking;
import org.bukkit.plugin.PluginManager;

public class EventHandler {

    private final cBanking plugin;

    public EventHandler(cBanking plugin) {
        this.plugin = plugin;
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(plugin), plugin);

    }
}