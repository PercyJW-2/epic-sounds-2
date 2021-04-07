package audiocore;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class AudioInstanceManager {

    private static final int PLAYLIST_LIMIT = 1000;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS = new HashMap<>();
    private static final int FRAME_BUFFER_DURATION = 2000;
    private static final Logger LOG = LoggerFactory.getLogger(AudioInstanceManager.class);
    private boolean searchPlaylist;

    public AudioInstanceManager() {
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer(final Guild guild) {
        final AudioPlayer player = MANAGER.createPlayer();
        final TrackManager manager = new TrackManager(player);
        player.addListener(manager);

        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

        PLAYERS.put(guild, new AbstractMap.SimpleEntry<>(player, manager));

        return player;
    }

    private boolean hasPlayer(final Guild guild) {
        return PLAYERS.containsKey(guild);
    }

    public AudioPlayer getPlayer(final Guild guild) {
        if (hasPlayer(guild)) {
            return PLAYERS.get(guild).getKey();
        } else {
            return createPlayer(guild);
        }
    }

    public TrackManager getTrackManager(final Guild guild) {
        return PLAYERS.get(guild).getValue();
    }

    public boolean isIdle(final Guild guild) {
        return !hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null;
    }

    public void loadTrack(
            final String identifier, final Member author, final Message msg, final boolean pSearchPlaylist,
            final int playlistIndex, final boolean silent) {
        this.searchPlaylist = pSearchPlaylist;
        final Guild guild = author.getGuild();
        getPlayer(guild);
        MANAGER.setFrameBufferDuration(FRAME_BUFFER_DURATION);
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(final AudioTrack track) {
                getTrackManager(guild).enQueue(track, author, msg.getTextChannel());
                if (!silent)
                msg.getTextChannel().sendMessage(
                        buildSingleSongMsg(
                                track.getInfo(),
                                track.getDuration(),
                                author,
                                msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                ).queue();
            }

            @SuppressWarnings({"checkstyle:MultipleStringLiterals", "LineLength"})
            @Override
            public void playlistLoaded(final AudioPlaylist playlist) {
                if (searchPlaylist) {
                    for (int i = playlistIndex; i < (Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT)); i++) {
                        getTrackManager(guild).enQueue(playlist.getTracks().get(i), author, msg.getTextChannel());
                    }
                    if (!silent)
                    msg.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setDescription(":musical_note: **Playlist added!**")
                                    .addField("Title: ", playlist.getName(), true)
                                    .addField("Size: ", "`" + (playlist.getTracks().size() - playlistIndex) + " Songs`", true)
                                    .addBlankField(true)
                                    .addField("Requested by: ", (author.getNickname() == null) ? author.getEffectiveName() : author.getNickname(), true)
                                    .addField("Duration    : ", "`" + getPlaylistTimestamp(playlist) + " Minutes`", true)
                                    .addBlankField(true)
                                    .setFooter("Epic Sounds V2", msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .build()
                    ).queue();
                    LOG.info("Youtube search: Playlist: {}", identifier);
                } else {
                    getTrackManager(
                            guild).enQueue(playlist.getTracks().get(playlistIndex), author, msg.getTextChannel());
                    final AudioTrackInfo trackInfo = playlist.getTracks().get(playlistIndex).getInfo();
                    if (!silent)
                    msg.getTextChannel().sendMessage(
                            buildSingleSongMsg(
                                    trackInfo,
                                    playlist.getTracks().get(playlistIndex).getDuration(),
                                    author,
                                    msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    ).queue();
                    LOG.info("Youtube serach: Track: {}", identifier);
                }
            }

            @Override
            public void noMatches() {
                msg.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(Color.RED)
                                .setDescription("There was nothing to be found")
                        .build()
                ).queue();
            }

            @Override
            public void loadFailed(final FriendlyException exception) {
                LOG.error("ES IST EIN VERDAMMTER FEHLER AUFGETRETEN");
                LOG.error(exception.getMessage());
            }
        });
    }

    @SuppressWarnings("LineLength")
    private MessageEmbed buildSingleSongMsg(
            final AudioTrackInfo info, final long duration, final Member author, final String avatarURL) {
        return new EmbedBuilder()
                .setColor(Color.RED)
                .setDescription(":musical_note: **Song added!**")
                .addField("       Title: ", info.title, true)
                .addField("       Author:", info.author, true)
                .addBlankField(true)
                .addField("         URL:", info.uri, true)
                .addField("    Duration: ", "`" + (info.isStream ? "LIVE" : getTimestamp(duration) + " Minutes") + "`", true)
                .addBlankField(true)
                .addField("Requested by: ", (author.getNickname() == null) ? author.getEffectiveName() : author.getNickname(), false)
                .setFooter("Epic Sounds V2", avatarURL)
                .build();
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    public String getTimestamp(final long milis) {
        long seconds = milis / 1000;
        final long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        final long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String getPlaylistTimestamp(final AudioPlaylist playlist) {
        long allMilis = 0;
        for (final AudioTrack track : playlist.getTracks()) {
            allMilis += track.getDuration();
        }
        return getTimestamp(allMilis);
    }

    public void skip(final Guild guild) {
        if (isIdle(guild)) return;
        getPlayer(guild).stopTrack();
    }

    public void setVolume(final Guild guild, final int volume) {
        getPlayer(guild).setVolume(volume);
    }

    public int getVolume(final Guild guild) {
        return getPlayer(guild).getVolume();
    }

    public void stop(final Guild guild) {
        if (isIdle(guild)) return;
        getTrackManager(guild).purgeQueue();
        getPlayer(guild).stopTrack();
    }
}
