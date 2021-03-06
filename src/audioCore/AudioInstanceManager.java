package audioCore;

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

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class AudioInstanceManager {

    private static final int PLAYLIST_LIMIT = 1000;
    private static final AudioPlayerManager MANAGER = new DefaultAudioPlayerManager();
    private static final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> PLAYERS = new HashMap<>();
    private boolean searchPlaylist;

    public AudioInstanceManager() {
        AudioSourceManagers.registerRemoteSources(MANAGER);
    }

    private AudioPlayer createPlayer(Guild g) {
        AudioPlayer p = MANAGER.createPlayer();
        TrackManager m = new TrackManager(p);
        p.addListener(m);

        g.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(p));

        PLAYERS.put(g, new AbstractMap.SimpleEntry<>(p, m));

        return p;
    }

    private boolean hasPlayer(Guild g) {
        return PLAYERS.containsKey(g);
    }

    public AudioPlayer getPlayer (Guild g) {
        if (hasPlayer(g)) {
            return PLAYERS.get(g).getKey();
        } else {
            return createPlayer(g);
        }
    }

    public TrackManager getTrackManager (Guild g) {
        return PLAYERS.get(g).getValue();
    }

    public boolean isIdle(Guild g) {
        return !hasPlayer(g) || getPlayer(g).getPlayingTrack() == null;
    }

    public void loadTrack(String identifier, Member author, Message msg, boolean pSearchPlaylist) {
        this.searchPlaylist = pSearchPlaylist;
        Guild guild = author.getGuild();
        getPlayer(guild);
        MANAGER.setFrameBufferDuration(2000);
        MANAGER.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                getTrackManager(guild).enQueue(track, author, msg.getTextChannel());
                msg.getTextChannel().sendMessage(
                        new EmbedBuilder()
                                .setColor(new Color(255, 0, 0))
                                .setDescription(":musical_note: **Song added!**")
                                .addField("       Title: ", track.getInfo().title, true)
                                .addField("       Author:", track.getInfo().author, true)
                                .addBlankField(true)
                                .addField("         URL:", track.getInfo().uri, true)
                                .addField("    Duration: ", "`" + getTimestamp(track.getDuration()) + " Minutes`", true)
                                .addBlankField(true)
                                .addField("Requested by: ", (author.getNickname() == null) ? author.getEffectiveName() : author.getNickname() + "", false)
                                .setFooter("Epic Sounds V2", msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                .build()
                ).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (!searchPlaylist) {
                    getTrackManager(guild).enQueue(playlist.getTracks().get(0), author, msg.getTextChannel());
                    AudioTrackInfo trackInfo = playlist.getTracks().get(0).getInfo();
                    msg.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(new Color(255, 0, 0))
                                    .setDescription(":musical_note: **Song added!**")
                                    .addField("       Title: ", trackInfo.title, true)
                                    .addField("      Author: ", trackInfo.author, true)
                                    .addBlankField(true)
                                    .addField("         URL: ", trackInfo.uri, true)
                                    .addField("    Duration: ", "`" + getTimestamp(playlist.getTracks().get(0).getDuration()) + " Minutes`", true)
                                    .addBlankField(true)
                                    .addField("Requested by: ", (author.getNickname() == null) ? author.getEffectiveName() : author.getNickname() + "", false)
                                    .setFooter("Epic Sounds V2", msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .build()
                    ).queue();
                    System.out.println("YOUTUBE SUCHEN SONG, " + identifier);
                } else {
                    for (int i = 0; i < (Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT)); i++) {
                        getTrackManager(guild).enQueue(playlist.getTracks().get(i), author, msg.getTextChannel());
                    }
                    msg.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(new Color(255, 0, 0))
                                    .setDescription(":musical_note: **Playlist added!**")
                                    .addField("       Title: ", playlist.getName(), true)
                                    .addField("        Size: ", "`" + playlist.getTracks().size() + " Songs`", true)
                                    .addBlankField(true)
                                    .addField("Requested by: ", (author.getNickname() == null) ? author.getEffectiveName() : author.getNickname() + "", true)
                                    .addField("Duration: ", "`" + getPlaylistTimestamp(playlist) + " Minutes`", true)
                                    .addBlankField(true)
                                    .setFooter("Epic Sounds V2", msg.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .build()
                    ).queue();
                    System.out.println("YOUTUBE SUCHEN PLAYLIST, " + identifier);
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
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
            }
        });
    }

    public String getTimestamp(long milis) {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours +":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String getPlaylistTimestamp(AudioPlaylist playlist) {
        long allMilis = 0;
        for (AudioTrack t : playlist.getTracks()) {
            allMilis += t.getDuration();
        }
        return getTimestamp(allMilis);
    }

    public void skip (Guild g) {
        if (isIdle(g)) return;
        getPlayer(g).stopTrack();
    }

    public void setVolume (Guild g, int volume) {
        getPlayer(g).setVolume(volume);
    }

    public int getVolume (Guild g) {
        return getPlayer(g).getVolume();
    }

    public void stop (Guild g) {
        if (isIdle(g)) return;
        getTrackManager(g).purgeQueue();
        getPlayer(g).stopTrack();
    }

}
