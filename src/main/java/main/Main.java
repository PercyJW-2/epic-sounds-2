package main;

import audioCore.AudioInstanceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import commands.Help;
import commands.PrefixCustomizer;
import commands.music.*;
import commands.sounds.AddSound;
import listeners.CommandListener;
import listeners.UserJoinListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import util.Prefixes;
import util.Sounds;

public class Main {

    private static JDABuilder builder;
    private static final AudioInstanceManager audioManager = new AudioInstanceManager();

    public static void main(String[] args) {

        JDA jda;

        builder = JDABuilder.create(args[0], Arrays.asList(
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
                loadPrefixes();
                loadSounds();
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    backupPrefixes();
                    backupSounds();
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
                backupPrefixes();
                backupSounds();
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
        //CommandHandler.getCommands().put("bassboost", new BassBoost(audioManager)); // TODO ->firstly migration to lavalink

        CommandHandler.getCommands().put("addsound", new AddSound());

    }

    private static void addListeners() {
        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new UserJoinListener());
    }

    private static void backupPrefixes(){
        Gson gson = new Gson();
        String json = gson.toJson(Prefixes.getPrefixMap());
        System.out.println(json);
        try (FileWriter writer = new FileWriter("prefixes.json",StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();

            System.out.println("saved prefixMap in prefixes.json");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static void loadPrefixes() throws IOException{
        Gson gson = new Gson();
        HashMap<Long, String> prefixMap = null;
        try {
            //load File
            StringBuilder contentBuilder = new StringBuilder();
            Stream<String> stream = Files.lines(Paths.get("prefixes.json"), StandardCharsets.UTF_8);
            stream.forEach(contentBuilder::append);
            String json = contentBuilder.toString();
            //loaded File
            System.out.println(json);
            Type type = new TypeToken<HashMap<Long,String>>(){}.getType();
            prefixMap = gson.fromJson(json, type);
        } catch (NoSuchFileException f){
            System.out.println("File does not exist");
        }
        if (prefixMap == null) {
            prefixMap = new HashMap<>();
        }
        Prefixes.setPrefixMap(prefixMap);
        System.out.println("loaded prefixMap");
    }

    private static void backupSounds(){
        Gson gson = new Gson();
        String json = gson.toJson(Sounds.getSoundsMap());
        System.out.println(json);
        try (FileWriter writer = new FileWriter("sounds.json",StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();

            System.out.println("saved soundsMap in sounds.json");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static void loadSounds() throws IOException{
        Gson gson = new Gson();
        HashMap<Long, HashMap<String, String>> soundsMap = null;
        try {
            //load File
            StringBuilder contentBuilder = new StringBuilder();
            Stream<String> stream = Files.lines(Paths.get("sounds.json"), StandardCharsets.UTF_8);
            stream.forEach(contentBuilder::append);
            String json = contentBuilder.toString();
            //loaded File
            System.out.println(json);
            Type type = new TypeToken<HashMap<Long,HashMap<String, String>>>(){}.getType();
            soundsMap = gson.fromJson(json, type);
        } catch (NoSuchFileException f){
            System.out.println("File does not exist");
        }
        if (soundsMap == null) {
            soundsMap = new HashMap<>();
        }
        Sounds.setSoundsMap(soundsMap);
        System.out.println("loaded prefixMap");
    }

}
