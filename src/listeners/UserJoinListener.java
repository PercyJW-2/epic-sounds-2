package listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.List;

public class UserJoinListener extends ListenerAdapter {
    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        isTheBotLeaving(event.getChannelLeft(), event.getJDA(), event.getGuild(), null);
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        isTheBotLeaving(event.getChannelLeft(), event.getJDA(), event.getGuild(), event.getChannelJoined());
    }

    private void isTheBotLeaving(VoiceChannel vc, JDA jda, Guild guild, VoiceChannel newChannel) {
        List<Member> memberList = vc.getMembers();
        if (memberList.size() == 1) {
            if (memberList.get(0).getUser().getId().equals(jda.getSelfUser().getId())) {
                guild.getAudioManager().closeAudioConnection();
                if (newChannel != null) {
                    new Thread(() -> {
                       while (guild.getAudioManager().isConnected()) {
                           try {
                               Thread.sleep(1000);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                       guild.getAudioManager().openAudioConnection(newChannel);
                       System.out.println("Switched voice channel");
                    }).start();
                }
            }
        }
    }
}
