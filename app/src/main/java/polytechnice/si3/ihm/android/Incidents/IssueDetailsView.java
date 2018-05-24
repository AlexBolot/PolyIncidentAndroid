package polytechnice.si3.ihm.android.Incidents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import polytechnice.si3.ihm.android.R;
import polytechnice.si3.ihm.android.database.model.Issue;

public class IssueDetailsView extends AppCompatActivity {

    private static String TAG = "IssueDetailsView";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity start");
        setContentView(R.layout.incident_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Issue issue = new Issue(getIntent());
        TextView title = findViewById(R.id.title);
        title.setText(issue.getTitle());

        TextView progress = findViewById(R.id.progress);
        switch (issue.getProgressID()) {
            case 1:
                progress.setText("A faire");
                break;
            case 2:
                progress.setText("En cours de traitement");
                break;
            case 3:
                progress.setText("Traité");
                break;
            default:
                break;
        }

        TextView date = findViewById(R.id.date);
        date.setText(issue.getDate());

        TextView description = findViewById(R.id.descriptionLayout);
        description.setText(issue.getDescription());

        Button callButton = findViewById(R.id.phoneCallButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:(+33)" + issue.getEmergencyPhoneNumber().substring(1)));
                startActivity(callIntent);
            }
        });

        Button smsButton = findViewById(R.id.smsButton);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            public void onClick(View v) {
                String number = "(+33)" + issue.getEmergencyPhoneNumber().substring(1);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
            }
        });
    }
}
