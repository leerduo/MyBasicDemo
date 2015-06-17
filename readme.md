#ActionBar部分需要注意的是：
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


















