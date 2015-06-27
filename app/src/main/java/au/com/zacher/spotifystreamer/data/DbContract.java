package au.com.zacher.spotifystreamer.data;

import au.com.zacher.spotifystreamer.data.entry.DbEntry;
import au.com.zacher.spotifystreamer.data.entry.SearchHistoryEntry;

/**
 * Created by Brad on 27/06/2015.
 */
public class DbContract {
    public static final DbEntry[] Entries = {
            new SearchHistoryEntry()
    };

}

