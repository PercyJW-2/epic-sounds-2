package main;

import commands.PrefixCustomizer;
import listeners.CommandListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import util.Prefixes;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.Map;

public class Main {

    private static JDABuilder builder;

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                backupPrefixes();
                backupSounds();
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }));

        JDA jda = null;

        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(args[0]);
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setGame(Game.playing("your Music"));

        addListeners();
        addCommands();

        try {
            jda = builder.build();
            jda.awaitReady();

            try {
                loadPrefixes(jda);
                loadSounds();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (LoginException l) {
            l.printStackTrace();
        } catch (InterruptedException i) {
            i.printStackTrace();
        }

    }

    private static void addCommands() {
        CommandHandler.commands.put("customizePrefix", new PrefixCustomizer());
    }

    private static void addListeners() {
        builder.addEventListener(new CommandListener());
    }

    private static void backupPrefixes() throws SQLException{
        Connection conn = connect();
        if (conn == null) {
            System.out.println("Failed to backup the Prefixes");
            return;
        }

        //checking if table is available

        String sql = "CREATE TABLE IF NOT EXISTS prefixes (" +
                " serverId BIGINT PRIMARY KEY, " +
                " prefix CHAR" +
                ");";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();

        //backup Prefixes starts

        for (Map.Entry<Guild, String> e: Prefixes.prefixMap.entrySet()) {
            sql = "INSERT OR REPLACE INTO prefixes (serverID, prefix) VALUES (" +
                     e.getKey().getId() + ", '" +
                     e.getValue() +
                    "');";
            stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
        }

        conn.close();
        System.out.println("Cosed Database connection");
    }

    private static void loadPrefixes(JDA jda) throws SQLException{
        Connection conn = connect();
        if (conn == null) {
            System.out.println("Failed to load Prefixes");
            return;
        }

        //checking if table is available

        String sql = "CREATE TABLE IF NOT EXISTS prefixes (" +
                " serverId BIGINT PRIMARY KEY, " +
                " prefix CHAR" +
                ");";

        Statement stmt = conn.createStatement();
        stmt.execute(sql);
        stmt.close();

        //loading the saved Prefixes

        sql = "SELECT *" +
                "FROM prefixes";

        stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        stmt.close();

        Long[] serveridArray = (Long[]) result.getArray("serverId").getArray();
        String[] prefixArray = (String[]) result.getArray("prefix").getArray();

        for (int i = 0; serveridArray.length > i && prefixArray.length > i; i++) {
            Prefixes.prefixMap.put(jda.getGuildById(serveridArray[i]), prefixArray[i]);
        }

        conn.close();
        System.out.println("Closed Database connection");
    }

    private static void backupSounds() throws SQLException{

    }

    private static void loadSounds() throws SQLException{

    }

    private static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:epic-sounds-2-data.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to Database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

}
