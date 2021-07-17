package commands.music;

import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import util.EventContainer;

import static util.DefaultMessageWriter.*;

public class Join implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Join(final AudioInstanceManager audioInstanceManager,
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
        if (args != null && args.length > 0
        && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))) {
            writeMessage(help(), event);
            return;
        }
        final Guild guild = event.getGuild();
        final VoiceChannel vChan = event.getMember().getVoiceState().getChannel();
        if (guild.getAudioManager().isConnected()) {
            writeError("Already Joined a Channel", event);
        } else if (vChan != null) {
            guild.getAudioManager().openAudioConnection(vChan);
            audioInstanceManager.getPlayer(guild);
            if (audioInstanceManager.getPlayer(guild).isPaused()) {
                audioInstanceManager.getPlayer(guild).setPaused(false);
            }
            writeMessage("Joined Voice-Channel", event);
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this Command to let the Bot join a Channel. You have to be connected to the Channel,"
                + " if you want the Bot to join.";
    }
}
