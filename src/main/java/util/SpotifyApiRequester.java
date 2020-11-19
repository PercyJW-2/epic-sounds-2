package util;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistsItemsRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SpotifyApiRequester {
    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId("48af4ea8b1a54ab98ec8aeaa6dbd3753")
            .setClientSecret("6cd5d1611a8447c5bdc33053b4d3245e")
            .build();

    private static final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

    public static void clientCredentials() {
        try {
            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static String getTrack(String trackID) {
        clientCredentials();
        final GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackID).build();
        try {
            final Track track = getTrackRequest.execute();
            return track.getName() + "-" + track.getArtists()[0].getName();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "never gonna give you up-Rick Astley"; //haha
    }

    public static String[] getPlaylist(String playlistID) {
        clientCredentials();
        final GetPlaylistsItemsRequest getPlaylistsItems = spotifyApi.getPlaylistsItems(playlistID).build();
        try {
            final Paging<PlaylistTrack> playlist = getPlaylistsItems.execute();
            PlaylistTrack[] items = playlist.getItems();
            String[] names = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                names[i] = ((Track) items[i].getTrack()).getName() + "-" + ((Track) items[i].getTrack()).getArtists()[0].getName();
            }
            return names;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
}
