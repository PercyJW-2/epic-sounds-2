package util;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Prefixes {

    public static final String defaultPrefix = "Yo!";
    public static HashMap<Long, String > prefixMap = new HashMap<>();

    public static String getPrefix (Long guildID) {
        String prefix = prefixMap.get(guildID);
        if (prefix == null) {
            prefix = defaultPrefix;
        }
        return prefix;
    }
}
