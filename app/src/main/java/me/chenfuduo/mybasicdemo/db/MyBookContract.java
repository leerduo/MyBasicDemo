package me.chenfuduo.mybasicdemo.db;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 2015/6/17.
 */
public final class MyBookContract {
    public MyBookContract() {
    }

    public static abstract class BookEntry implements BaseColumns{
        public static final String TABLE_NAME = "Book";

        public static final String COLUMN_BOOK_AUTHOR = "author";

        public static final String COLUMN_BOOK_PRICE = "price";

        public static final String COLUMN_BOOK_PAGES = "pages";

        public static final String COLUMN_BOOK_NAME = "name";
    }

}
