package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInfo;
import epicsounds2.audiocore.AudioInstanceManager;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import epicsounds2.util.EventContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static epicsounds2.util.DefaultMessageWriter.*;

public class Queue implements Command {

    private static final int PAGE_SIZE = 10;
    private static final Logger LOG = LoggerFactory.getLogger(Queue.class);

    private final AudioInstanceManager audioInstanceManager;

    public Queue(final AudioInstanceManager audioInstanceManager,
                   final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
        jda.upsertCommand(invoke, description)
                .addOption(OptionType.INTEGER,
                        "pagenum",
                        "Number of the page of the queue",
                        false)
                .queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        int sideNumb = 1;

        if (args != null && args.length > 0) {
            for (final String arg : args) {
                if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-h")) {
                    writeMessage(help(), event);
                    return;
                } else {
                    try {
                        sideNumb = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        writeError("Please write a number for the queue-page.", event);
                        return;
                    }
                }
            }
        }

        executeAction(event, sideNumb);
    }

    private void executeAction(final EventContainer event, final int sideNumb) {
        final Guild guild = event.getGuild();
        if (audioInstanceManager.getPlayer(guild).getPlayingTrack() == null) {
            writeError("The bot needs to be playing something!", event);
        } else {
            final Set<AudioInfo> tracks = audioInstanceManager.getTrackManager(guild).getQueue();
            final List<AudioInfo> allTracks = new LinkedList<>();

            long totalLength = 0;
            for (final AudioInfo info : tracks) {
                totalLength += info.getTrack().getDuration();
                allTracks.add(info);
            }

            final int pageCount = tracks.size() / PAGE_SIZE + 1;
            if (sideNumb > pageCount || sideNumb < 1) {
                writeError("Please write a number within the range of 1 to " + pageCount, event);
            } else {
                final int startValue = (sideNumb - 1) * PAGE_SIZE;
                final int endValue = sideNumb * PAGE_SIZE;
                final EmbedBuilder playlistMsg = new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription("**CURRENT QUEUE**")
                        .addField("Page:", sideNumb + "/" + pageCount, true)
                        .addField("Total length:", audioInstanceManager.getTimestamp(totalLength), true)
                        .setFooter("Epic Sounds V2", event.getJDA().getSelfUser().getEffectiveAvatarUrl());
                for (int i = startValue; i < (Math.min(endValue, tracks.size())); i++) {
                    playlistMsg.addField((i + 1) + ":",
                            allTracks.get(i).getTrack().getInfo().title
                                    + "\n`Duration: "
                                    + audioInstanceManager.getTimestamp(allTracks.get(i).getTrack().getDuration())
                                    + "`",
                            false);
                }
                LOG.info("Built queue-message");
                event.getReply().reply(playlistMsg.build());
            }
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this command to view the current queue.\n"
                + "Add a number for the correct page of the queue.";
    }
}
