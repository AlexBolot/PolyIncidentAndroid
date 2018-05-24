package polytechnice.si3.ihm.android.Incidents;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import polytechnice.si3.ihm.android.R;
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

public class IssueDetailsView extends AppCompatActivity {

    private static String TAG = "IssueDetailsView";

    private User creator;
    private User assignee;
    private Category category;
    private Importance importance;
    private Progress progress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity start");
        setContentView(R.layout.incident_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Issue issue = new Issue(getIntent());
        TextView title = findViewById(R.id.title);
        title.setText(issue.getTitle());

        //region ====== Fetch data =====
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        IssueViewModel issueViewModel = ViewModelProviders.of(this).get(IssueViewModel.class);
        CategoryViewModel categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        ImportanceViewModel importanceViewModel = ViewModelProviders.of(this).get(ImportanceViewModel.class);
        ProgressViewModel progressViewModel = ViewModelProviders.of(this).get(ProgressViewModel.class);

        creator = userViewModel.getByID(issue.getCreatorID()).orElse(null);
        assignee = userViewModel.getByID(issue.getAssigneeID()).orElse(null);
        category = categoryViewModel.getByID(issue.getCategoryID()).orElse(null);
        importance = importanceViewModel.getByID(issue.getImportanceID()).orElse(null);
        progress = progressViewModel.getByID(issue.getProgressID()).orElse(null);
        //endregion


        TextView creator = findViewById(R.id.creator);
        creator.setText(this.creator.getName());

        TextView assignee = findViewById(R.id.resp);
        assignee.setText(this.assignee.getName());

        //TODO Listener to start UserActivity on creator or resp

        TextView progress = findViewById(R.id.progress);
        progress.setText(this.progress.getLabel());

        TextView date = findViewById(R.id.date);
        date.setText(issue.getDate());

        TextView description = findViewById(R.id.descriptionLayout);
        description.setText(issue.getDescription());

        TextView emergencyPhoneNumber = findViewById(R.id.emergencyPhoneNumber);
        emergencyPhoneNumber.setText("0" + issue.getEmergencyPhoneNumber());

        TextView importance = findViewById(R.id.importance);
        importance.setText(this.importance.getLabel());

        TextView category = findViewById(R.id.category);
        category.setText(this.category.getLabel());


        Button callButton = findViewById(R.id.phoneCallButton);
        callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:(+33)" + issue.getEmergencyPhoneNumber()));
            startActivity(callIntent);
        });

        Button smsButton = findViewById(R.id.smsButton);
        smsButton.setOnClickListener(v -> {
            String number = "(+33)" + issue.getEmergencyPhoneNumber();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
        });
    }
}
