package au.com.zacher.spotifystreamer.data.helper;

import android.content.Context;

import au.com.zacher.spotifystreamer.data.helper.SearchHistoryDbHelper;

/**
 * Created by Brad on 14/06/2015.
 */
public class ArtistSearchHistoryDbHelper extends SearchHistoryDbHelper {
    public static final String TYPE = "Artist";

    public ArtistSearchHistoryDbHelper(Context context) {
        super(context);
    }

    @Override
    protected String getType() {
        return TYPE;
    }
}
