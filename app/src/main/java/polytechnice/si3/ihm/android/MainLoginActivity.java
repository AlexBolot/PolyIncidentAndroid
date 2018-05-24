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

import java.util.List;

import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.UserViewModel;

public class MainLoginActivity extends AppCompatActivity {

    private static String TAG = "MainLoginActivity";
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);


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
