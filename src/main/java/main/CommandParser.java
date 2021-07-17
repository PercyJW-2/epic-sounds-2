package main;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

@SuppressWarnings("PMD.ClassNamingConventions")
public class CommandParser {

    protected CommandParser() {
        throw new UnsupportedOperationException();
    }

    public static CommandContainer parser(final String raw, final MessageReceivedEvent event, final String prefix) {

        final var beheaded = raw.replaceFirst(Pattern.quote(prefix), "");
        final var splitBeheaded = beheaded.split(" ");
        final var invoke = splitBeheaded[0].toLowerCase(Locale.getDefault());
        final var split = new ArrayList<>(Arrays.asList(splitBeheaded));
        final var args = new String[split.size() - 1];
        split.subList(1, split.size()).toArray(args);

        return new CommandContainer(raw, beheaded, splitBeheaded, invoke, args, event);
    }

    public static class CommandContainer {

        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final MessageReceivedEvent event;

        @SuppressWarnings("PMD.ArrayIsStoredDirectly")
        public CommandContainer(
                final String rw,
                final String beheaded,
                final String[] splitBeheaded,
                final String invoke,
                final String[] args,
                final MessageReceivedEvent event) {
            this.raw = rw;
            this.beheaded = beheaded;
            this.splitBeheaded = splitBeheaded;
            this.invoke = invoke;
            this.args = args;
            this.event = event;
        }

    }


}
