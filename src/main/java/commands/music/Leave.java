package commands.music;

import commands.Command;
import net.dv8tion.jda.api.JDA;
import util.EventContainer;

import static util.DefaultMessageWriter.*;

public class Leave implements Command {

    public Leave(final String invoke, final String description, final JDA jda) {
        jda.upsertCommand(invoke, description).queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        if (args != null && args.length > 0
            && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))) {
                writeMessage(help(), event);
                return;
        }
        if (!event.getGuild().getAudioManager().isConnected()) {
            writeError("The Bot is in not connected to any Channel", event);
            return;
        }
        event.getGuild().getAudioManager().closeAudioConnection();
        writeMessage("Left Voice-Channel", event);
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this Command to disconnect the Bot from a Channel."
                + " The Bot has to be connected to an Channel, so it can leave.";
    }
}
