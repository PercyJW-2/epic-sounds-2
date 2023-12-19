package epicsounds2.commands.sounds;

import epicsounds2.commands.Command;
import epicsounds2.util.EventContainer;
import epicsounds2.util.Prefixes;

import java.util.HashMap;

import epicsounds2.util.Sounds;

import static epicsounds2.util.DefaultMessageWriter.writeMessage;
import static epicsounds2.util.DefaultMessageWriter.writeError;

public class AddSound implements Command {
    private long guildID;

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        guildID = event.getGuild().getIdLong();
        if (args[0] == null) {
            writeError("You need to give this Command some Arguments", event);
            return;
        }
        if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
            writeMessage(help(), event);
            return;
        }
        final String link;
        if (args.length >= 2) {
            link = args[1];
        } else {
            writeError("You need to give this Command at least two arguments", event);
            return;
        }
        if (!Sounds.containsGuild(guildID)) {
            Sounds.addGuildSoundSet(guildID, new HashMap<>());
        }
        final String keyword = args[0];
        Sounds.getGuildSoundSet(guildID).put(keyword, link);
        writeMessage("Added " + keyword + "to the sounds of this server", event);
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this Command to add a Sound to the local Server-Sounds.\n"
                + "usage: " + Prefixes.getPrefix(guildID) + "addSound [sound-keyword] [link]";
    }
}
