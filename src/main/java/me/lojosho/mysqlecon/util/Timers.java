package me.lojosho.mysqlecon.util;

import me.lojosho.mysqlecon.MySQLEcon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timers {

    private final MySQLEcon plugin;

    public Timers(MySQLEcon plugin) {
        this.plugin = plugin;
        startTimer();
    }

    public void startTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.Monies.clear();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.Monies.put(player, MySQLEcon.getEconomy().getBalance(player));
                }
            }
        }.runTaskTimer(plugin, 0L, 20);
    }
}
