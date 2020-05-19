package commands.music;

import audioCore.AudioInstanceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import static util.DefaultMessageWriter.writeError;
import static util.DefaultMessageWriter.writePersistentMessage;

public class Current implements Command {

    private final AudioInstanceManager audioInstanceManager;

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
            String avatarURL = event.getJDA().getSelfUser().getAvatarUrl();
            Message msg = event.getChannel().sendMessage(
                    getAudioStatusEmbed(info, avatarURL, g)
            ).complete();

            Timer updateTimer = new Timer();
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (track.getState() == AudioTrackState.PLAYING)
                        msg.editMessage(getAudioStatusEmbed(info, avatarURL, g)).queue();
                    else {
                        msg.delete().queue();
                        updateTimer.cancel();
                    }
                }
            }, 5000, 5000);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to show the current progress of the currently playing song.";
    }

    private double calculatePercentage (AudioTrack track) {
        double current = track.getPosition();
        double length = track.getDuration();
        return (current/length)*100;
    }

    private String buildStatusBar(double percentage) {
        int status = (int) Math.round(percentage/2);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i <= 50; i++) {
            if (i == status) {
                bar.append(">");
            } else {
                bar.append("\u2013");
            }
        }
        return bar.toString();
    }

    private MessageEmbed getAudioStatusEmbed(AudioTrackInfo info, String botAvatarUrl, Guild g) {
        AudioTrack track = audioInstanceManager.getPlayer(g).getPlayingTrack();
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription("**CURRENT TRACK PLAYING:**")
                .addField("Title:", info.title, true)
                .addField("Author:", info.author, true)
                .addBlankField(true)
                .addField("URL:", info.uri, true)
                .addField((info.isStream ? "Listening for" : "Duration:"), "`" +
                        audioInstanceManager.getTimestamp(track.getPosition()) +
                        (!info.isStream ? "/" + audioInstanceManager.getTimestamp(track.getDuration()) : "") +
                        "`", true)
                .addBlankField(true)
                .addField("Status:", (info.isStream ? ":red_circle: Live" : "`" + buildStatusBar(calculatePercentage(track)) + "`"), false)
                .setFooter("Epic Sounds V2", botAvatarUrl)
                .build();
    }
}
