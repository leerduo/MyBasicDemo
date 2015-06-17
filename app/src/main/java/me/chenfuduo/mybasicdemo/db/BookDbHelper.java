package me.chenfuduo.mybasicdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/6/17.
 */
public class BookDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "book.db";

    private static final String CREATE_TABLE_BOOK = "CREATE TABLE " + MyBookContract.BookEntry.TABLE_NAME +
            " (" + MyBookContract.BookEntry._ID + " INTEGER PRIMARY KEY," +
            MyBookContract.BookEntry.COLUMN_BOOK_AUTHOR + " TEXT, " +
            MyBookContract.BookEntry.COLUMN_BOOK_PRICE + " REAL, " +
            MyBookContract.BookEntry.COLUMN_BOOK_PAGES + " INTEGER, " +
            MyBookContract.BookEntry.COLUMN_BOOK_NAME + " TEXT)";

    private static final String DELETE_TABLE_BOOK = "DROP TABLE IF EXISTS " + MyBookContract.BookEntry.TABLE_NAME;


    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DELETE_TABLE_BOOK);
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
