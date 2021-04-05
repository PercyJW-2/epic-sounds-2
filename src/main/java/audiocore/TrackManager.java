package audiocore;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer PLAYER;
    private final LinkedList<AudioInfo> QUEUE;

    public TrackManager(AudioPlayer PLAYER) {
        this.PLAYER = PLAYER;
        this.QUEUE = new LinkedList<>();
    }

    public void enQueue(AudioTrack track, Member author, TextChannel channel) {
        AudioInfo info = new AudioInfo(track, author, channel);
        QUEUE.add(info);

        if (PLAYER.getPlayingTrack() == null) {
            PLAYER.playTrack(track);
        }
    }

    public Set<AudioInfo> getQueue() {
        return new LinkedHashSet<>(QUEUE);
    }

    public LinkedList<AudioInfo> getRealQueue() {
        return QUEUE;
    }

    public void purgeQueue() {
        QUEUE.clear();
    }

    public void shuffleQueue() {
        List<AudioInfo> cQueue = new ArrayList<>(getQueue());
        AudioInfo current = cQueue.get(0);
        cQueue.remove(0);
        Collections.shuffle(cQueue);
        cQueue.add(0, current);
        purgeQueue();
        QUEUE.addAll(cQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioInfo info = QUEUE.element();
        GuildVoiceState voiceState = info.getAuthor().getVoiceState();
        if (voiceState == null) {
            System.out.println("No voice-state found");
            return;
        }
        VoiceChannel vChan = voiceState.getChannel();


        if (vChan == null)
            player.stopTrack();
        else
            new Thread(() -> info.getAuthor().getGuild().getAudioManager().openAudioConnection(vChan)).start();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        QUEUE.poll();
        if (!QUEUE.isEmpty())
            player.playTrack(QUEUE.peek().getTrack());
    }
}
