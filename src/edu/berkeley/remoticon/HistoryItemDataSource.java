package edu.berkeley.remoticon;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Comment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HistoryItemDataSource {

  // Database fields
  private SQLiteDatabase database;
  private SQLiteHelper dbHelper;
  private String[] allColumns = { SQLiteHelper.COLUMN_ID,
      SQLiteHelper.COLUMN_HISTORY_CHANNEL,
      SQLiteHelper.COLUMN_HISTORY_NAME,
      SQLiteHelper.COLUMN_HISTORY_TIME};

  public HistoryItemDataSource(Context context) {
    dbHelper = new SQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public HistoryItem createHistoryItem(int channel, String name, long time) {
    ContentValues values = new ContentValues();
    values.put(SQLiteHelper.COLUMN_HISTORY_CHANNEL, channel);
    values.put(SQLiteHelper.COLUMN_HISTORY_NAME, name);
    values.put(SQLiteHelper.COLUMN_HISTORY_TIME, time);
    long insertId = database.insert(SQLiteHelper.TABLE_HISTORY, null,
        values);
    Cursor cursor = database.query(SQLiteHelper.TABLE_HISTORY,
        allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    HistoryItem newHistoryItem = cursorToHistoryItem(cursor);
    cursor.close();
    return newHistoryItem;
  }

  public void deleteHistoryItem(HistoryItem hi) {
    long id = hi.getId();
    System.out.println("HistoryItem deleted with id: " + id);
    database.delete(SQLiteHelper.TABLE_HISTORY, SQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }

  public List<HistoryItem> getAllHistoryItems() {
    List<HistoryItem> historyItems = new ArrayList<HistoryItem>();

    Cursor cursor = database.query(SQLiteHelper.TABLE_HISTORY,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      HistoryItem hi = cursorToHistoryItem(cursor);
      historyItems.add(hi);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return historyItems;
  }

  private HistoryItem cursorToHistoryItem(Cursor cursor) {
    HistoryItem hi = new HistoryItem();
    hi.setId(cursor.getLong(0));
    hi.setChannel(cursor.getInt(1));
    hi.setName(cursor.getString(2));
    hi.setTime(cursor.getLong(3));
    return hi;
  }
}
