package polytechnice.si3.ihm.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.w3c.dom.Text;

import polytechnice.si3.ihm.android.database.model.User;


public class ProfileActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_profile);

        User user = new User(getIntent());

        TextView name = findViewById(R.id.username);
        TextView role= findViewById(R.id.role);
        TextView userPhoneNumber = findViewById(R.id.userPhoneNumber);

        name.setText(user.getName());
        role.setText(user.isAdmin() ? "Administrateur" : "Utilisateur classique");
        userPhoneNumber.setText(user.getPhoneNumber());
    }
}
