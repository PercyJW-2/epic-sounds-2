package commands.music;

import audiocore.AudioInfo;
import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import util.EventContainer;

import java.util.Queue;

import static util.DefaultMessageWriter.*;

public class Undo implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Undo(final AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        int count = 1;

        if (args != null && args.length > 0) {
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                writeMessage(help(), event);
                return;
            } else {
                count = Integer.parseInt(args[0]);
                if (count < 1) {
                    writeError("Please write an positive number", event);
                    return;
                }
            }
        }

        executeAction(event, count);
    }

    private void executeAction(final EventContainer event, final int count) {
        final Guild guild = event.getGuild();
        if (audioInstanceManager.isIdle(guild)) {
            writeError("There is nothing to undo", event);
        } else {
            if (audioInstanceManager.getTrackManager(guild).getQueue().isEmpty()) {
                writeError("There is nothing that can be deleted.", event);
            } else {
                final Queue<AudioInfo> audioQueue = audioInstanceManager.getTrackManager(guild).getRealQueue();
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
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this Command to delete the most recent addition to the queue.\n"
                + "Add a number to undo multiple songs.";
    }
}
