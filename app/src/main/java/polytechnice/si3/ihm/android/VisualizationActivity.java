package polytechnice.si3.ihm.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.getbase.floatingactionbutton.FloatingActionButton;

import polytechnice.si3.ihm.android.Incidents.DoingFragment;
import polytechnice.si3.ihm.android.Incidents.DoneFragment;
import polytechnice.si3.ihm.android.Incidents.TodoFragment;
import polytechnice.si3.ihm.android.database.model.User;

public class VisualizationActivity extends AppCompatActivity {

    private static String TAG = "VisualizationActivity";
    private User userConnected;
    private Runnable globalUpdateRunnable = this::recreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation);

        userConnected = new User(getIntent());
        Log.d(TAG + "_user_connected", userConnected.toString());

        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        CustomViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton btnAdd = findViewById(R.id.float_add);
        btnAdd.setOnClickListener(view -> {
            Intent addView = new Intent(this, AddingActivity.class);
            userConnected.feedIntent(addView);
            startActivityForResult(addView, 1);
        });

        FloatingActionButton btnSrch = findViewById(R.id.float_search);
        btnSrch.setOnClickListener(view -> {
            Intent searchView = new Intent(this, SearchActivity.class);
            userConnected.feedIntent(searchView);
            startActivity(searchView);
        });

        FloatingActionButton btnProfile = findViewById(R.id.float_profile);
        btnProfile.setOnClickListener(view -> {
            Intent profileView = new Intent(this, ProfileActivity.class);
            userConnected.feedIntent(profileView);
            startActivity(profileView);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //TODO If not administrator, hide 3rd item
            //Position starts at 0 but we need it to start at 1 :)
            position++;

            switch (position) {
                case 1:
                    return TodoFragment.newInstance(position, userConnected, globalUpdateRunnable);
                case 2:
                    return DoingFragment.newInstance(position, userConnected, globalUpdateRunnable);
                case 3:
                    return DoneFragment.newInstance(position, userConnected, globalUpdateRunnable);
                default:
                    return PlaceholderFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) recreate();
    }
}
