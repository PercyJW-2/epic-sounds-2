package commands.music;

import audioCore.AudioInstanceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

import static util.defaultMessageWriter.writeError;
import static util.defaultMessageWriter.writePersistentMessage;

public class Current implements Command {

    AudioInstanceManager audioInstanceManager;

    public Current (AudioInstanceManager audioInstanceManager) {
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
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
                return;
            }
        }
        if (audioInstanceManager.isIdle(g)) {
            writeError("There is nothing to be shown", event);
        } else {
            AudioTrack track = audioInstanceManager.getPlayer(g).getPlayingTrack();
            AudioTrackInfo info = track.getInfo();
            event.getChannel().sendMessage(
                    new EmbedBuilder()
                            .setColor(Color.BLUE)
                            .setDescription("**CURRENT TRACK PLAYING:**")
                            .addField("Title", info.title, false)
                            .addField("Duration", audioInstanceManager.getTimestamp(track.getPosition()) + "/" + audioInstanceManager.getTimestamp(track.getDuration()), true)
                            .addField("Author", info.author, false)
                            .build()
            ).queue();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to show the current progress of the currently playing song.";
    }
}
