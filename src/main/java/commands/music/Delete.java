package commands.music;

import audiocore.AudioInfo;
import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;

import java.util.*;

import static util.DefaultMessageWriter.*;

public class Delete implements Command {

    private final AudioInstanceManager audioInstanceManager;
    private long guildID;

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
        guildID = g.getIdLong();
        List<Integer> toBeDeleted = new LinkedList<>();
        if (args != null) {
            for (String arg : args) {
                switch (arg) {
                    case "--help":
                    case "-h":
                        writePersistentMessage(help(), event);
                        return;
                    default:
                        int number = Integer.parseInt(arg);
                        if (number < 0) {
                            writeError("All Numbers need to be positive! It will be skipped", event);
                        }
                        toBeDeleted.add(number - 1);
                }
            }
        }

        if (audioInstanceManager.isIdle(g)) {
            writeError("There is nothing to delete", event);
        } else {
            LinkedList<AudioInfo> audioQueue = audioInstanceManager.getTrackManager(g).getRealQueue();
            if (audioQueue.size() < 2) {
                writeError("There is nothing that can be deleted.", event);
            } else {
                toBeDeleted.sort(Comparator.comparingInt(o -> o));
                for (int i = 0; i < toBeDeleted.size(); i++) {
                    if (toBeDeleted.get(i) - i >= audioQueue.size()) {
                        writeError("There is no song with the number " + toBeDeleted.get(i) + ". It will be skipped", event);
                        toBeDeleted.remove(i);
                        i--;
                    } else
                        audioQueue.remove(toBeDeleted.get(i) - i);
                }
            }
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to delete a song from the queue with its specified number." +
                "Usage: `" + Prefixes.getPrefix(guildID) + "delete [number1] [number2] [number3] ... [numberN]`";
    }
}
