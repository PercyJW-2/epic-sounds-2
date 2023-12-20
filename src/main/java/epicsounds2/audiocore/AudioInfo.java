package epicsounds2.audiocore;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import epicsounds2.util.EventContainer;


public record AudioInfo(AudioTrack track, Member author, EventContainer.Reply reply) {

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
