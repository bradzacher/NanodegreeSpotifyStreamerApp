package au.com.zacher.spotifystreamer.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.adapter.ArtistListAdapter;
import au.com.zacher.spotifystreamer.adapter.SearchListAdapter;
import au.com.zacher.spotifystreamer.provider.ArtistSearchHistoryProvider;
import au.com.zacher.spotifystreamer.provider.SearchHistoryProvider;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Brad on 14/06/2015.
 */
public class ArtistSearchActivity extends SearchActivity<Artist> {
    @Override
    protected SearchListAdapter<Artist> initListAdapter() {
        return new ArtistListAdapter(this, R.layout.fragment_search_item);
    }

    @Override
    protected void doQuery(String query, Map<String, Object> queryParams, final SearchActivity.QueryCallback<Artist> callback) {
        SpotifyApi api = new SpotifyApi();
        api.getService().searchArtists(query, queryParams, new Callback<ArtistsPager>() {
            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                // sort the artists by follower count
                Collections.sort(artistsPager.artists.items, new Comparator<Artist>() {
                    @Override
                    public int compare(Artist lhs, Artist rhs) {
                        return rhs.followers.total - lhs.followers.total;
                    }
                });

                callback.success(artistsPager.artists.items);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    @Override
    protected String getSearchQueryHint() {
        return getResources().getString(R.string.artist_search_hint);
    }

    @Override
    protected SearchHistoryProvider getSearchHistoryProvider() {
        return new ArtistSearchHistoryProvider(this);
    }

    @Override
    protected Artist createBasicItem(String id, String description, String imageUrl) {
        Artist artist = new Artist();
        artist.id = id;
        artist.images = new ArrayList<Image>(1);
        Image i = new Image();
        i.url = imageUrl;
        // don't need legit size because there's only 1 image
        i.height = -1;
        i.width = -1;
        artist.images.add(i);
        artist.name = description;

        return artist;
    }
}
