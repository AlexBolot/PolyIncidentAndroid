package polytechnice.si3.ihm.android.Incidents;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.database.model.Issue;

public class IssueDetailsView extends AppCompatActivity {

    private static String TAG = "IssueDetailsView";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity start");
        setContentView(R.layout.incident_details);

        Issue issue = new Issue(getIntent());
        TextView title = findViewById(R.id.title);
        title.setText(issue.getTitle());
    }
}
