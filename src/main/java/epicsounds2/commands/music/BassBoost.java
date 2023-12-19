package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInstanceManager;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import epicsounds2.util.EventContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import epicsounds2.util.Pair;

import java.util.HashMap;
import java.util.Map;

import static epicsounds2.util.DefaultMessageWriter.writeMessage;

public class BassBoost implements Command {

    private static final float[] BOOSTED =
            {0.4f, 0.3f, 0.2f, 0.1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private static final int CHANNELS = 15;
    private static final Logger LOG = LoggerFactory.getLogger(BassBoost.class);

    private final AudioInstanceManager audioInstanceManager;
    private final Map<Guild, Pair<EqualizerFactory, Boolean>> equalizers = new HashMap<>();

    public BassBoost(final AudioInstanceManager audioInstanceManager,
                     final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
        jda.upsertCommand(invoke, description).queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        if (args != null && args.length > 0
            && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))) {
            writeMessage(help(), event);
            return;
        }
        final Guild guild = event.getGuild();
        if (!equalizers.containsKey(guild)) {
            final EqualizerFactory eqFactory = new EqualizerFactory();
            audioInstanceManager.getPlayer(guild).setFilterFactory(eqFactory);
            equalizers.put(guild, new Pair<>(eqFactory, false));
            LOG.info("Filter Created");
        }
        final Pair<EqualizerFactory, Boolean> currentEQ = equalizers.get(guild);
        final EqualizerFactory eq = currentEQ.getFirst();
        if (currentEQ.getSecond()) {
            for (int i = 0; i < CHANNELS; i++) {
                eq.setGain(i, 0f);
            }
            currentEQ.setSecond(false);
            writeMessage("Disabled Bass-Boost", event);
            LOG.info("Disabled");
        } else {
            for (int i = 0; i < BOOSTED.length; i++) {
                eq.setGain(i, BOOSTED[i]);
            }
            currentEQ.setSecond(true);
            writeMessage("Enabled Bass-Boost", event);
            LOG.info("Enabled");
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this Command to enable and disable Bass-Boost";
    }
}
