package au.com.zacher.spotifystreamer.data.entry;

import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import au.com.zacher.spotifystreamer.data.ColumnType;
import au.com.zacher.spotifystreamer.data.DbColumn;

public final class SearchHistoryEntry extends DbEntry {
    public static final String TABLE_NAME = "search_history";

    public static final Map<String, DbColumn> columns;
    static {
        DbColumn col;
        Map<String, DbColumn> map = new HashMap<>();

        col = new DbColumn("id", ColumnType.TEXT);
        COLUMN_ID = col;
        map.put("COLUMN_ID", col);

        col = new DbColumn("type", ColumnType.TEXT);
        COLUMN_TYPE = col;
        map.put("COLUMN_TYPE", col);

        col = new DbColumn("image_url", ColumnType.TEXT);
        COLUMN_IMAGE_URL = col;
        map.put("COLUMN_IMAGE_URL", col);

        col = new DbColumn("title", ColumnType.TEXT);
        COLUMN_TITLE = col;
        map.put("COLUMN_TITLE", col);

        col = new DbColumn("subtitle", ColumnType.TEXT);
        COLUMN_SUBTITLE = col;
        map.put("COLUMN_SUBTITLE", col);

        col = new DbColumn("query_date", ColumnType.INTEGER);
        COLUMN_QUERY_TIME = col;
        map.put("COLUMN_QUERY_DATE", col);

        columns = Collections.unmodifiableMap(map);
    }

    /**
     * The unique spotify id of the item
     */
    public static final DbColumn COLUMN_ID;
    /**
     * The type of history entry (i.e. "artist" for artist search history items)
     */
    public static final DbColumn COLUMN_TYPE;
    /**
     * The url of the image associated with this entry
     */
    public static final DbColumn COLUMN_IMAGE_URL;
    /**
     * The title of this entry
     */
    public static final DbColumn COLUMN_TITLE;
    /**
     * The subtitle, or tag-line, of this entry
     */
    public static final DbColumn COLUMN_SUBTITLE;
    /**
     * The date that this entry was last found
     */
    public static final DbColumn COLUMN_QUERY_TIME;

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID.columnString() + "," +
                    COLUMN_TYPE.columnString() + "," +
                    COLUMN_IMAGE_URL.columnString() + "," +
                    COLUMN_TITLE.columnString() + "," +
                    COLUMN_SUBTITLE.columnString() + "," +
                    COLUMN_QUERY_TIME.columnString() +
                ")";
    private static final String DROP_TABLE = "DROP TABLE" + TABLE_NAME;

    @Override
    public void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
        // we don't care for the existing data
        db.execSQL(DROP_TABLE);
        createTable(db);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, DbColumn> getColumns() {
        return columns;
    }
}
