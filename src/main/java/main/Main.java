package main;

import commands.Command;
//import commands.spotifyconnect.SpotifyConnect;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileLoadingUtils;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class Main {

    private static final int EXIT_LOAD_ERROR = 101;
    private static final int EXIT_NO_SETTINGS = 100;
    private static final long BACKUP_DELAY = 86_400_000L;
    private static JDABuilder builder;
    private static final AudioInstanceManager AUDIO_MANAGER = new AudioInstanceManager();
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    /**
     * Starts the Bot.
     * @param args all Program Arguments are contained in this Method
     */
    public static void main(final String[] args) {

        final Map<String, String> settings;
        try {
            settings = FileLoadingUtils.loadSettings();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            System.exit(EXIT_LOAD_ERROR);
            return;
        } catch (SettingsNotFoundException e) {
            LOG.warn("Did not find Settings-File. Generated one. Please fill out its settings");
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

        try {
            jda = builder.build();
            jda.awaitReady();

            addCommands(jda);

            try {
                FileLoadingUtils.loadPrefixes();
                FileLoadingUtils.loadSounds();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    FileLoadingUtils.backupPrefixes();
                    FileLoadingUtils.backupSounds();
                }));
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        } catch (LoginException | InterruptedException l) {
            LOG.error(l.getMessage());
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

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private static void addCommands(final JDA jda) {
        addCommand(
                new PrefixCustomizer("customize-prefix", "Customizes Prefix when not using /", jda),
                "customizeprefix");
        addCommand(new Help("help", "Prints all Commands", jda), "help");
        addCommand(
                new Join(AUDIO_MANAGER, "join", "Lets the bot join the channel", jda),
                "join");
        addCommand(
                new Leave("leave", "Lets the Bot leave the cannel", jda),
                "leave");
        addCommand(
                new Play(AUDIO_MANAGER, "play", "Starts playback for a song", jda),
                "play", "p");
        addCommand(
                new Stop(AUDIO_MANAGER, "stop", "Stops playback", jda),
                "stop", "st");
        addCommand(
                new Skip(AUDIO_MANAGER, "skip", "Skips song", jda),
                "skip", "next", "sk");
        addCommand(
                new Volume(AUDIO_MANAGER, "volume", "Changes volume", jda),
                "volume");
        addCommand(
                new Pause(AUDIO_MANAGER, "pause", "Pauses playback", jda),
                "pause");
        addCommand(
                new Queue(AUDIO_MANAGER, "queue", "Shows the current queue", jda),
                "queue", "list");
        addCommand(
                new Current(AUDIO_MANAGER, "playing", "Shows the current song", jda),
                "playing", "current", "np");
        addCommand(
                new Undo(AUDIO_MANAGER, "undo", "Deletes the most recent addition to the queue", jda),
                "undo");
        addCommand(
                new Delete(AUDIO_MANAGER, "delete", "Deletes the specified song from teh queue", jda),
                "delete");
        addCommand(
                new Shuffle(AUDIO_MANAGER, "shuffle", "Shuffles the queue", jda),
                "shuffle");
        addCommand(
                new BassBoost(AUDIO_MANAGER, "bassboost", "Enables/Disables bassboost", jda),
                "bassboost");
        //addCommand(
        //        new SpotifyConnect(AUDIO_MANAGER, "spotifyconnect", "Activates Spotify connect playback", jda),
        //        "spotifyconnect");

        //addCommand(new AddSound(), jda, "Adds Sound to soundboard", "addsound");
        //TODO -> Soundboard doing it with more time

    }

    private static void addCommand(final Command cmd, final String... names) {
        for (final String name : names) {
            CommandHandler.getCommands().put(name, cmd);
        }
    }

    private static void addListeners() {
        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new UserJoinListener());
    }

}
