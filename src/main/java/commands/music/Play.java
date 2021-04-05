package commands.music;

import audio_core.AudioInstanceManager;
import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Prefixes;
import util.SpotifyApiRequester;

import java.awt.*;
import java.util.Objects;

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
        boolean blockedDefaultLoading = false;

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
                case "--help":
                case "-h":
                    writePersistentMessage(help(),event);
                    break;
                case "--ytsearch":
                case "-yts":
                    ytsearch = true;
                    break;
                case "--scsearch":
                case "-scs":
                    ytsearch = false;
                    scsearch = true;
                    break;
                case "--playlist":
                case "-p":
                    playlist = true;
                    break;
                default:
                    search.append(s).append(" ");
            }
        }

        if (search.length() < 1) {
            writeError("Please give a valid source!", event);
            return;
        }

        String searchFinal = search.toString();
        int playlistIndex = 0;

        if (searchFinal.startsWith("<") && searchFinal.endsWith(">")) {
            searchFinal = searchFinal.substring(1, searchFinal.length() - 2);
        }

        if (!(searchFinal.startsWith("http://") || searchFinal.startsWith("https://"))) {
            if (scsearch)
                searchFinal = "scsearch:" + searchFinal;
            else if (ytsearch)
                searchFinal = "ytsearch:" + searchFinal;
        } else {
            if (searchFinal.contains("open.spotify")) {
                String[] splitted = searchFinal.split("/");
                if (splitted[splitted.length - 2].equals("track")) {
                    String trackID = splitted[splitted.length - 1];
                    trackID = trackID.substring(0, trackID.indexOf("?"));
                    searchFinal = "ytsearch:" + SpotifyApiRequester.getTrack(trackID);
                } else if (splitted[splitted.length - 2].equals("playlist")) {
                    blockedDefaultLoading = true;
                    String playlistID = splitted[splitted.length - 1].substring(0, splitted[splitted.length - 1].indexOf("?"));
                    String[] queries = SpotifyApiRequester.getPlaylist(playlistID);
                    for (String s: queries) {
                        audioInstanceManager
                                .loadTrack("ytsearch:" + s, Objects.requireNonNull(event.getMember()), event.getMessage(), playlist, playlistIndex, true);
                    }
                    event.getTextChannel().sendMessage(
                            new EmbedBuilder()
                                    .setColor(new Color(255, 0, 0))
                                    .setDescription(":musical_note: **Playlist added!**")
                                    .addField("       Title: ", "Spotify Playlist", true)
                                    .addField("        Size: ", "`" + queries.length + " Songs`", true)
                                    .addBlankField(true)
                                    .addField("Requested by: ", (event.getMember().getNickname() == null) ? event.getMember().getEffectiveName() : event.getMember().getNickname() + "", true)
                                    .setFooter("Epic Sounds V2", event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                                    .build()
                    ).queue();
                }
            } else {
                searchFinal = searchFinal.substring(0, search.length() - 1);
                if (searchFinal.contains("&index=")) {
                    playlistIndex = searchFinal.toCharArray()[searchFinal.length() - 1] - '1';
                } else {
                    playlist = true;
                }
            }
        }

        if (!g.getAudioManager().isConnected()) {
            VoiceChannel vChan = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
            audioInstanceManager.getPlayer(g);
            g.getAudioManager().openAudioConnection(vChan);
            if (audioInstanceManager.getPlayer(g).isPaused()) {
                audioInstanceManager.getPlayer(g).setPaused(false);
            }
        }
        if (!blockedDefaultLoading)
            audioInstanceManager
                    .loadTrack(searchFinal, Objects.requireNonNull(event.getMember()), event.getMessage(), playlist, playlistIndex, false);
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return "Use this command to add music to the queue.\n" +
                "use '--help' or '-h' to view this page.\n" +
                "use '--ytsearch' or '-yts' to search your music on Youtube. (This is enabled by default.)\n" +
                "use '--scsearch' or '-scs' to search your music on Soundcloud.\n" +
                "use '--playlist' or '-p' to add the all search-results to your queue.\n" +
                "or just write the link to your music after the command.\n" +
                "finally, when the playback was stopped just write the command to resume playback.";
    }
}
