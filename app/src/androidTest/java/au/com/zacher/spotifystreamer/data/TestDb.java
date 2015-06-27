package au.com.zacher.spotifystreamer.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.view.Display;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import au.com.zacher.spotifystreamer.RandomString;
import au.com.zacher.spotifystreamer.data.entry.DbEntry;
import au.com.zacher.spotifystreamer.data.entry.SearchHistoryEntry;
import au.com.zacher.spotifystreamer.data.helper.ArtistSearchHistoryDbHelper;
import au.com.zacher.spotifystreamer.data.helper.DbHelper;
import au.com.zacher.spotifystreamer.data.helper.SearchHistoryDbHelper;
import au.com.zacher.spotifystreamer.model.DisplayItem;

/**
 * Created by Brad on 27/06/2015.
 */
public class TestDb extends AndroidTestCase {
    private void deleteDb() {
        this.mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() {
        deleteDb();
    }

    public void testCreateDb() throws Throwable {
        SQLiteDatabase db = new DbHelper(this.mContext).getWritableDatabase();
        assertTrue(db.isOpen());

        // check for the existence of all required tables
        HashSet<String> tableNameSet = new HashSet<>();
        for (DbEntry e : DbContract.Entries) {
            tableNameSet.add(e.getTableName());
        }
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: the database was not created correctly", cursor.moveToFirst());

        do {
            tableNameSet.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        assertTrue("Error: not all tables were created (missing: " + TextUtils.join(",", tableNameSet) + ")", tableNameSet.isEmpty());
        cursor.close();

        // check each table for all required columns
        for (DbEntry e : DbContract.Entries) {
            cursor = db.rawQuery("PRAGMA table_info(" + e.getTableName() + ")", null);
            assertTrue("Error: table " + e.getTableName() + " is missing", cursor.moveToFirst());

            HashSet<String> columnNameSet = new HashSet<>();
            for (DbColumn col : e.getColumns().values()) {
                columnNameSet.add(col.name);
            }

            int columnNameIndex = cursor.getColumnIndex("name");
            do {
                columnNameSet.remove(cursor.getString(columnNameIndex));
            } while (cursor.moveToNext());

            assertTrue("Error: table " + e.getTableName() + " was missing columns: " + TextUtils.join(",", columnNameSet), columnNameSet.isEmpty());
            cursor.close();
        }

        db.close();
    }

    public void testHistoryEntry() {
        List<DisplayItem> items;
        long seed = (new Date()).getTime();
        Random rand = new Random(seed);
        RandomString randomString = new RandomString(rand, 20);

        LinkedList<DisplayItem> randomItems = new LinkedList<>();

        int count = SearchHistoryDbHelper.HISTORY_MAX_COUNT + 1;
        for (int i = 0; i < count; i++) {
            randomItems.add(new DisplayItem(
                    randomString.nextString(),
                    randomString.nextString(),
                    randomString.nextString(),
                    randomString.nextString()
            ));
        }

        ArtistSearchHistoryDbHelper db = new ArtistSearchHistoryDbHelper(this.mContext);

        // test add
        DisplayItem item = randomItems.removeFirst();
        db.addHistory(item);
        items = db.getHistory();
        assertEquals("Error: there were more items than expected (" + seed + ")", items.size(), 1);
        assertTrue("Error: the item was not the same as expected (" + seed + ")", items.get(0).equals(item));

        // test the history limiter
        for (DisplayItem d : randomItems) {
            db.addHistory(d);
        }


        items = db.getHistory();
        assertEquals("Error: there were more history items than expected (" + seed + ")", items.size(), SearchHistoryDbHelper.HISTORY_MAX_COUNT);
        for (DisplayItem d : randomItems) {
            assertTrue("Error: the history did not contain the correct items", items.contains(d));
        }

        db.close();
    }
}
