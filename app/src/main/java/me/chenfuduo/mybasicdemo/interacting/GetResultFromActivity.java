package me.chenfuduo.mybasicdemo.interacting;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.chenfuduo.mybasicdemo.R;

public class GetResultFromActivity extends AppCompatActivity {

    public static final int PICK_CONTACT_REQUEST = 1;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_result_from);
        textView = (TextView) findViewById(R.id.tv);
    }

    public void click(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        //MIME:  "vnd.android.cursor.dir/phone_v2"
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                //打印的结果   content://com.android.contacts/data/1  我选择的是第一个联系人
                Log.e("Test",contactUri.toString());
                // We only need the NUMBER column, because there will be only one row in the result
                // /** Generic data column, the meaning is {@link #MIMETYPE} specific */   data1
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                // Do something with the phone number...
                textView.setText(number);
            }
        }

    }


}
