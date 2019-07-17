package util;

import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

public class Prefixes {

    private static final String defaultPrefix = "Yo!";
    public static HashMap<Guild, String > prefixMap = new HashMap<Guild, String>();

    public static String getDefaultPrefix() {
        return defaultPrefix;
    }
}
