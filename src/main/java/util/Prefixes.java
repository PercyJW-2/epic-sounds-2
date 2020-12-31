package util;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Prefixes {

    public static final String defaultPrefix = "Yo!";
    private static HashMap<Long, String> prefixMap = new HashMap<>();

    public static void addPrefix(long guildID, String prefix) {
        prefixMap.put(guildID, prefix);
    }

    public static void setPrefixMap(HashMap<Long, String> map) {
        if (prefixMap.isEmpty()) {
            prefixMap = map;
        }
    }

    public static HashMap<Long, String> getPrefixMap() {
        return prefixMap;
    }

    public static String getPrefix (long guildID) {
        String prefix = prefixMap.get(guildID);
        if (prefix == null) {
            prefix = defaultPrefix;
        }
        return prefix;
    }
}
