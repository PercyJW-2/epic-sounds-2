package util;

import Exceptions.SettingsNotFoundException;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings("PMD.ClassNamingConventions")
public class SpotifyApiRequester {
    private static SpotifyApi spotifyApi = null;

    protected SpotifyApiRequester() {
        throw new UnsupportedOperationException();
    }

    public static void clientCredentials() {
        if (spotifyApi == null) {
            try {
                final HashMap<String, String> settings = FileLoadingUtils.loadSettings();
                final String spotifyClientId = settings.get("Spotify_Client_ID");
                final String spotifyClientSecret = settings.get("Spotify_Client_Secret");
                spotifyApi = new SpotifyApi.Builder()
                        .setClientId(spotifyClientId)
                        .setClientSecret(spotifyClientSecret)
                        .build();
            } catch (IOException | SettingsNotFoundException e) {
                System.out.println("There were some Problems while loading the settings");
            }
        }
        try {
            final ClientCredentials clientCredentials = spotifyApi.clientCredentials().build().execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String getTrack(final String trackID) {
        clientCredentials();
        final GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackID).build();
        try {
            final Track track = getTrackRequest.execute();
            return track.getName() + "-" + track.getArtists()[0].getName();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        // haha
        return "never gonna give you up-Rick Astley";
    }

    public static String[] getPlaylist(final String playlistID) {
        clientCredentials();
        final GetPlaylistsItemsRequest getPlaylistsItems = spotifyApi.getPlaylistsItems(playlistID).build();
        try {
            final Paging<PlaylistTrack> playlist = getPlaylistsItems.execute();
            final PlaylistTrack[] items = playlist.getItems();
            String[] names = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                names[i] = items[i].getTrack().getName()
                        + "-"
                        + ((Track) items[i].getTrack()).getArtists()[0].getName();
            }
            return names;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return new String[0];
    }
}
