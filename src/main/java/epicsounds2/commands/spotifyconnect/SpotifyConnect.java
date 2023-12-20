package epicsounds2.commands.spotifyconnect;

import epicsounds2.audiocore.AudioInstanceManager;
import com.spotify.connectstate.Connect;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import epicsounds2.util.EventContainer;
import epicsounds2.util.Pair;
import xyz.gianlu.librespot.ZeroconfServer;
import xyz.gianlu.librespot.core.Session;
import xyz.gianlu.librespot.player.Player;
import xyz.gianlu.librespot.player.PlayerConfiguration;

import java.io.IOException;

public class SpotifyConnect implements Command {

    private static Pair<Long, ZeroconfServer> currentServer;
    private final AudioInstanceManager audioInstanceManager;

    public SpotifyConnect(final AudioInstanceManager audioInstanceManager,
                     final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @Override
    public void action(final String[] args, final EventContainer event) {
        try {
            if (currentServer == null) {
                final ZeroconfServer server = new ZeroconfServer.Builder()
                        .setListenAll(true)
                        .setDeviceType(Connect.DeviceType.AUDIO_DONGLE)
                        .setDeviceName("Epic Sounds 2")
                        .create();
                currentServer = new Pair<>(event.getGuild().getIdLong(), server);
                server.addSessionListener(new ZeroconfServer.SessionListener() {
                    private Player lastPlayer = null;
                    @Override
                    public void sessionClosing(@NotNull final Session session) {
                        if (lastPlayer != null) lastPlayer.close();
                    }

                    @Override
                    public void sessionChanged(@NotNull final Session session) {
                        final PlayerConfiguration conf = new PlayerConfiguration.Builder()
                                .setOutput(PlayerConfiguration.AudioOutput.CUSTOM)
                                .setOutputClass("")
                                .build();
                        lastPlayer = new Player(conf, session);
                    }
                });
            } else {
                currentServer.getSecond().close();
                currentServer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return null;
    }
}
