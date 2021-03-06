package me.chenfuduo.mybasicdemo;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AddFragmentActivity extends AppCompatActivity implements BlankFragment.MyCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fragment);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
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
}
