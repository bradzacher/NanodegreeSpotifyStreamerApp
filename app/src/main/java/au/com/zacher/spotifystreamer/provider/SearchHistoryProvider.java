package au.com.zacher.spotifystreamer.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import au.com.zacher.spotifystreamer.model.DisplayItem;

/**
 * Created by Brad on 14/06/2015.
 */
public abstract class SearchHistoryProvider extends SQLiteOpenHelper {
    private static final int HISTORY_MAX_COUNT = 10;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "search_history";
    private static final String TABLE_NAME = DATABASE_NAME;

    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (id TEXT PRIMARY KEY, type TEXT, image_url TEXT, title TEXT, subtitle TEXT, query_date DATETIME DEFAULT CURRENT_TIMESTAMP);";
    private static final String TABLE_DELETE = "DROP TABLE " + TABLE_NAME;

    private static SearchHistoryProvider currentDb;

    public SearchHistoryProvider(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TABLE_DELETE);
        this.onCreate(db);
    }

    /**
     * Inserts a new history entry and maintains the maximum history length
     * @param item
     */
    public void addHistory(DisplayItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        String type = getType();

        db.beginTransaction();
        try {
            // insert the new row
            ContentValues values = new ContentValues();
            values.put("id", item.id);
            values.put("type", type);
            values.put("image_url", item.imageUrl);
            values.put("title", item.title);
            values.put("subtitle", item.subtitle);
            try {
                db.insert(TABLE_NAME, null, values);
            } catch (SQLiteConstraintException ex) {} // ignore the duplicate constraint error

            // delete any rows over the max limit
            String[] typeArg = new String[] { type };
            long rowCount = DatabaseUtils.queryNumEntries(db, TABLE_NAME, "type = ?", typeArg);
            if (rowCount > HISTORY_MAX_COUNT) {
                db.delete(TABLE_NAME, "id NOT IN (SELECT id FROM " + TABLE_NAME + " WHERE type = ? ORDER BY query_date DESC LIMIT " + HISTORY_MAX_COUNT + ")", typeArg);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public List<DisplayItem> getHistory() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<DisplayItem> results = new ArrayList<DisplayItem>();

        db.beginTransaction();
        try {
            Cursor cur = db.query(TABLE_NAME, new String[] {"id", "image_url", "title", "subtitle"}, "type = ?", new String[] {this.getType()}, null, null, "query_date DESC");
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                do {
                    DisplayItem item = new DisplayItem(
                            cur.getString(0),
                            cur.getString(1),
                            cur.getString(2),
                            cur.getString(3)
                    );
                    results.add(item);

                    cur.moveToNext();
                } while (!cur.isAfterLast() && !cur.isClosed());
            }
        } finally {
            db.endTransaction();
        }
        db.close();

        return results;
    }

    /**
     * Gets the type to identify the history by in the table
     * @return
     */
    protected abstract String getType();
}
