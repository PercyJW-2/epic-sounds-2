package commands.music;

import audio_core.AudioInstanceManager;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.HashMap;

import static util.DefaultMessageWriter.writePersistentMessage;

public class BassBoost implements Command {

    HashMap<Guild, Boolean> activated = new HashMap<>();
    AudioInstanceManager audioInstanceManager;
    HashMap<Guild, Equalizer> equalizers = new HashMap<>();

    public BassBoost (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Guild g = event.getGuild();
        if (args != null && args.length > 0) {
            if (args[0].toLowerCase().equals("--help") || args[0].toLowerCase().equals("-h")) {
                writePersistentMessage(help(), event);
                return;
            }
        }
        if (!activated.containsKey(g) || activated.get(g)) {
            audioInstanceManager.getPlayer(g).setFilterFactory((track, format, output) -> {
                equalizers.put(g, new Equalizer(format.channelCount, output, new float[]{
                        0.2f,
                        0.2f,
                        0.1f,
                        0f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                        -0.1f,
                }));
                return Collections.singletonList(equalizers.get(g));
            });
            activated.put(g, true);
        } else {
            Equalizer eq = equalizers.get(g);
            for (int i = 0; i < 15; i++) {
                eq.setGain(i, 0);
            }
            activated.put(g, false);
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "";
    }
}
