package commands.music;

import audioCore.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;

import static util.DefaultMessageWriter.*;

public class Play implements Command {

    private final AudioInstanceManager audioInstanceManager;

    public Play (AudioInstanceManager audioInstanceManager) {
        this.audioInstanceManager = audioInstanceManager;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        boolean ytsearch = true;
        boolean playlist = false;
        boolean scsearch = false;

        Guild g = event.getGuild();

        if (args == null || args.length < 1) {
            if (audioInstanceManager.getPlayer(g).isPaused()) {
                audioInstanceManager.getPlayer(g).setPaused(false);
                writeMessage("Resumed Playback!", event);
                return;
            }
            writeError("Provide at least one argument and/or a searchkeyword.\nTo find out about the possible inputs write '" +
                              Prefixes.getPrefix(g.getIdLong()) +
                              "play --help'",event);
            return;
        }

        StringBuilder search = new StringBuilder();
        for (String s : args) {
            switch (s.toLowerCase()) {
                case "--help", "-h" -> {
                    writePersistentMessage(help(),event);
                    return;
                }
                case "--ytsearch", "-yts" -> ytsearch = true;
                case "--scsearch", "-scs" -> {
                    ytsearch = false;
                    scsearch = true;
                }
                case "--playlist", " -p" -> playlist = true;
                default -> search.append(s).append(" ");
            }
        }

        if (search.length() < 1) {
            writeError("Please give a valid source!", event);
            return;
        }

        String searchFinal = "";

        if (!(search.toString().startsWith("http://") || search.toString().startsWith("https://"))) {
            if (scsearch)
                searchFinal = search.insert(0, "scsearch:").toString();
            else if (ytsearch)
                searchFinal = search.insert(0, "ytsearch:").toString();
        } else {
            searchFinal = search.substring(0,search.length()-1);
        }

        if (!g.getAudioManager().isConnected()) {
            VoiceChannel vChan = event.getMember().getVoiceState().getChannel();
            audioInstanceManager.getPlayer(g);
            g.getAudioManager().openAudioConnection(vChan);
            if (audioInstanceManager.getPlayer(g).isPaused()) {
                audioInstanceManager.getPlayer(g).setPaused(false);
            }
        }
        audioInstanceManager.loadTrack(searchFinal, event.getMember(), event.getMessage(), playlist);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return """
                Use this command to add music to the queue.
                use '--help' or '-h' to view this page.
                use '--ytsearch' or '-yts' to search your music on Youtube. (This is enabled by default.)
                use '--scsearch' or '-scs' to search your music on Soundcloud.
                use '--playlist' or '-p' to add the all search-results to your queue.
                or just write the link to your music after the command.
                finally, when the playback was stopped just write the command to resume playback.""";
    }
}
