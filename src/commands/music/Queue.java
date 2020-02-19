package commands.music;

import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static util.defaultMessageWriter.*;

public class Queue implements Command {

    AudioInstanceManager audioInstanceManager;

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
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
                return;
            } else {
                try {
                    sideNumb = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    writeError("Please write a number for the queue-page.", event);
                }
            }
        }

        if (audioInstanceManager.getPlayer(g).getPlayingTrack() == null) {
            writeError("The bot needs to be playing something!", event);
            return;
        }

        List<String> tracks = new ArrayList<>();
        List<String> trackSublist;
        audioInstanceManager.getTrackManager(g).getQueue().forEach(audioInfo -> tracks.add(audioInstanceManager.buildQueueMessage(audioInfo)));
        if (tracks.size() > 20)
            trackSublist = tracks.subList((sideNumb-1)*20, (sideNumb-1)*20+20);
        else
            trackSublist = tracks;
        String out = trackSublist.stream().collect(Collectors.joining("\n"));
        int sideNumbAll = 1;
        if (tracks.size() > 20)
            sideNumb = tracks.size() / 20;
        writePersistentMessage("**CURRENT QUEUE:**\n" +
                "*[" + "Tracks | Side " + sideNumb + " / " + sideNumbAll + "]*\n" + out, event);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to view the current queue. Add a number for the correct page of the queue.";
    }
}
