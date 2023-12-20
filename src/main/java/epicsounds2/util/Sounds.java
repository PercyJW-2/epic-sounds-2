package epicsounds2.util;

import java.util.Map;
import java.util.HashMap;

public final class Sounds {
    private static Map<Long, Map<String, String>> soundsMap = new HashMap<>();

    private Sounds() {
        throw new UnsupportedOperationException();
    }

    public static boolean containsGuild(final Long key) {
        return soundsMap.containsKey(key);
    }

    public static void addGuildSoundSet(final long guildID, final Map<String, String> sounds) {
        soundsMap.put(guildID, sounds);
    }

    public static Map<String, String> getGuildSoundSet(final long guildID) {
        return soundsMap.get(guildID);
    }

    public static Map<Long, Map<String, String>> getSoundsMap() {
        return soundsMap;
    }

    public static void setSoundsMap(final Map<Long, Map<String, String>> soundsMap) {
        if (soundsMap.isEmpty()) {
            Sounds.soundsMap = soundsMap;
        }
    }
}
