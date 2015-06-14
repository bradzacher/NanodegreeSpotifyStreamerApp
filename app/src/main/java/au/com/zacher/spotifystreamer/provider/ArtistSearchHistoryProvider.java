package au.com.zacher.spotifystreamer.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Brad on 14/06/2015.
 */
public class ArtistSearchHistoryProvider extends SearchHistoryProvider {
    public static final String TYPE = "Artist";

    public ArtistSearchHistoryProvider(Context context) {
        super(context);
    }

    @Override
    protected String getType() {
        return TYPE;
    }
}
