package main;

import audioCore.AudioInstanceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import commands.Help;
import commands.PrefixCustomizer;
import commands.music.*;
import listeners.CommandListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static util.Prefixes.prefixMap;

public class Main {

    private static JDABuilder builder;
    private static AudioInstanceManager audioManager = new AudioInstanceManager();

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                backupPrefixes();
                backupSounds();
            } catch (IOException s) {
                s.printStackTrace();
            }
        }));

        JDA jda;

        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(args[0]);
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("your Music | Yo!help"));

        addListeners();
        addCommands();

        try {
            jda = builder.build();
            jda.awaitReady();

            try {
                loadPrefixes(jda);
                loadSounds(jda);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (LoginException l) {
            l.printStackTrace();
        } catch (InterruptedException i) {
            i.printStackTrace();
        }

        ExecutorService myExecutor = Executors.newCachedThreadPool();
        myExecutor.execute(() -> {
            try {
                Thread.sleep(86400000L); //Wait 24h
                backupPrefixes();
                backupSounds();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException s) {
                s.printStackTrace();
            }
        });
    }

    private static void addCommands() {
        CommandHandler.commands.put("customizePrefix", new PrefixCustomizer());
        CommandHandler.commands.put("help", new Help());
        CommandHandler.commands.put("join", new Join(audioManager));
        CommandHandler.commands.put("leave", new Leave(audioManager));
        CommandHandler.commands.put("play", new Play(audioManager));
        CommandHandler.commands.put("stop", new Stop(audioManager));
        CommandHandler.commands.put("skip", new Skip(audioManager));
        CommandHandler.commands.put("next", new Skip(audioManager));
        CommandHandler.commands.put("volume", new Volume(audioManager));
        CommandHandler.commands.put("pause", new Pause(audioManager));
        CommandHandler.commands.put("queue", new Queue(audioManager));
        CommandHandler.commands.put("current", new Current(audioManager));
        CommandHandler.commands.put("playing", new Current(audioManager));
        CommandHandler.commands.put("undo", new Undo(audioManager));
    }

    private static void addListeners() {
        builder.addEventListeners(new CommandListener());
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

    private static void loadPrefixes(JDA jda) throws IOException{
        Gson gson = new Gson();
        try {
            //load File
            StringBuilder contentBuilder = new StringBuilder();
            Stream<String> stream = Files.lines(Paths.get("prefixes.json"), StandardCharsets.UTF_8);
            stream.forEach(s -> contentBuilder.append(s));
            String json = contentBuilder.toString();
            //loaded File
            System.out.println(json);
            Type type = new TypeToken<HashMap<Long,String>>(){}.getType();
            prefixMap = gson.fromJson(json, type);
        } catch (NoSuchFileException f){
            System.out.println("File does not exist");
        }
        if (prefixMap == null) {
            prefixMap = new HashMap<Long,String>();
        }
        System.out.println("loaded prefixMap");
    }

    private static void backupSounds() throws IOException{

        System.out.println("Cosed Database connection");
    }

    private static void loadSounds(JDA jda) throws IOException{

        System.out.println("Closed Database connection");
    }

}
