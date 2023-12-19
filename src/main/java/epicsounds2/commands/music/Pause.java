package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInstanceManager;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import epicsounds2.util.EventContainer;

import static epicsounds2.util.DefaultMessageWriter.*;

public class Pause implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Pause(final AudioInstanceManager audioInstanceManager,
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
        if (args == null || args.length < 1) {
            audioInstanceManager.getPlayer(guild).setPaused(true);
            writeMessage("Paused playback!", event);
        } else if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
            writeMessage(help(), event);
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this command to pause the music-playback. "
                + "If the bot rejoins the channel it forgets that is was paused.";
    }
}
