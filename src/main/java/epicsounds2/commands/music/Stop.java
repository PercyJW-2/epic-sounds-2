package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInstanceManager;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import epicsounds2.util.EventContainer;
import epicsounds2.util.Prefixes;

import static epicsounds2.util.DefaultMessageWriter.*;

public class Stop implements Command {

    private long guildID;

    private final AudioInstanceManager audioInstanceManager;

    public Stop(final AudioInstanceManager audioInstanceManager,
                   final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
        jda.upsertCommand(invoke, description).queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        final Guild guild = event.getGuild();
        guildID = guild.getIdLong();
        if (args != null && args.length > 0
                && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))) {
            writeMessage(help(), event);
            return;
        }

        audioInstanceManager.stop(guild);

        writeMessage("Stopped playback and purged queue.", event);
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this command to stop the music playback and purge the queue.\n"
                + "To view this message write '" + Prefixes.getPrefix(guildID) + "stop --help'.";
    }
}
