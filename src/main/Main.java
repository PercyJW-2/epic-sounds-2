package main;

import audioCore.AudioInstanceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static util.Prefixes.prefixMap;

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
                    try {
                        backupPrefixes();
                        backupSounds();
                    } catch (IOException s) {
                        s.printStackTrace();
                    }
                }));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (LoginException | InterruptedException l) {
            l.printStackTrace();
        }

        ExecutorService myExecutor = Executors.newCachedThreadPool();
        myExecutor.execute(() -> {
            try {
                Thread.sleep(86400000L); //Wait 24h
                backupPrefixes();
                backupSounds();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void addCommands() {
        CommandHandler.commands.put("customizeprefix", new PrefixCustomizer());
        CommandHandler.commands.put("help", new Help());
        CommandHandler.commands.put("join", new Join(audioManager));
        CommandHandler.commands.put("leave", new Leave());
        CommandHandler.commands.put("play", new Play(audioManager));
        CommandHandler.commands.put("stop", new Stop(audioManager));
        CommandHandler.commands.put("skip", new Skip(audioManager));
        CommandHandler.commands.put("next", new Skip(audioManager));
        CommandHandler.commands.put("volume", new Volume(audioManager));
        CommandHandler.commands.put("pause", new Pause(audioManager));
        CommandHandler.commands.put("queue", new Queue(audioManager));
        CommandHandler.commands.put("current", new Current(audioManager));
        CommandHandler.commands.put("playing", new Current(audioManager));
        CommandHandler.commands.put("np", new Current(audioManager));
        CommandHandler.commands.put("undo", new Undo(audioManager));
        CommandHandler.commands.put("delete", new Delete(audioManager));
        CommandHandler.commands.put("shuffle", new Shuffle(audioManager));
        //CommandHandler.commands.put("bassboost", new BassBoost(audioManager)); TODO ->firstly migration to lavalink
    }

    private static void addListeners() {
        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new UserJoinListener());
    }

    private static void backupPrefixes() throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(prefixMap);
        System.out.println(json);
        FileWriter writer = new FileWriter("prefixes.json");
        writer.write(json);
        writer.flush();
        writer.close();

        System.out.println("saved prefixMap in prefixes.json");
    }

    private static void loadPrefixes() throws IOException{
        Gson gson = new Gson();
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
        System.out.println("loaded prefixMap");
    }

    private static void backupSounds() throws IOException{

        System.out.println("Cosed Database connection");
    }

    private static void loadSounds() throws IOException{

        System.out.println("Closed Database connection");
    }

}
