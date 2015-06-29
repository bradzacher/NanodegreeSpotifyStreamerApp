package au.com.zacher.spotifystreamer.adapter;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import au.com.zacher.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Brad on 29/06/2015.
 */
public class TrackListAdapter extends DisplayItemListAdapter<Track> {

    public TrackListAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    protected String getItemTitle(Track item) {
        return item.name;
    }

    @Override
    protected String getItemSubtitle(Track item) {
        return item.album.name;
    }

    @Override
    protected List<Image> getItemImages(Track item) {
        return item.album.images;
    }

    @Override
    protected String getItemId(Track item) {
        return item.id;
    }

    @Override
    protected Class getClickActivityClass() {
        Toast.makeText(this.getContext(), this.getContext().getResources().getString(R.string.artist_view_toast_text), Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    protected String getIdIntentExtraString() {
        return null;
    }

    @Override
    protected String getTitleIntentExtraString() {
        return null;
    }
}
