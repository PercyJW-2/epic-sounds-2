package util;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EventConverterUtil {
    protected EventConverterUtil() {
        throw new UnsupportedOperationException();
    }

    public static EventContainer convertMessageEvent(final MessageReceivedEvent event) {
        return new EventContainer(
                event.getJDA(),
                event.getGuild(),
                event.getMember(),
                embed -> new MessageHookContainer(event.getMessage().replyEmbeds(embed).complete()),
                false
        );
    }

    public static EventContainer convertSlashCommandEvent(final SlashCommandEvent event) {
        return new EventContainer(
                event.getJDA(),
                event.getGuild(),
                event.getMember(),
                embed -> new MessageHookContainer(event.replyEmbeds(embed).complete()),
                true
        );
    }
}
