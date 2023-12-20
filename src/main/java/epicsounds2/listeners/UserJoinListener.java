package epicsounds2.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

public class UserJoinListener extends ListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(UserJoinListener.class);

    private static final int FREQUENCY = 1000;


    @Override
    public void onGuildVoiceUpdate(@Nonnull final GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft() == null) {
            return;
        }
        isTheBotLeaving(event.getChannelLeft(), event.getJDA(), event.getGuild(), event.getChannelJoined());
    }

    private void isTheBotLeaving(final AudioChannel vc,
                                 final JDA jda,
                                 final Guild guild,
                                 final AudioChannel newChannel) {
        final List<Member> memberList = vc.getMembers();
        if (memberList.size() == 1
            && memberList.get(0).getUser().getId().equals(jda.getSelfUser().getId())) {
            guild.getAudioManager().closeAudioConnection();
            if (newChannel != null) {
                new Thread(() -> {
                    try {
                        Thread.sleep(FREQUENCY);
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage());
                    }
                    guild.getAudioManager().openAudioConnection(newChannel);
                    LOG.info("Switched voice channel");
                }).start();
            }
        }
    }
}
