package au.com.zacher.spotifystreamer.data.entry;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import au.com.zacher.spotifystreamer.data.DbColumn;

public abstract class DbEntry {
    /**
     * Creates the associated table in the {@link SQLiteDatabase}
     * @param db the database to use to create
     */
    public abstract void createTable(SQLiteDatabase db);

    /**
     * Upgrades the associated table in the {@link SQLiteDatabase} to the given version
     * @param db the database to upgrade
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    public abstract void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * Gets the name of the table associated with this {@link DbEntry}
     */
    public abstract String getTableName();

    /**
     * Gets a list of the {@link DbColumn} in the table
     */
    public abstract Map<String, DbColumn> getColumns();
}
