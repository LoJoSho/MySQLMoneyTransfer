package me.lojosho.mysqlecon;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerListener implements Listener {

    private final MySQLEcon plugin;

    public PlayerListener(MySQLEcon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        String SUUID = player.getUniqueId().toString();
        try {
            ResultSet rs = MySQLEcon.preparedStatement("SELECT COUNT(UUID) FROM PLAYER_MONEY WHERE UUID = '" + SUUID + "';").executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) { // is not in system
                MySQLEcon.preparedStatement(" ").executeUpdate();
            } else { // is already in statement
                ResultSet rs2 = MySQLEcon.preparedStatement("SELECT * FROM PLAYER_MONEY WHERE UUID = '" + SUUID + "';").executeQuery();
                rs2.next();
                int money = rs2.getInt("MONEY");
                double hasmoney = MySQLEcon.getEconomy().getBalance(player);
                MySQLEcon.getEconomy().withdrawPlayer(player, hasmoney);
                MySQLEcon.getEconomy().depositPlayer(player, money);
            }
        } catch (SQLException y) {
            y.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        try {
            ResultSet rs = MySQLEcon.preparedStatement("SELECT COUNT(UUID) FROM PLAYER_MONEY WHERE UUID = '" + player.getUniqueId() + "';").executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                plugin.getLogger().info("Unable to find the recently exited player MySQL. There is an error somehwere in PlayerListner.java or the MySQL database");
            } else {
                double hasmoney = MySQLEcon.getEconomy().getBalance(player);
                String SUUID = player.getUniqueId().toString();
                MySQLEcon.preparedStatement("REPLACE INTO PLAYER_MONEY(UUID,MONEY)" +
                        "VALUES('" + SUUID + "', " + hasmoney + ");").executeUpdate();
            }
        } catch (SQLException x) {
            x.printStackTrace();
        }
    }
}
