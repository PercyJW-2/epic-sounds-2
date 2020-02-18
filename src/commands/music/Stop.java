package commands.music;

import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;

import static util.defaultMessageWriter.*;

public class Stop implements Command {

    private long guildID;

    private AudioInstanceManager audioInstanceManager;

    public Stop (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        guildID = g.getIdLong();
        if (args != null && args.length > 0) {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
                return;
            }
        }

        audioInstanceManager.stop(g);

        writeMessage("Stopped playback and purged queue.",event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to stop the music playback and purge the queue.\n" +
                "To view this message write '"+ Prefixes.getPrefix(guildID) + "stop --help'.";
    }
}
