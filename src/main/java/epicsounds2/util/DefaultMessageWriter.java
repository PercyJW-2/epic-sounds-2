package epicsounds2.util;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

@SuppressWarnings("PMD.ClassNamingConventions")
public class DefaultMessageWriter {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMessageWriter.class);

    protected DefaultMessageWriter() {
        throw new UnsupportedOperationException();
    }

    public static void writeError(final String error, final EventContainer event) {
        LOG.warn(error);
        event.getReply().reply(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Error")
                        .setDescription(error)
                        .build()
        );
    }

    public static void writeMessage(final String msg, final EventContainer event) {
        LOG.info(msg);
        event.getReply().reply(
                new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setDescription(msg)
                        .build()
        );
    }
}
