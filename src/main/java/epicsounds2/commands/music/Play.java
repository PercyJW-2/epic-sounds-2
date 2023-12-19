package epicsounds2.commands.music;

import epicsounds2.audiocore.AudioInstanceManager;
import epicsounds2.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import epicsounds2.util.EventContainer;
import org.jetbrains.annotations.NotNull;
import epicsounds2.util.Prefixes;
import epicsounds2.util.SpotifyApiRequester;

import java.awt.*;
import java.util.Locale;
import java.util.Objects;

import static epicsounds2.util.DefaultMessageWriter.*;

public class Play implements Command {

    private final AudioInstanceManager audioInstanceManager;
    private boolean blockedDefaultLoading;

    public Play(final AudioInstanceManager audioInstanceManager,
                   final String invoke, final String description, final JDA jda) {
        this.audioInstanceManager = audioInstanceManager;
        jda.upsertCommand(invoke, description)
                .addOption(
                        OptionType.STRING,
                        "parameters",
                        "Contains different search-parameters, like -p -yts -scs or -h",
                        false
                ).addOption(
                        OptionType.STRING,
                        "song",
                        "Contains a link or a search query.",
                        false
                ).queue();
    }

    @Override
    public boolean called(final String[] args, final EventContainer event) {
        return false;
    }

    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "PMD.MissingBreakInSwitch"})
    @Override
    public void action(final String[] args, final EventContainer event) {
        final Guild guild = event.getGuild();

        if (args == null || args.length < 1) {
            if (audioInstanceManager.getPlayer(guild).isPaused()) {
                audioInstanceManager.getPlayer(guild).setPaused(false);
                writeMessage("Resumed Playback!", event);
                return;
            }
            writeError("Provide at least one argument and/or a searchkeyword.\n"
                    + "To find out about the possible inputs write '"
                    + Prefixes.getPrefix(guild.getIdLong())
                    + "play --help'", event);
            return;
        }
        boolean playlist = false;
        boolean scsearch = false;

        final StringBuilder search = new StringBuilder();
        for (final String arg : args) {
            switch (arg.toLowerCase(Locale.getDefault())) {
                case "--help":
                case "-h":
                    writeMessage(help(), event);
                    break;
                case "--ytsearch":
                case "-yts":
                    break;
                case "--scsearch":
                case "-scs":
                    scsearch = true;
                    break;
                case "--playlist":
                case "-p":
                    playlist = true;
                    break;
                default:
                    search.append(arg).append(' ');
            }
        }

        if (search.isEmpty()) {
            writeError("Please give a valid source!", event);
            return;
        }

        String searchFinal = search.toString();
        int playlistIndex = 0;

        if (searchFinal.startsWith("<") && searchFinal.endsWith(">")) {
            searchFinal = searchFinal.substring(1, searchFinal.length() - 2);
        }

        blockedDefaultLoading = false;
        if (searchFinal.startsWith("http://") || searchFinal.startsWith("https://")) {
            if (searchFinal.contains("open.spotify")) {
                searchFinal = getSpotifySongNames(event, playlist, searchFinal, playlistIndex);
            } else {
                searchFinal = searchFinal.substring(0, search.length() - 1);
                if (searchFinal.contains("&index=")) {
                    playlistIndex = searchFinal.toCharArray()[searchFinal.length() - 1] - '1';
                } else {
                    playlist = true;
                }
            }
        } else {
            if (scsearch)
                searchFinal = "scsearch:" + searchFinal;
            else
                searchFinal = "ytsearch:" + searchFinal;
        }

        startPlayback(event, guild, playlist, searchFinal, playlistIndex, blockedDefaultLoading);
    }

    @NotNull
    private String getSpotifySongNames(
            final EventContainer event,
            final boolean playlist,
            final String searchFinal,
            final int playlistIndex) {
        final String[] splitted = searchFinal.split("/");
        String output = "";
        if (splitted[splitted.length - 2].equals("track")) {
            String trackID = splitted[splitted.length - 1];
            trackID = trackID.substring(0, trackID.indexOf('?'));
            output = "ytsearch:" + SpotifyApiRequester.getTrack(trackID);
        } else if (splitted[splitted.length - 2].equals("playlist")) {
            blockedDefaultLoading = true;
            final String playlistID =
                    splitted[splitted.length - 1].substring(0, splitted[splitted.length - 1].indexOf('?'));
            final String[] queries = SpotifyApiRequester.getPlaylist(playlistID);
            for (final String query : queries) {
                audioInstanceManager
                        .loadTrack("ytsearch:" + query,
                                Objects.requireNonNull(event.getMember()),
                                event.getReply(), playlist, playlistIndex, true);
            }
            event.getReply().reply(
                    new EmbedBuilder()
                            .setColor(Color.RED)
                            .setDescription(":musical_note: **Playlist added!**")
                            .addField("Title: ", "Spotify Playlist", true)
                            .addField("Size: ", "`" + queries.length + " Songs`", true)
                            .addBlankField(true)
                            .addField("Requested by: ", (event.getMember().getNickname() == null)
                                    ? event.getMember().getEffectiveName()
                                    : event.getMember().getNickname(), true)
                            .setFooter("Epic Sounds V2",
                                    event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                            .build()
            );
        }
        return output;
    }

    private void startPlayback(
            final EventContainer event,
            final Guild guild,
            final boolean playlist,
            final String searchFinal,
            final int playlistIndex,
            final boolean blockedDefaultLoading) {
        if (!guild.getAudioManager().isConnected()) {
            final AudioChannel vChan =
                    Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();
            audioInstanceManager.getPlayer(guild);
            guild.getAudioManager().openAudioConnection(vChan);
            if (audioInstanceManager.getPlayer(guild).isPaused()) {
                audioInstanceManager.getPlayer(guild).setPaused(false);
            }
        }
        if (!blockedDefaultLoading)
            audioInstanceManager
                    .loadTrack(
                            searchFinal,
                            Objects.requireNonNull(event.getMember()),
                            event.getReply(),
                            playlist,
                            playlistIndex,
                            false);
    }

    @Override
    public void executed(final boolean success, final EventContainer event) {

    }

    @Override
    public String help() {
        return "Use this command to add music to the queue.\n"
                + "use '--help' or '-h' to view this page.\n"
                + "use '--ytsearch' or '-yts' to search your music on Youtube. (This is enabled by default.)\n"
                + "use '--scsearch' or '-scs' to search your music on Soundcloud.\n"
                + "use '--playlist' or '-p' to add the all search-results to your queue.\n"
                + "or just write the link to your music after the command.\n"
                + "finally, when the playback was stopped just write the command to resume playback.";
    }
}
