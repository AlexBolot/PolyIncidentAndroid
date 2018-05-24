package polytechnice.si3.ihm.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class ProfileActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
