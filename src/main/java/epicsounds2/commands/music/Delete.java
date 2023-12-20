package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInfo;
import epicsounds2.audiocore.AudioInstanceManager;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import epicsounds2.util.EventContainer;
import epicsounds2.util.Prefixes;

import java.util.*;

import static epicsounds2.util.DefaultMessageWriter.*;

public class Delete implements Command {

    private final AudioInstanceManager audioInstanceManager;
    private long guildID;

    public Delete(final AudioInstanceManager audioInstanceManager,
                   final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
        jda.upsertCommand(invoke, description).addOption(
                OptionType.STRING,
                "tracknumbers",
                "Numbers of tracks that are deleted",
                true
        ).queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @SuppressWarnings({"PMD.MissingBreakInSwitch", "PMD.SwitchStmtsShouldHaveDefault"})
    @Override
    public void action(final String[] args, final EventContainer event) {
        final Guild guild = event.getGuild();
        guildID = guild.getIdLong();
        final List<Integer> toBeDeleted = new LinkedList<>();
        if (args != null) {
            for (final String arg : args) {
                if (arg.equals("--help") || arg.equals("-h")) {
                    writeMessage(help(), event);
                    return;
                } else {
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
                final StringBuilder notDeletedTracks = new StringBuilder();
                int deletedTracks = 0;
                for (final Integer integer : toBeDeleted) {
                    if (integer - deletedTracks >= audioQueue.size()) {
                        notDeletedTracks.append(integer).append(' ');
                    } else {
                        audioQueue.remove(integer - deletedTracks);
                        deletedTracks++;
                    }
                }
                if (notDeletedTracks.length() == 0) {
                    writeError("There is/are no songs with the number "
                            + notDeletedTracks
                            + ". It will be skipped", event);
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
