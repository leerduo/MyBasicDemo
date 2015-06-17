package me.chenfuduo.mybasicdemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.chenfuduo.mybasicdemo.db.BookDbHelper;
import me.chenfuduo.mybasicdemo.db.MyBookContract;

public class MyDBTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dbtest);
    }

    public void insert(View view){
        BookDbHelper dbHelper = new BookDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name","Math");
        values.put("author","wujun");
        values.put("pages",453);
        values.put("price",34.4);
        db.insert(MyBookContract.BookEntry.TABLE_NAME,null,values);
        values.clear();
        values.put("name","Chinese");
        values.put("author","duoduo");
        values.put("pages",233);
        values.put("price",23.4);
        db.insert(MyBookContract.BookEntry.TABLE_NAME,null,values);
    }


    public void query(View view){
        BookDbHelper dbHelper = new BookDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        /**
         * Query the given URL, returning a {@link Cursor} over the result set.
         *
         * @param distinct true if you want each row to be unique, false otherwise.
         * @param table The table name to compile the query against.表名
         * @param columns A list of which columns to return. Passing null will
         *            return all columns, which is discouraged to prevent reading
         *            data from storage that isn't going to be used.指定查询的列名
         * @param selection A filter declaring which rows to return, formatted as an
         *            SQL WHERE clause (excluding the WHERE itself). Passing null
         *            will return all rows for the given table.指定where的约束条件
         * @param selectionArgs You may include ?s in selection, which will be
         *         replaced by the values from selectionArgs, in order that they
         *         appear in the selection. The values will be bound as Strings.为where中的占位符提供具体的值
         * @param groupBy A filter declaring how to group rows, formatted as an SQL
         *            GROUP BY clause (excluding the GROUP BY itself). Passing null
         *            will cause the rows to not be grouped.指定需要group by的列
         * @param having A filter declare which row groups to include in the cursor,
         *            if row grouping is being used, formatted as an SQL HAVING
         *            clause (excluding the HAVING itself). Passing null will cause
         *            all row groups to be included, and is required when row
         *            grouping is not being used.为group by后的结果进一步约束
         * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause
         *            (excluding the ORDER BY itself). Passing null will use the
         *            default sort order, which may be unordered.指定查询结果的排序方式
         * @param limit Limits the number of rows returned by the query,
         *            formatted as LIMIT clause. Passing null denotes no LIMIT clause.
         * @return A {@link Cursor} object, which is positioned before the first entry. Note that
         * {@link Cursor}s are not synchronized, see the documentation for more details.
         * @see Cursor
         */
        Cursor cursor = db.query(MyBookContract.BookEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex(MyBookContract.BookEntry.COLUMN_BOOK_NAME));
                String author = cursor.getString(cursor.getColumnIndex(MyBookContract.BookEntry.COLUMN_BOOK_AUTHOR));
                int pages = cursor.getInt(cursor.getColumnIndex(MyBookContract.BookEntry.COLUMN_BOOK_PAGES));
                double price = cursor.getDouble(cursor.getColumnIndex(MyBookContract.BookEntry.COLUMN_BOOK_PRICE));

                Log.e("Test","name:" + name + "-author:" + author + "-pages:" + pages + "-prices:" + price);

            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void update(View view){
        BookDbHelper dbHelper = new BookDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("price",10.99);
        db.update(MyBookContract.BookEntry.TABLE_NAME,values,"name = ?",new String[]{"Math"});

    }

    public void delete(View view){
        BookDbHelper dbHelper = new BookDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MyBookContract.BookEntry.TABLE_NAME,"pages > ?",new String[]{"400"});
    }

    public void transaction(View view){
        BookDbHelper dbHelper = new BookDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();//开启事务
        try {
            db.delete(MyBookContract.BookEntry.TABLE_NAME,null,null);
            /*
            //测试代码，测试事务保证操作的完整性
            if (true){
                throw  new NullPointerException();
            }*/

            ContentValues values = new ContentValues();
            values.put("name","English");
            values.put("author","Tom");
            values.put("pages",100);
            values.put("price",14.4);
            db.insert(MyBookContract.BookEntry.TABLE_NAME,null,values);
            db.setTransactionSuccessful();//事务已经执行成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();//结束事务
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_dbtest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
