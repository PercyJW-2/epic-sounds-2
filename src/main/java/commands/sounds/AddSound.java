package commands.sounds;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;

import java.util.HashMap;

import util.Sounds;

import static util.DefaultMessageWriter.writeMessage;
import static util.DefaultMessageWriter.writeError;

public class AddSound implements Command {
    private long guildID;

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        guildID = event.getGuild().getIdLong();
        if (args[0] == null) {
            writeError("You need to give this Command some Arguments", event);
            return;
        }
        if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
            writeMessage(help(), event);
            return;
        }
        String keyword = args[0];
        String link;
        if (args.length >= 2) {
            link = args[1];
        } else {
            writeError("You need to give this Command at least two arguments", event);
            return;
        }
        if (!Sounds.containsGuild(guildID)) {
            Sounds.addGuildSoundSet(guildID, new HashMap<>());
        }
        Sounds.getGuildSoundSet(guildID).put(keyword, link);
        writeMessage("Added " + keyword + "to the sounds of this server", event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to add a Sound to the local Server-Sounds.\n" +
                "usage: " + Prefixes.getPrefix(guildID) + "addSound [sound-keyword] [link]";
    }
}
