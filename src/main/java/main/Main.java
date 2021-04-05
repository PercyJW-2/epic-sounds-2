package main;

import exceptions.SettingsNotFoundException;
import audiocore.AudioInstanceManager;
import commands.Help;
import commands.PrefixCustomizer;
import commands.music.*;
import commands.music.Queue;
import listeners.CommandListener;
import listeners.UserJoinListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.*;

import util.FileLoadingUtils;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Main {

    private static final int EXIT_LOAD_ERROR = 101;
    private static final int EXIT_NO_SETTINGS = 100;
    private static final long BACKUP_DELAY = 86_400_000L;
    private static JDABuilder builder;
    private static final AudioInstanceManager AUDIO_MANAGER = new AudioInstanceManager();

    /**
     * Starts the Bot
     * @param args all Program Arguments are contained in this Method
     */
    public static void main(final String[] args) {

        final Map<String, String> settings;
        try {
            settings = FileLoadingUtils.loadSettings();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(EXIT_LOAD_ERROR);
            return;
        } catch (SettingsNotFoundException e) {
            System.out.println("Did not find Settings-File. Generated one. Please fill out its settings");
            System.exit(EXIT_NO_SETTINGS);
            return;
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

        //TODO remove Timer
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FileLoadingUtils.backupPrefixes();
                FileLoadingUtils.backupSounds();
            }
        }, BACKUP_DELAY, BACKUP_DELAY);
        //Wait 24h
    }

    private static void addCommands() {
        CommandHandler.getCommands().put("customizeprefix", new PrefixCustomizer());
        CommandHandler.getCommands().put("help", new Help());
        CommandHandler.getCommands().put("join", new Join(AUDIO_MANAGER));
        CommandHandler.getCommands().put("leave", new Leave());
        CommandHandler.getCommands().put("play", new Play(AUDIO_MANAGER));
        CommandHandler.getCommands().put("stop", new Stop(AUDIO_MANAGER));
        CommandHandler.getCommands().put("skip", new Skip(AUDIO_MANAGER));
        CommandHandler.getCommands().put("next", new Skip(AUDIO_MANAGER));
        CommandHandler.getCommands().put("volume", new Volume(AUDIO_MANAGER));
        CommandHandler.getCommands().put("pause", new Pause(AUDIO_MANAGER));
        CommandHandler.getCommands().put("queue", new Queue(AUDIO_MANAGER));
        CommandHandler.getCommands().put("current", new Current(AUDIO_MANAGER));
        CommandHandler.getCommands().put("playing", new Current(AUDIO_MANAGER));
        CommandHandler.getCommands().put("np", new Current(AUDIO_MANAGER));
        CommandHandler.getCommands().put("undo", new Undo(AUDIO_MANAGER));
        CommandHandler.getCommands().put("delete", new Delete(AUDIO_MANAGER));
        CommandHandler.getCommands().put("shuffle", new Shuffle(AUDIO_MANAGER));
        //CommandHandler.getCommands().put("bassboost", new BassBoost(audioManager)); // TODO -> someday dunno

        //CommandHandler.getCommands().put("addsound", new AddSound()); // TODO -> Soundboard doing it with more time

    }

    private static void addListeners() {
        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new UserJoinListener());
    }

}
