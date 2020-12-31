package util;

import Exceptions.SettingsNotFoundException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class FileLoadingUtils {

    public static void backupPrefixes() {
        saveObj("prefixes", Prefixes.getPrefixMap());
    }

    public static void loadPrefixes() throws IOException{
        HashMap<Long, String> prefixMap =
                loadObj("prefixes", new TypeToken<>(){});
        if (prefixMap == null) {
            prefixMap = new HashMap<>();
        }
        Prefixes.setPrefixMap(prefixMap);
        System.out.println("loaded prefixMap");
    }

    public static void backupSounds(){
        saveObj("sounds", Sounds.getSoundsMap());
    }

    public static void loadSounds() throws IOException{
        HashMap<Long, HashMap<String, String>> soundsMap =
                loadObj("sounds", new TypeToken<>(){});
        if (soundsMap == null) {
            soundsMap = new HashMap<>();
        }
        Sounds.setSoundsMap(soundsMap);
        System.out.println("loaded soundsMap");
    }

    public static HashMap<String, String> loadSettings() throws IOException, SettingsNotFoundException {
        HashMap<String, String> settings = loadObj("settings", new TypeToken<>(){});
        if (settings == null) {
            settings = new HashMap<>();
            settings.put("Discord_API_Key", "Your_Bot_Token");
            settings.put("Spotify_Client_ID", "Your_Spotify_Client_ID");
            settings.put("Spotify_Client_Secret", "Your_Spotify_Client_Secret");
            saveObj("settings", settings);
            throw new SettingsNotFoundException();
        }
        return settings;
    }

    private static <T> void saveObj(final String name, T objToSave) {
        Gson gson = new Gson();
        String json = gson.toJson(objToSave);
        System.out.println(json);
        try (FileWriter writer = new FileWriter(name + ".json", StandardCharsets.UTF_8)) {
            writer.write(json);
            writer.flush();

            System.out.println(("saved name in name.json").replaceAll("name", name));
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    private static <T> T loadObj(final String name, TypeToken<T> type) throws IOException {
        Gson gson = new Gson();
        T obj = null;
        try {
            //load File
            StringBuilder contentBuilder = new StringBuilder();
            Stream<String> stream = Files.lines(Paths.get(name + ".json"), StandardCharsets.UTF_8);
            stream.forEach(contentBuilder::append);
            String json = contentBuilder.toString();
            //loaded File
            System.out.println(json);
            obj = gson.fromJson(json, type.getType());
        } catch (NoSuchFileException f){
            System.out.println("File does not exist");
            System.out.println("File will be created on exit");
        }
        System.out.println("loaded " + name);
        return obj;
    }
}
