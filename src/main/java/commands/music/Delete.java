package commands.music;

import audiocore.AudioInfo;
import audiocore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import util.EventContainer;
import util.Prefixes;

import java.util.*;

import static util.DefaultMessageWriter.*;

public class Delete implements Command {

    private final AudioInstanceManager audioInstanceManager;
    private long guildID;

    public Delete(final AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @SuppressWarnings("PMD.MissingBreakInSwitch")
    @Override
    public void action(final String[] args, final EventContainer event) {
        final Guild guild = event.getGuild();
        guildID = guild.getIdLong();
        final List<Integer> toBeDeleted = new LinkedList<>();
        if (args != null) {
            for (final String arg : args) {
                switch (arg) {
                    case "--help":
                    case "-h":
                        writeMessage(help(), event);
                        return;
                    default:
                        final int number = Integer.parseInt(arg);
                        if (number < 1) {
                            writeError("All Numbers need to be positive! It will be skipped", event);
                        }
                        toBeDeleted.add(number - 1);
                }
            }
        }

        delete(event, guild, toBeDeleted);
    }

    private void delete(final EventContainer event, final Guild guild, final List<Integer> toBeDeleted) {
        if (audioInstanceManager.isIdle(guild)) {
            writeError("There is nothing to delete", event);
        } else {
            final LinkedList<AudioInfo> audioQueue = audioInstanceManager.getTrackManager(guild).getRealQueue();
            if (audioQueue.size() < 2) {
                writeError("There is nothing that can be deleted.", event);
            } else {
                toBeDeleted.sort(Comparator.comparingInt(o -> o));
                int deletedTracks = 0;
                for (final Integer integer : toBeDeleted) {
                    if (integer - deletedTracks >= audioQueue.size()) {
                        writeError("There is no song with the number "
                                + integer
                                + ". It will be skipped", event);
                    } else {
                        audioQueue.remove(integer - deletedTracks);
                        deletedTracks++;
                    }
                }
            }
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this command to delete a song from the queue with its specified number."
                + "Usage: `" + Prefixes.getPrefix(guildID) + "delete [number1] [number2] [number3] ... [numberN]`";
    }
}
