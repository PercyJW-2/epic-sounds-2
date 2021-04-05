package commands.music;

import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import static util.DefaultMessageWriter.*;
import util.Prefixes;

public class Shuffle implements Command {

    private final AudioInstanceManager audioInstanceManager;
    private long guildID;

    public Shuffle (AudioInstanceManager audioInstanceManager) {
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
            if (args[0].equals("--help") || args[0].equals("-h")) {
                writeMessage(help(), event);
                return;
            }
        }

        if (audioInstanceManager.getTrackManager(g).getQueue().isEmpty()) {
            writeError("Queue is empty", event);
        }

        audioInstanceManager.getTrackManager(g).shuffleQueue();
        writeMessage("Shuffled queue successfully", event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to shuffle the queue.\n" +
                "To view this message write '"+ Prefixes.getPrefix(guildID) + "shuffle --help'.";
    }
}
