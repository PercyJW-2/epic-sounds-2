package commands.music;

import audio_core.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static util.DefaultMessageWriter.*;

public class Join implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Join(AudioInstanceManager audioManager) {
        audioInstanceManager = audioManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        if (args != null && args.length > 0) {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
                return;
            }
        }
        VoiceChannel vChan = event.getMember().getVoiceState().getChannel();
        if (g.getAudioManager().isConnected()) {
            writeError("Already Joined a Channel", event);
        } else if (vChan != null) {
            g.getAudioManager().openAudioConnection(vChan);
            audioInstanceManager.getPlayer(g);
            if (audioInstanceManager.getPlayer(g).isPaused()) {
                audioInstanceManager.getPlayer(g).setPaused(false);
            }
            writeMessage("Joined Voice-Channel", event);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to let the Bot join a Channel. You have to be connected to the Channel, if you want the Bot to join.";
    }
}
