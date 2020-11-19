package util;

import java.util.HashMap;

public class Sounds {

    private static HashMap<Long, HashMap<String, String>> soundsMap = new HashMap<>();

    public static boolean containsGuild(Long key) {
        return soundsMap.containsKey(key);
    }

    public static void addGuildSoundSet(long guildID, HashMap<String, String> sounds) {
        soundsMap.put(guildID, sounds);
    }

    public static HashMap<String, String> getGuildSoundSet(long guildID) {
        return soundsMap.get(guildID);
    }

    public static HashMap<Long, HashMap<String, String>> getSoundsMap() {
        return soundsMap;
    }

    public static void setSoundsMap(HashMap<Long, HashMap<String, String>> soundsMap) {
        if (soundsMap.isEmpty()) {
            Sounds.soundsMap = soundsMap;
        }
    }
}
