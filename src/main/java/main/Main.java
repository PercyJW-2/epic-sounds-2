package main;

import Exceptions.SettingsNotFoundException;
import audiocore.AudioInstanceManager;
import commands.Help;
import commands.PrefixCustomizer;
import commands.music.*;
import listeners.CommandListener;
import listeners.UserJoinListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import util.FileLoadingUtils;

public class Main {

    private static JDABuilder builder;
    private static final AudioInstanceManager audioManager = new AudioInstanceManager();

    public static void main(String[] args) {

        HashMap<String, String> settings = null;
        try {
            settings = FileLoadingUtils.loadSettings();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(101);
        } catch (SettingsNotFoundException e) {
            System.out.println("Did not find Settings-File. Generated one. Please fill out its settings");
            System.exit(100);
        }

        final String discordAPIKey = settings.get("Discord_API_Key");

        JDA jda;

        builder = JDABuilder.create(discordAPIKey, Arrays.asList(
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_VOICE_STATES)
        );

        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("your Music | Yo!help"));

        addListeners();
        addCommands();

        try {
            jda = builder.build();
            jda.awaitReady();

            try {
                FileLoadingUtils.loadPrefixes();
                FileLoadingUtils.loadSounds();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    FileLoadingUtils.backupPrefixes();
                    FileLoadingUtils.backupSounds();
                }));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (LoginException | InterruptedException l) {
            l.printStackTrace();
        }

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FileLoadingUtils.backupPrefixes();
                FileLoadingUtils.backupSounds();
            }
        }, 86400000L, 86400000L); //Wait 24h
    }

    private static void addCommands() {
        CommandHandler.getCommands().put("customizeprefix", new PrefixCustomizer());
        CommandHandler.getCommands().put("help", new Help());
        CommandHandler.getCommands().put("join", new Join(audioManager));
        CommandHandler.getCommands().put("leave", new Leave());
        CommandHandler.getCommands().put("play", new Play(audioManager));
        CommandHandler.getCommands().put("stop", new Stop(audioManager));
        CommandHandler.getCommands().put("skip", new Skip(audioManager));
        CommandHandler.getCommands().put("next", new Skip(audioManager));
        CommandHandler.getCommands().put("volume", new Volume(audioManager));
        CommandHandler.getCommands().put("pause", new Pause(audioManager));
        CommandHandler.getCommands().put("queue", new Queue(audioManager));
        CommandHandler.getCommands().put("current", new Current(audioManager));
        CommandHandler.getCommands().put("playing", new Current(audioManager));
        CommandHandler.getCommands().put("np", new Current(audioManager));
        CommandHandler.getCommands().put("undo", new Undo(audioManager));
        CommandHandler.getCommands().put("delete", new Delete(audioManager));
        CommandHandler.getCommands().put("shuffle", new Shuffle(audioManager));
        //CommandHandler.getCommands().put("bassboost", new BassBoost(audioManager)); // TODO -> someday dunno

        //CommandHandler.getCommands().put("addsound", new AddSound()); // TODO -> Soundboard doing it with more time

    }

    private static void addListeners() {
        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new UserJoinListener());
    }

}
