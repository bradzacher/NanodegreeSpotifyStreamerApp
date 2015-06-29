package au.com.zacher.spotifystreamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.HashMap;
import java.util.Locale;

import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.adapter.DisplayItemListAdapter;
import au.com.zacher.spotifystreamer.adapter.TrackListAdapter;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ArtistViewActivity extends DisplayItemListActivity<Track> {
    public static final String ID_INTENT_EXTRA;
    public static final String TITLE_INTENT_EXTRA;
    static {
        Class c = ArtistViewActivity.class;
        String base_package = c.getPackage() + ".IntentExtra." + c.getName();
        ID_INTENT_EXTRA = base_package + ".ArtistId";
        TITLE_INTENT_EXTRA = base_package + ".ArtistName";
    }
    private String artistId;
    private String artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.logActionCreate("ArtistViewActivity");

        super.onCreate(savedInstanceState);

        this.setProgressStatus(ProgressStatus.SEARCHING);

        // fetch the artist ID from the intent
        Intent i = this.getIntent();
        this.artistId = i.getStringExtra(ArtistViewActivity.ID_INTENT_EXTRA);
        this.artistName = i.getStringExtra(ArtistViewActivity.TITLE_INTENT_EXTRA);

        // set the title so it looks pretty
        this.getToolbar().setTitle(this.artistName);

        // for running back on the main thread
        final Handler mainHandler = new Handler(this.getApplicationContext().getMainLooper());

        // TODO - get country from access token profile
        String countryCode = Locale.getDefault().getCountry();

        SpotifyApi api = new SpotifyApi();
        HashMap<String, Object> queryParams = new HashMap<>();
        queryParams.put("country", countryCode);
        api.getService().getArtistTopTrack(this.artistId, queryParams, new Callback<Tracks>() {
            @Override
            public void success(final Tracks tracks, Response response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        DisplayItemListAdapter<Track> adapter = getDisplayItemListAdapter();
                        adapter.clear();
                        adapter.addAllItems(tracks.tracks);

                        if (tracks.tracks.size() > 0) {
                            setProgressStatus(ProgressStatus.LIST_READY);
                        } else {
                            setProgressStatus(ProgressStatus.NO_RESULTS);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                // TODO error handling
            }
        });
    }

    @Override
    protected DisplayItemListAdapter<Track> initListAdapter() {
        return new TrackListAdapter(this, R.layout.fragment_display_item);
    }

    @Override
    protected int getToolbarMenuId() {
        return R.menu.menu_main;
    }

    @Override
    protected int getNoResultsTextId() {
        return R.string.artist_view_no_results;
    }
}
