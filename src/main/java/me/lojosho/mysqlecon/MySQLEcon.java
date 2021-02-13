package me.lojosho.mysqlecon;

import me.lojosho.mysqlecon.util.Timers;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.HashMap;
import java.util.logging.Logger;

public final class MySQLEcon extends JavaPlugin {

    private static final Logger log = Logger.getLogger("MySQLEcon");
    private static Economy econ = null;
    private String host, database, username, password;
    private int port;
    private static Connection connection;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        host = "localhost";
        port = 3306;
        database = "moneydatabase";
        username = "root";
        password = "";
        try {
            openConnection();
            System.out.println("Connected to MySQL database!");
        } catch (SQLException x) {
            x.printStackTrace();
        }
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return; }
        new Timers(this);
    }

    private void openConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
    }

    public static PreparedStatement preparedStatement(String query) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }
    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().getPendingTasks().forEach(BukkitTask::cancel);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Double Money = Monies.get(player);
            String SUUID = player.getUniqueId().toString();
            try {
                ResultSet rs = MySQLEcon.preparedStatement("SELECT COUNT(UUID) FROM PLAYER_MONEY WHERE UUID = '" + player.getUniqueId().toString() + "';").executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    this.getLogger().info("Unable to find the recently exited player MySQL. There is an error somehwere in PlayerListner.java or the MySQL database");
                } else {
                    MySQLEcon.preparedStatement("REPLACE INTO PLAYER_MONEY(UUID,MONEY)" +
                            "VALUES('" + SUUID + "', " + Money + ");").executeUpdate();
                }
            } catch (SQLException x) {
                x.printStackTrace();
            }
        }
    }

    public HashMap<Player, Double> Monies = new HashMap<>();
}
