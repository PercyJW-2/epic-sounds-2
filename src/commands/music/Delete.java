package commands.music;

import audioCore.AudioInfo;
import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Queue;

import static util.defaultMessageWriter.*;

public class Delete implements Command {

    AudioInstanceManager audioInstanceManager;

    public Delete(AudioInstanceManager audioInstanceManager) {
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
            writeError("There is nothing to delete", event);
        } else {
            Queue<AudioInfo> audioQueue = audioInstanceManager.getTrackManager(g).getRealQueue();
            if (audioQueue.size() < 2) {
                writeError("There is nothing that can be deleted.", event);
            } else {
                if (audioQueue.size() < count) {
                    writeError("Requested to many songs to be deleted", event);
                    return;
                }
                audioQueue.add(audioQueue.poll());
                for (int i = 0; i < count; i++) {
                    audioQueue.poll();
                }
                for (int i = 1; i < audioQueue.size(); i++) {
                    audioQueue.add(audioQueue.poll());
                }
                writeMessage("Deleted " + count + " of the next tracks.", event);
            }
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this Command to delete the next song, that is going to be played.\n" +
                "Add a number to undo multiple songs.";
    }
}
