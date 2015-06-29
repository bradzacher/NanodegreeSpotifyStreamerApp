package au.com.zacher.spotifystreamer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import au.com.zacher.spotifystreamer.Logger;
import au.com.zacher.spotifystreamer.R;
import au.com.zacher.spotifystreamer.adapter.DisplayItemListAdapter;
import au.com.zacher.spotifystreamer.adapter.TrackListAdapter;
import kaaes.spotify.webapi.android.models.Track;


public class ArtistViewActivity extends DisplayItemListActivity<Track> {
    public static final String INTENT_EXTRA;
    static {
        Class c = ArtistViewActivity.class;
        INTENT_EXTRA = c.getPackage() + ".IntentExtra." + c.getName() + ".ArtistId";
    }
    private String artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.logActionCreate("ArtistViewActivity");
        super.onCreate(savedInstanceState);

        // fetch the artist ID from the intent
        Intent i = this.getIntent();
        this.artistId = i.getStringExtra(ArtistViewActivity.INTENT_EXTRA);

        ((TextView)this.findViewById(R.id.test)).setText(this.artistId);
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
