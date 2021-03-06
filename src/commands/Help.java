package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;

import java.awt.*;

public class Help implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        String prefix = Prefixes.getPrefix(event.getGuild().getIdLong());
        event.getChannel().sendMessage(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription("``` __  __     ______     __         ______  \n" +
                                        "/\\ \\_\\ \\   /\\  ___\\   /\\ \\       /\\  == \\ \n" +
                                        " \\ \\  __ \\  \\ \\  __\\   \\ \\ \\____  \\ \\  _-/ \n" +
                                        "  \\ \\_\\ \\_\\  \\ \\_____\\  \\ \\_____\\  \\ \\_\\   \n" +
                                        "   \\/_/\\/_/   \\/_____/   \\/_____/   \\/_/   \n" +
                                        "                                  ``` \n" +
                                "_(for further details write the command with the addition of an '--help')_")
                        .addField(prefix + "customizePrefix","Changes the current Prefix.",false)
                        .addField(prefix + "join", "Summons the bot to your voice-channel.", false)
                        .addField(prefix + "leave", "Prompts the bot to leave your voice-channel.", false)
                        .addField(prefix + "play", "Adds Music provided by links and search-queries to the queue.", false)
                        .addField(prefix + "pause", "Pauses the music playback of the bot. To resume playback use the '" + prefix + "play' command", false)
                        .addField(prefix + "stop", "Stops the music playback and purges the queue.", false)
                        .addField(prefix + "skip", "Skips the current song and plays the next one, if it exists.", false)
                        .addField(prefix + "volume", "Changes and displays the master volume.", false)
                        .addField(prefix + "current", "Displays the currently played song", false)
                        .addField(prefix + "queue", "Displays the current queue of songs", false)
                        .addField(prefix + "undo", "Removes the most recently added song. Add a number to remove the more songs.", false)
                        .addField(prefix + "delete", "Removes the song, that is going to be played next. Add a number to remove more songs.", false)
                        .addField(prefix + "shuffle", "Shuffles the current queue", false)
                        .build()
        ).queue();
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
