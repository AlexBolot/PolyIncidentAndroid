package polytechnice.si3.ihm.android;

import android.arch.lifecycle.ViewModelProviders;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import polytechnice.si3.ihm.android.Incidents.DoingFragment;
import polytechnice.si3.ihm.android.Incidents.DoneFragment;
import polytechnice.si3.ihm.android.Incidents.TodoFragment;
import polytechnice.si3.ihm.android.database.model.Category;
import polytechnice.si3.ihm.android.database.model.Importance;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.Progress;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.CategoryViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ImportanceViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ProgressViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.UserViewModel;

public class VisualizationActivity extends AppCompatActivity {

    private static String TAG = "VisualizationActivity";
    private static boolean mustInitDB = false;
    private User userConnected;

    //region Database
    private void setupDB(UserViewModel userViewModel, IssueViewModel issueViewModel,
                         CategoryViewModel categoryViewModel, ImportanceViewModel importanceViewModel,
                         ProgressViewModel progressViewModel) {

        //region ========== Deletions =======
        issueViewModel.deleteAll();
        userViewModel.deleteAll();
        importanceViewModel.deleteAll();
        categoryViewModel.deleteAll();
        progressViewModel.deleteAll();

        userViewModel.insert(
                new User(false, "User1", "0621236433"),
                new User(false, "User2", "0621246433"),
                new User(true, "Admin1", "0621256333"));

        categoryViewModel.insert(
                new Category("Pertes"),
                new Category("Dégâts"),
                new Category("Inquiétude"));

        importanceViewModel.insert(
                new Importance("Faible"),
                new Importance("Moyenne"),
                new Importance("Forte"));

        progressViewModel.insert(
                new Progress("En cours"),
                new Progress("En cours de traitement"),
                new Progress("Traité"));


        Date today = new Date();

        final SimpleDateFormat formatter = new SimpleDateFormat("'Le' dd/MM/yyyy 'à' hh:mm");

        //Get all users
        userViewModel.getAll().observeForever(userList -> {
            if (userList != null && !userList.isEmpty()) {
                Log.i(TAG + "_initDB", userList.toString());

                issueViewModel.insert(
                        new Issue(userList.get(0).getId(), userList.get(1).getId(),
                                "Je veux un raton laveur", "Il est trop mignon",
                                "https://i.imgur.com/VVWVgxp.png", formatter.format(today),
                                1, 2, 2, "0621236433"),
                        new Issue(userList.get(1).getId(), userList.get(2).getId(),
                                "Vase cassé", "Je balance pas, mais le vase est cassé",
                                "https://i.imgur.com/URVyanB.png", formatter.format(today),
                                1, 3, 2, "0621236433"),
                        new Issue(userList.get(userList.size() - 1).getId(), userList.get(0).getId(),
                                "On me suit", "Je me sens épié depuis quelques temps",
                                "https://dl.dropboxusercontent.com/s/j1oog5oud6e6res/038%20%20%20%20le%20rassemblement%20du%20corbeau%20ii.mp4",
                                formatter.format(today),
                                1, 1, 2, "0621236433"),
                        new Issue(userList.get(userList.size() - 1).getId(), userList.get(0).getId(),
                                "On me suit", "J'ai faim.",
                                "https://dl.dropboxusercontent.com/s/j1oog5oud6e6res/038%20%20%20%20le%20rassemblement%20du%20corbeau%20ii.mp4",
                                formatter.format(today),
                                1, 1, 2, "0621236433")
                );
            }
        });
    }

    //endregion

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

        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.getAll().observe(this, users -> {
            if (users != null && !users.isEmpty())
                users.stream().findAny().ifPresent(user -> {
                    userViewModel.logIn(user);
                    Log.d(TAG, "Logged in : " + user);
                });
        });

        FloatingActionButton btnAdd = findViewById(R.id.float_add);
        btnAdd.setOnClickListener(view -> {
            Intent addView = new Intent(this, AddingActivity.class);
            userViewModel.getLoggedIn().ifPresent(user -> addView.putExtra("LoggedIn", user.getId()));
            startActivity(addView);
        });

        FloatingActionButton btnProfile = findViewById(R.id.float_profile);
        btnProfile.setOnClickListener(view -> {
            Intent profileView = new Intent(this, ProfileActivity.class);
            userConnected.feedIntent(profileView);
            startActivity(profileView);
        });
    }

    private void print(List list) {
        if (list != null && !list.isEmpty()) Log.i(TAG, list.toString());
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
                    return TodoFragment.newInstance(position, userConnected);
                case 2:
                    return DoingFragment.newInstance(position, userConnected);
                case 3:
                    return DoneFragment.newInstance(position, userConnected);
                default:
                    return PlaceholderFragment.newInstance(position);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
