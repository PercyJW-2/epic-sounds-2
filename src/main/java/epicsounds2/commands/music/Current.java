package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInstanceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import epicsounds2.util.EventContainer;
import epicsounds2.util.MessageHookContainer;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import static epicsounds2.util.DefaultMessageWriter.writeError;
import static epicsounds2.util.DefaultMessageWriter.writeMessage;

public class Current implements Command {

    private static final int DELAY = 5000;
    private final AudioInstanceManager audioInstanceManager;

    public Current(final AudioInstanceManager audioInstanceManager,
                   final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
        jda.upsertCommand(invoke, description).queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        if (args != null && args.length > 0
                && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))) {
                writeMessage(help(), event);
                return;
        }
        final Guild guild = event.getGuild();
        if (audioInstanceManager.isIdle(guild)) {
            writeError("There is nothing to be shown", event);
        } else {
            final AudioTrack track = audioInstanceManager.getPlayer(guild).getPlayingTrack();
            final AudioTrackInfo info = track.getInfo();
            final String avatarURL = event.getJDA().getSelfUser().getAvatarUrl();
            final MessageHookContainer msg = event.getReply().reply(
                    getAudioStatusEmbed(info, avatarURL, guild)
            );

            final Timer updateTimer = new Timer();
            //TODO remove Timer
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (track.getState() == AudioTrackState.PLAYING)
                        if (msg.getMsg() == null) {
                            if (msg.getHook().isExpired()) {
                                updateTimer.cancel();
                                msg.getHook()
                                        .getInteraction()
                                        .getMessageChannel()
                                        .sendMessage("For Live-Updates the Command needs to be typed again!")
                                        .queue();
                            } else {
                                msg.getHook().editOriginalEmbeds(getAudioStatusEmbed(info, avatarURL, guild)).queue();
                            }
                        } else {
                            msg.getMsg().editMessageEmbeds(getAudioStatusEmbed(info, avatarURL, guild)).queue();
                        }
                    else {
                        updateTimer.cancel();
                    }
                }
            }, DELAY, DELAY);
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this command to show the current progress of the currently playing song.";
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private double calculatePercentage(final AudioTrack track) {
        final double current = track.getPosition();
        final double length = track.getDuration();
        return (current / length) * 100;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private String buildStatusBar(final double percentage) {
        final int status = (int) Math.round(percentage / 2);
        final StringBuilder bar = new StringBuilder();
        for (int i = 0; i <= 50; i++) {
            if (i == status) {
                bar.append('>');
            } else {
                bar.append('\u2013');
            }
        }
        return bar.toString();
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private MessageEmbed getAudioStatusEmbed(
            final AudioTrackInfo info,
            final String botAvatarUrl,
            final Guild guild) {
        final AudioTrack track = audioInstanceManager.getPlayer(guild).getPlayingTrack();
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription("**CURRENT TRACK PLAYING:**")
                .addField("Title:", info.title, true)
                .addField("Author:", info.author, true)
                .addBlankField(true)
                .addField("URL:", info.uri, true)
                .addField(info.isStream
                                ? "Listening for"
                                : "Duration:",
                        "`" + audioInstanceManager.getTimestamp(track.getPosition())
                                + (info.isStream
                                        ? ""
                                        : "/" + audioInstanceManager.getTimestamp(track.getDuration())) + "`",
                        true)
                .addBlankField(true)
                .addField("Status:", info.isStream
                                                ? ":red_circle: Live"
                                                : "`" + buildStatusBar(calculatePercentage(track)) + "`",
                        false)
                .setFooter("Epic Sounds V2", botAvatarUrl)
                .build();
    }
}
