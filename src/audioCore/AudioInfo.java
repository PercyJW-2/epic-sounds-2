package audioCore;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;


public class AudioInfo {

    private final AudioTrack TRACK;
    private final Member AUTHOR;
    private final TextChannel CHANNEL;

    public AudioInfo(AudioTrack track, Member author, TextChannel channel) {
        this.TRACK = track;
        this.AUTHOR = author;
        this.CHANNEL = channel;
    }

    public AudioTrack getTRACK() {
        return TRACK;
    }

    public Member getAUTHOR() {
        return AUTHOR;
    }

    public TextChannel getCHANNEL() {
        return CHANNEL;
    }
}
