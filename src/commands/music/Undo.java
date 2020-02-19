package commands.music;

import audioCore.AudioInfo;
import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Queue;
import java.util.Set;

import static util.defaultMessageWriter.*;

public class Undo implements Command {

    AudioInstanceManager audioInstanceManager;

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

        if (args != null && args.length > 0) {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h"))
                writePersistentMessage(help(), event); return;
        }

        if (audioInstanceManager.isIdle(g)) {
            writeError("There is nothing to undo", event);
        } else {
            if (audioInstanceManager.getTrackManager(g).getQueue().isEmpty()) {
                writeError("There is nothing that can be deleted.", event);
            } else {
                Queue<AudioInfo> audioQueue = audioInstanceManager.getTrackManager(g).getRealQueue();
                for (int i = audioQueue.size(); i > 1; i--) {
                    audioQueue.add(audioQueue.poll());
                }
                audioQueue.poll();
                writeMessage("Deleted the most recent addition to the queue.", event);
            }
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to delete the most recent addition to the queue.";
    }
}
