package audiocore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TrackManager extends AudioEventAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TrackManager.class);
    private final AudioPlayer player;
    @SuppressWarnings("PMD.LooseCoupling")
    private final LinkedList<AudioInfo> queue;

    public TrackManager(final AudioPlayer player) {
        super();
        this.player = player;
        this.queue = new LinkedList<>();
    }

    public void enQueue(final AudioTrack track, final Member author, final TextChannel channel) {
        final AudioInfo info = new AudioInfo(track, author, channel);
        queue.add(info);

        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
        }
    }

    public Set<AudioInfo> getQueue() {
        return new LinkedHashSet<>(queue);
    }

    @SuppressWarnings("PMD.LooseCoupling")
    public LinkedList<AudioInfo> getRealQueue() {
        return queue;
    }

    public void purgeQueue() {
        queue.clear();
    }

    public void shuffleQueue() {
        final List<AudioInfo> cQueue = new ArrayList<>(getQueue());
        final AudioInfo current = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, current);
        purgeQueue();
        queue.addAll(cQueue);
    }

    @Override
    public void onTrackStart(final AudioPlayer player, final AudioTrack track) {
        final AudioInfo info = queue.element();
        final GuildVoiceState voiceState = info.getAuthor().getVoiceState();
        if (voiceState == null) {
            LOG.warn("No voice-state found");
            return;
        }
        final VoiceChannel vChan = voiceState.getChannel();

        if (vChan == null)
            player.stopTrack();
        else
            new Thread(() -> info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan)).start();
    }

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        queue.poll();
        if (!queue.isEmpty())
            player.playTrack(queue.peek().getTrack());
    }
}
