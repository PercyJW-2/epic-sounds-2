package util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("PMD.ClassNamingConventions")
public class DefaultMessageWriter {

    private static final int DELAY = 10_000;

    protected DefaultMessageWriter() {
        throw new UnsupportedOperationException();
    }

    public static void writeError (final String error, final MessageReceivedEvent event) {
        System.out.println(error);
        final Message msg = event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Error")
                        .setDescription(error)
                        .build()
        ).complete();

        //TODO remove Timer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, DELAY);
    }

    public static void writeMessage (final String msg, final MessageReceivedEvent event) {
        System.out.println(msg);
        final Message message = event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setDescription(msg)
                        .build()
        ).complete();

        //TODO remove Timer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                message.delete().queue();
            }
        }, DELAY);
    }

    public static void writePersistentMessage (final String msg, final MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setDescription(msg)
                        .build()
        ).queue();
    }
}
