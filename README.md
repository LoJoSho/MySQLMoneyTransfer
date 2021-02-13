# MySQLMoneyTransfer
A Minecraft plugin that balances the balance between servers.

Connection to the database happens in MySQLEcon.java. Whenever a player joins, it get's that player from the database and sets the player balance to said number, (See PlayerListener.java). When a player leaves, their balances is set in the MySQL database. If they join a different server, that balance will carry over. 

During the plugin, every second it produces a hashmap of all online players balances. If the server stops, it then uses that hashmap to send that data to the MySQL server. 
