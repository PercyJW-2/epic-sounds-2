package commands.music;

import audiocore.AudioInfo;
import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Queue;

import static util.DefaultMessageWriter.*;

public class Undo implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Undo (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        int count = 1;

        if (args != null && args.length > 0) {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
                return;
            } else {
                count = Integer.parseInt(args[0]);
                if (count < 1) {
                    writeError("Please write an positive number", event);
                    return;
                }
            }
        }

        if (audioInstanceManager.isIdle(g)) {
            writeError("There is nothing to undo", event);
        } else {
            if (audioInstanceManager.getTrackManager(g).getQueue().isEmpty()) {
                writeError("There is nothing that can be deleted.", event);
            } else {
                Queue<AudioInfo> audioQueue = audioInstanceManager.getTrackManager(g).getRealQueue();
                if (audioQueue.size() < count) {
                    writeError("Requested to many songs to be deleted", event);
                    return;
                }
                for (int i = audioQueue.size(); i > count; i--) {
                    audioQueue.add(audioQueue.poll());
                }
                for (int i = 0; i < count; i++) {
                    audioQueue.poll();
                }
                writeMessage("Deleted " + count + " the most recent additions to the queue.", event);
            }
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to delete the most recent addition to the queue.\n" +
                "Add a number to undo multiple songs.";
    }
}
