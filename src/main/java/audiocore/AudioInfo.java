package audiocore;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import util.EventContainer;


public class AudioInfo {

    private final AudioTrack track;
    private final Member author;
    private final EventContainer.Reply reply;

    public AudioInfo(final AudioTrack track, final Member author, final EventContainer.Reply reply) {
        this.track = track;
        this.author = author;
        this.reply = reply;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public Member getAuthor() {
        return author;
    }

    public EventContainer.Reply getReply() {
        return reply;
    }
}
