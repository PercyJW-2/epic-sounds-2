package util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EventContainer {
    private final JDA jda;
    private final Guild guild;
    private final Member member;
    private final Reply reply;
    private final boolean slash;

    public EventContainer(
            final JDA jda, final Guild guild, final Member member, final Reply reply, final boolean slash) {
        this.jda = jda;
        this.guild = guild;
        this.member = member;
        this.reply = reply;
        this.slash = slash;
    }

    public JDA getJDA() {
        return jda;
    }

    public Guild getGuild() {
        return guild;
    }

    public Member getMember() {
        return member;
    }

    public Reply getReply() {
        return reply;
    }

    public boolean isSlash() {
        return slash;
    }

    @FunctionalInterface
    public interface Reply {
        MessageHookContainer reply(final MessageEmbed embed);
    }
}
