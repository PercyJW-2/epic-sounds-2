package commands.music;

import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static util.defaultMessageWriter.*;

public class Leave implements Command {

    private AudioInstanceManager audioInstanceManager;

    public Leave(AudioInstanceManager audioManager) {
        audioInstanceManager = audioManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if (args != null && args.length > 0) {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h"))
                writePersistentMessage(help(), event); return;
        }
        if (event.getGuild().getAudioManager().isConnected()==false) {
            writeError("The Bot is in not connected to any Channel", event);
            return;
        }
        event.getGuild().getAudioManager().closeAudioConnection();
        writeMessage("Left Voice-Channel", event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to disconnect the Bot from a Channel. The Bot has to be connected to an Channel, so it can leave.";
    }
}
