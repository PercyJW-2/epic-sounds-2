package util;

import exceptions.SettingsNotFoundException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class FileLoadingUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileLoadingUtils.class);

    protected FileLoadingUtils() {
        throw new UnsupportedOperationException();
    }

    public static void backupPrefixes() {
        saveObj("prefixes", Prefixes.getPrefixMap());
    }

    public static void loadPrefixes() throws IOException {
        HashMap<Long, String> prefixMap =
                loadObj("prefixes", new TypeToken<>(){});
        if (prefixMap == null) {
            prefixMap = new HashMap<>();
        }
        Prefixes.setPrefixMap(prefixMap);
        LOG.info("loaded prefixMap");
    }

    public static void backupSounds() {
        saveObj("sounds", Sounds.getSoundsMap());
    }

    public static void loadSounds() throws IOException {
        Map<Long, Map<String, String>> soundsMap =
                loadObj("sounds", new TypeToken<>(){});
        if (soundsMap == null) {
            soundsMap = new HashMap<>();
        }
        Sounds.setSoundsMap(soundsMap);
        LOG.info("loaded soundsMap");
    }

    public static Map<String, String> loadSettings() throws IOException, SettingsNotFoundException {
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

    private static <T> void saveObj(final String name, final T objToSave) {
        final Gson gson = new Gson();
        final String json = gson.toJson(objToSave);
        LOG.info(json);
        try (OutputStream fos = Files.newOutputStream(new File(name + ".json").toPath())) {
            fos.write(json.getBytes(StandardCharsets.UTF_8));

            LOG.info("saved name in {}.json", name);
        } catch (IOException i) {
            LOG.error(i.getMessage());
        }
    }

    private static <T> T loadObj(final String name, final TypeToken<T> type) throws IOException {
        final Gson gson = new Gson();
        T obj = null;
        try {
            //load File
            final StringBuilder contentBuilder = new StringBuilder();
            final Stream<String> stream = Files.lines(Paths.get(name + ".json"), StandardCharsets.UTF_8);
            stream.forEach(contentBuilder::append);
            final String json = contentBuilder.toString();
            //loaded File
            LOG.info(json);
            obj = gson.fromJson(json, type.getType());
        } catch (NoSuchFileException f) {
            LOG.warn("File does not exist \n File will be created on exit");
        }
        LOG.info("loaded " + name);
        return obj;
    }
}
