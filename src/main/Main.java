package main;

import commands.PrefixCustomizer;
import listeners.CommandListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

import javax.security.auth.login.LoginException;

public class Main {

    private static JDABuilder builder;

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //
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

}
