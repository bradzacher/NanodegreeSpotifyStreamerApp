package au.com.zacher.spotifystreamer.data.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import au.com.zacher.spotifystreamer.data.entry.SearchHistoryEntry;
import au.com.zacher.spotifystreamer.model.DisplayItem;

/**
 * Created by Brad on 14/06/2015.
 */
public abstract class SearchHistoryDbHelper extends DbHelper {
    public static final int HISTORY_MAX_COUNT = 10;

    public SearchHistoryDbHelper(Context context) {
        super(context);
    }

    /**
     * Inserts a new history entry and maintains the maximum history length
     * @param item the item to add
     */
    public void addHistory(DisplayItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        String type = getType();

        db.beginTransaction();
        try {
            // insert the new row
            ContentValues values = new ContentValues();
            values.put(SearchHistoryEntry.COLUMN_ID.name, item.id);
            values.put(SearchHistoryEntry.COLUMN_TYPE.name, type);
            values.put(SearchHistoryEntry.COLUMN_IMAGE_URL.name, item.imageUrl);
            values.put(SearchHistoryEntry.COLUMN_TITLE.name, item.title);
            values.put(SearchHistoryEntry.COLUMN_SUBTITLE.name, item.subtitle);
            values.put(SearchHistoryEntry.COLUMN_QUERY_TIME.name, (new Date()).getTime()); // CURRENT_TIMESTAMP only has second precision, which makes testing hard
            try {
                db.insert(SearchHistoryEntry.TABLE_NAME, null, values);
            } catch (SQLiteConstraintException ignored) {} // ignore the duplicate constraint error

            // delete any rows over the max limit
            String[] typeArg = new String[] { type };
            long rowCount = DatabaseUtils.queryNumEntries(db, SearchHistoryEntry.TABLE_NAME, "type = ?", typeArg);
            if (rowCount > HISTORY_MAX_COUNT) {
                db.delete(SearchHistoryEntry.TABLE_NAME, "id NOT IN (" +
                        "SELECT id FROM " + SearchHistoryEntry.TABLE_NAME + " " +
                        "WHERE type = ? " +
                        "ORDER BY " + SearchHistoryEntry.COLUMN_QUERY_TIME.name + " " +
                        "DESC LIMIT " + HISTORY_MAX_COUNT + ")", typeArg);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * Gets the list of {@link SearchHistoryDbHelper#HISTORY_MAX_COUNT} items from the history
     */
    public List<DisplayItem> getHistory() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<DisplayItem> results = new ArrayList<>();

        String[] queryColumns = {
                SearchHistoryEntry.COLUMN_ID.name,
                SearchHistoryEntry.COLUMN_IMAGE_URL.name,
                SearchHistoryEntry.COLUMN_TITLE.name,
                SearchHistoryEntry.COLUMN_SUBTITLE.name
        };
        db.beginTransaction();
        try {
            Cursor cur = db.query(SearchHistoryEntry.TABLE_NAME,
                                    queryColumns,
                                    SearchHistoryEntry.COLUMN_TYPE.name + " = ?",
                                    new String[] {this.getType()},
                                    null,
                                    null,
                                    SearchHistoryEntry.COLUMN_QUERY_TIME.name + " DESC");
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                do {
                    DisplayItem item = new DisplayItem(
                            cur.getString(cur.getColumnIndex(SearchHistoryEntry.COLUMN_ID.name)),
                            cur.getString(cur.getColumnIndex(SearchHistoryEntry.COLUMN_IMAGE_URL.name)),
                            cur.getString(cur.getColumnIndex(SearchHistoryEntry.COLUMN_TITLE.name)),
                            cur.getString(cur.getColumnIndex(SearchHistoryEntry.COLUMN_SUBTITLE.name))
                    );
                    results.add(item);

                    cur.moveToNext();
                } while (!cur.isAfterLast() && !cur.isClosed());
            }
            cur.close();
        } finally {
            db.endTransaction();
        }
        db.close();

        return results;
    }

    /**
     * Gets the type to identify the history by in the table
     */
    protected abstract String getType();
}
