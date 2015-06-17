#ActionBar
* 修改ActionBar的样式：
```java
  <!-- the theme applied to the application or activity -->
    <style name="CustomActionBarTheme"
        parent="@style/Theme.AppCompat.Light.DarkActionBar">
        <item name="android:actionBarStyle">@style/MyActionBar</item>

        <!-- Support library compatibility -->
        <item name="actionBarStyle">@style/MyActionBar</item>
    </style>

    <!-- ActionBar styles -->
    <style name="MyActionBar"
        parent="@style/Widget.AppCompat.Light.ActionBar.Solid.Inverse">
        <item name="android:background">@color/actionbar_background</item>

        <!-- Support library compatibility -->
        <item name="background">@color/actionbar_background</item>
    </style>
```
其中，颜色码为：
```java
 <color name="actionbar_background">#F3822C</color>
```
之后调用即可。
* 添加返回上一层
在SecondActivity中，现在添加返回到MainActivity，修改清单文件中的配置信息：
```java
  <activity
            android:name=".SecondActivity"
            android:label="@string/title_activity_second"
            android:parentActivityName=".MainActivity">
            <meta-data
                android:name="android.support.PARENT_ACTIVITY"
                android:value=".MainActivity" />
        </activity>
```
之后在SecondActivity的onCreate(...)方法中添加下面的代码：
```java
 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
```
这样就可以了。
* ActionBar的Overlay模式
ActionBar占用了Activity的部分空间，可以调用show()和hide()方法来设置它的显示和隐藏，
但是这样的话，Activity会重新计算并且绘制。为了避免这样的弊端，可以使用ActionBar的Overlay模式，
在该模式下，ActionBar下的Activity的空间是模糊的，但是当ActionBar隐藏起来的时候，Activity不需要重新
计算和重新绘制。要启用该模式，在样式文件下修改：
```xml
 <style name="CustomActionBarTheme"
        parent="Theme.AppCompat">
        <item name="android:windowActionBarOverlay">true</item>

        <!-- Support library compatibility -->
        <item name="windowActionBarOverlay">true</item>
    </style>
```
同时在布局文件中添加下面的代码：
```xml
 android:paddingTop="?attr/actionBarSize"
```
比如：
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/holo_blue_dark"
    android:paddingTop="?attr/actionBarSize">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_world" />

</RelativeLayout>
```
注意，需要修改ActionBar样式文件的颜色为半透明。
#在Activity运行时添加Fragment
为了添加Fragment，需要得到事务，为了得到事务，需要得到FragmentManager，这是流式接口。
```java
// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.frame_container) != null){
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null){
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            BlankFragment fragment = new BlankFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.frame_container,fragment).commit();

        }
```
移除和替换Fragment和添加Fragemnt的思路一样，只不过需要注意的是：
当移除和替换Fragment并将事务提交到后退栈的时候，被移除的Fragment处于停止状态(stopped)，而不是消亡状态(destoryed)
如果用户导航获取恢复fragment，它会重新启动；如果没有将事务添加到后退栈，那么fragment在移除或者替换的时候就消亡了。
如下的代码：
```java
// Create fragment and give it an argument specifying the article it should show
                OtherFragment newFragment = new OtherFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.frame_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
```
#Fragment之间的通信
Fragment和Fragment之间不能直接通信，它们之间的通信基于它们的宿主Activity。
代码中，AddFragmentActivity是宿主Activity，BlankFragment是书名列表，OtherFragment显示书名。当点击
BlankFragment列表中的任意一项的时候，OtherFragment显示书名。
BlankFragment中定义一个接口：
```java
  MyCallback myCallback;

    public interface MyCallback {
        void onArticleSelected(int position);
    }
```
指定宿主Activity必须实现该接口，否则抛出异常：
```java
 @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //下面的代码保证了宿主Activity必须实现下面的接口，否则会抛出异常
        try {
            myCallback = (MyCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }
```
实现：
```java
list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCallback.onArticleSelected(position);
            }
        });
```
宿主Activity实现该接口：
```java
 @Override
    public void onArticleSelected(int position) {
        OtherFragment otherFragment = (OtherFragment) getSupportFragmentManager().findFragmentById(R.id.frame_article);
        if (otherFragment != null){
            otherFragment.updateTextView(position);
        }else{
            // Create fragment and give it an argument specifying the article it should show
            OtherFragment newFragment = new OtherFragment();
            Bundle args = new Bundle();
            args.putInt(OtherFragment.ARG_POSITION,position);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.frame_article, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }
```
完整的代码参见demo。
#保存文件
* 保存到内部
```java
 public void save(View view) {
        String myString = "Hello";
        try {
            FileOutputStream fos = openFileOutput("test.txt", MODE_PRIVATE);
            fos.write(myString.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createCacheFile(View view) {
        File file = getTempFile();
    }

    private File getTempFile() {
        String fileName = "Hello";
        File file = null;
        try {
            file = File.createTempFile(fileName, null, this.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
```
* 保存到外存储
首先需要读写的权限，其次需要判断SD卡的读写状态：
```java
/* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
```
文件的权限包括Public和Private,按需创建：
```java
/**
     * public权限  getExternalStoragePublicDirectory()
     * @param albumName
     * @return
     */
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("Test", "Directory not created");
        }
        return file;
    }

    /**
     * private权限  getExternalFilesDir()
     * @param context
     * @param albumName
     * @return
     */
    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("Test", "Directory not created");
        }
        return file;
    }
```
一些有用的api:
`getFreeSpace()` or `getTotalSpace()`去获取空间的大小。
#SQLite
* 定义表的属性的时候，实现`BaseColumn`接口，这样就可以得到`_ID`属性，这样方便后面的操作(比如CursorAdapter)
如下：
```java
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
```
创建数据库，使用`SQLiteOpenHelper`:
```java
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
```
> 需要注意的是：Because they can be long-running, be sure that you call `getWritableDatabase()` or `getReadableDatabase()` in a background thread, such as with `AsyncTask` or `IntentService`.



* 插入：
```java
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
```
此时，执行相应的adb语句，即可查看数据库。
首先`adb shell`进入到设备的控制台，然后`cd data/data`进入到`/data/data/`目录下，此时`ls`下，可以看到我们的程序的包名`me.chenfuduo.mybasicdemo`,
cd到包名下，再去执行ls，看到一个文件夹的名字是`databases`，cd到该目录，ls下，看到了`book.db`这不就是创建的数据库嘛。再执行`sqlite3 book.db`,
此时进入到sqlite的命令模式，`.table`命令可以查看数据库的表名,`.schema`可以查看创建数据库的语句，`select * from Book`，查看表的内容，看到了刚刚插入的数据。
* 更新
```java
public void update(View view){
        BookDbHelper dbHelper = new BookDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("price",10.99);
        db.update(MyBookContract.BookEntry.TABLE_NAME,values,"name = ?",new String[]{"Math"});

    }
```
在终端下执行上面介绍的sqlite命令，可以查看列的值已经被改变。
* 删除
```java
 public void delete(View view){
        BookDbHelper dbHelper = new BookDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MyBookContract.BookEntry.TABLE_NAME,"pages > ?",new String[]{"400"});
    }
```
在终端下执行上面介绍的sqlite命令，可以查看大于400页的书籍全部被删除了。
* 查询
查询的方法的参数很多，最少的也有七个。
```java
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
```
同时，因为只是查询数据库，不要写权限，只要获取它的读取权限即可。
#使用数据库事务
现在数据库中的数据比较陈旧，我们需要将其删除，在删除的同时，插入新的数据，数据库事务可以保证这一系列的任务全部完成。
```java
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
```
















