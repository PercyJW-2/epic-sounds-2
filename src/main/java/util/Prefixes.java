package util;

import java.util.HashMap;
import java.util.Map;

public final class Prefixes {
    public static final String DEFAULT_PREFIX = "Yo!";
    private static Map<Long, String> prefixMap = new HashMap<>();

    private Prefixes() {
        throw new UnsupportedOperationException();
    }

    public static void addPrefix(final long guildID, final String prefix) {
        prefixMap.put(guildID, prefix);
    }

    public static void setPrefixMap(final Map<Long, String> map) {
        if (prefixMap.isEmpty()) {
            prefixMap = map;
        }
    }

    public static Map<Long, String> getPrefixMap() {
        return prefixMap;
    }

    public static String getPrefix(final long guildID) {
        String prefix = prefixMap.get(guildID);
        if (prefix == null) {
            prefix = DEFAULT_PREFIX;
        }
        return prefix;
    }
}
