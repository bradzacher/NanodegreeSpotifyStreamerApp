package au.com.zacher.spotifystreamer.adapter;

import android.content.Context;

import java.util.List;

import au.com.zacher.spotifystreamer.activity.ArtistViewActivity;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by Brad on 7/06/2015.
 */
public class ArtistListAdapter extends SearchListAdapter<Artist> {

    public ArtistListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    protected String getItemTitle(Artist item) {
        return item.name;
    }

    @Override
    protected String getItemSubtitle(Artist item) {
        return null;
    }

    @Override
    protected List<Image> getItemImages(Artist item) {
        return item.images;
    }

    @Override
    protected String getItemId(Artist item) {
        return item.id;
    }

    @Override
    protected Class getClickActivityClass() {
        return ArtistViewActivity.class;
    }

    @Override
    protected String getClickActivityExtraString() {
        return ArtistViewActivity.INTENT_EXTRA;
    }
}
