package commands.music;

import audioCore.AudioInfo;
import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static util.DefaultMessageWriter.*;

public class Queue implements Command {

    private static final int PAGE_SIZE = 10;

    private final AudioInstanceManager audioInstanceManager;

    public Queue (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        int sideNumb = 1;

        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.toLowerCase().equals("--help") || arg.toLowerCase().equals("-h")) {
                    writePersistentMessage(help(), event);
                    return;
                } else{
                    try {
                        sideNumb = Integer.parseInt(arg);
                    } catch (Exception e) {
                        writeError("Please write a number for the queue-page.", event);
                        return;
                    }
                }
            }
        }

        if (audioInstanceManager.getPlayer(g).getPlayingTrack() == null) {
            writeError("The bot needs to be playing something!", event);
        } else {
            Set<AudioInfo> tracks = audioInstanceManager.getTrackManager(g).getQueue();
            List<AudioInfo> allTracks = new LinkedList<>();

            long totalLength = 0;
            for (AudioInfo a : tracks) {
                totalLength += a.getTRACK().getDuration();
                allTracks.add(a);
            }

            int pageCount = (tracks.size()/PAGE_SIZE) + 1;
            if (sideNumb > pageCount || sideNumb < 1) {
                writeError("Please write a number within the range of 1 to " + pageCount, event);
            } else {
                int startValue = (sideNumb - 1) * PAGE_SIZE;
                int endValue = sideNumb * PAGE_SIZE;
                EmbedBuilder playlistMsg = new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription("**CURRENT QUEUE**")
                        .addField("Page:", sideNumb + "/" + pageCount, true)
                        .addField("Total length:", audioInstanceManager.getTimestamp(totalLength),true)
                        .setFooter("Epic Sounds V2", event.getJDA().getSelfUser().getEffectiveAvatarUrl());
                for (int i = startValue; i < (Math.min(endValue, tracks.size())); i++) {
                    playlistMsg.addField((i + 1) + ":",
                                    allTracks.get(i).getTRACK().getInfo().title +
                                    "\n`Duration: " +
                                    audioInstanceManager.getTimestamp(allTracks.get(i).getTRACK().getDuration()) +
                                    "`",
                            false);
                }
                System.out.println("Built queue-message");
                event.getChannel().sendMessage(playlistMsg.build()).queue();
            }
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to view the current queue.\n" +
                "Add a number for the correct page of the queue.";
    }
}
