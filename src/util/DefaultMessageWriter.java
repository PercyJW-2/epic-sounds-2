package util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class DefaultMessageWriter {
    public static void writeError (String error, MessageReceivedEvent event) {
        System.out.println(error);
        Message msg = event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("Error")
                        .setDescription(error)
                        .build()
        ).complete();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.delete().queue();
            }
        }, 10000);
    }

    public static void writeMessage (String msg, MessageReceivedEvent event) {
        System.out.println(msg);
        Message message = event.getTextChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setDescription(msg)
                        .build()
        ).complete();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                message.delete().queue();
            }
        }, 10000);
    }

    public static void writePersistentMessage (String msg, MessageReceivedEvent event) {
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setDescription(msg)
                        .build()
        ).queue();
    }
}
