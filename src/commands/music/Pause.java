package commands.music;

import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static util.defaultMessageWriter.*;

public class Pause implements Command {

    private AudioInstanceManager audioInstanceManager;

    public Pause (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        if (args == null || args.length < 1) {
            audioInstanceManager.getPlayer(g).setPaused(true);
            writeMessage("Paused playback!", event);
        } else {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
            }
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to pause the music-playback. If the bot rejoins the channel it forgets that is was paused.";
    }
}
