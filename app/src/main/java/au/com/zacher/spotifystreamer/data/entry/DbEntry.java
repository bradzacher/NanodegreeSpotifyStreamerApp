package au.com.zacher.spotifystreamer.data.entry;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import au.com.zacher.spotifystreamer.data.DbColumn;

public abstract class DbEntry {
    /**
     * Creates the associated table in the database
     * @param db the database to use to create
     */
    public abstract void createTable(SQLiteDatabase db);

    /**
     * Upgrades the associated table to the given version
     * @param db the database to upgrade
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    public abstract void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * Gets the name of the table associated with this entry
     */
    public abstract String getTableName();

    /**
     * Gets a list of the columns in the table
     */
    public abstract Map<String, DbColumn> getColumns();
}
