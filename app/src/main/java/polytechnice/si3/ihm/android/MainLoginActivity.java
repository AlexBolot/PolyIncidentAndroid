package polytechnice.si3.ihm.android;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

public class MainLoginActivity extends AppCompatActivity {

    private static String TAG = "MainLoginActivity";
    private UserViewModel userViewModel;

    private static boolean mustInitDB = true;

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
        setContentView(R.layout.activity_login);

        //region ========== Models ==========
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        IssueViewModel issueViewModel = ViewModelProviders.of(this).get(IssueViewModel.class);
        CategoryViewModel categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        ImportanceViewModel importanceViewModel = ViewModelProviders.of(this).get(ImportanceViewModel.class);
        ProgressViewModel progressViewModel = ViewModelProviders.of(this).get(ProgressViewModel.class);

        //endregion

        if (mustInitDB)
            setupDB(userViewModel, issueViewModel, categoryViewModel, importanceViewModel, progressViewModel);

        //region ========== Printing ========
        userViewModel.getAll().observeForever(this::print);
        issueViewModel.getAll().observeForever(this::print);
        categoryViewModel.getAll().observeForever(this::print);
        importanceViewModel.getAll().observeForever(this::print);
        progressViewModel.getAll().observeForever(progressList -> {
            if (progressList != null && !progressList.isEmpty()) {
                Log.i(TAG, progressList.stream().map(progress -> progress.getLabel() + "-" + progress.getId()).collect(Collectors.toList()).toString());
            }
        });

        //endregion

        Button connexionButton = findViewById(R.id.connexionButton);
        connexionButton.setOnClickListener(v -> {
            User connectedUser = getConnectedUser();

            if (connectedUser != null) {
                Intent intent = new Intent(this, VisualizationActivity.class);
                connectedUser.feedIntent(intent);
                this.startActivity(intent);
            }
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

    public User getConnectedUser() {
        EditText login = findViewById(R.id.login);
        EditText password = findViewById(R.id.password);
        Log.d(TAG + "_login", "Try to log : {" + login.getText().toString() + ", "
                + password.getText().toString() + "}");
        return userViewModel.getByNameAndPhoneNumber(login.getText().toString(),
                password.getText().toString()).orElse(userViewModel.getByNameAndPhoneNumber("Admin1", "0621256333").orElse(null));
    }
}
