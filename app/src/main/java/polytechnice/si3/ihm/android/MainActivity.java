package polytechnice.si3.ihm.android;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static boolean mustInitDB = false;

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
                new User(false, "User1"),
                new User(false, "User2"),
                new User(true, "Admin1")
        );

        categoryViewModel.insert(
                new Category(1, "Pertes"),
                new Category(2, "Dégâts"),
                new Category(3, "Inquiétude"));

        importanceViewModel.insert(
                new Importance(1, "Faible"),
                new Importance(2, "Moyenne"),
                new Importance(3, "Forte"));

        progressViewModel.insert(
                new Progress(1, "TODO"),
                new Progress(2, "DOING"),
                new Progress(3, "DONE"));


        //Get all users
        userViewModel.getAll().observeForever(userList -> {
            if (userList != null && !userList.isEmpty()) {
                Log.i(TAG + "_initDB", userList.toString());

                issueViewModel.insert(
                        new Issue(userList.get(0).getId(), userList.get(1).getId(),
                                "Je veux un raton laveur", "Il est trop mignon",
                                "https://i.imgur.com/VVWVgxp.png", new Date().toString(),
                                1, 1, 2),
                        new Issue(userList.get(1).getId(), userList.get(2).getId(),
                                "Vase cassé", "Je balance pas, mais le vase est cassé",
                                "https://i.imgur.com/URVyanB.png", new Date().toString(),
                                1, 3, 2),
                        new Issue(userList.get(userList.size() - 1).getId(), userList.get(0).getId(),
                                "On me suit", "Je me sens épié depuis quelques temps",
                                "http://dw.convertfiles.com/files/0960897001526757786/015%20%20%20%20les%20exploits%20ii.mp4",
                                new Date().toString(),
                                1, 2, 2)
                );
            }
        });
    }

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //region ========== Models ==========
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        IssueViewModel issueViewModel = ViewModelProviders.of(this).get(IssueViewModel.class);
        CategoryViewModel categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        ImportanceViewModel importanceViewModel = ViewModelProviders.of(this).get(ImportanceViewModel.class);
        ProgressViewModel progressViewModel = ViewModelProviders.of(this).get(ProgressViewModel.class);

        //endregion

        //region ========== Printing ========
        userViewModel.getAll().observeForever(this::print);
        issueViewModel.getAll().observeForever(this::print);
        categoryViewModel.getAll().observeForever(this::print);
        importanceViewModel.getAll().observeForever(this::print);
        progressViewModel.getAll().observeForever(this::print);

        //endregion


        if (mustInitDB)
            setupDB(userViewModel, issueViewModel, categoryViewModel, importanceViewModel, progressViewModel);

        //region ========== Printing ========
        userViewModel.getAll().observeForever(this::print);
        issueViewModel.getAll().observeForever(this::print);
        categoryViewModel.getAll().observeForever(this::print);
        importanceViewModel.getAll().observeForever(this::print);
        progressViewModel.getAll().observeForever(this::print);

        //endregion
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
                    return TodoFragment.newInstance(position);
                case 2:
                    return DoingFragment.newInstance(position);
                case 3:
                    return DoneFragment.newInstance(position);
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
