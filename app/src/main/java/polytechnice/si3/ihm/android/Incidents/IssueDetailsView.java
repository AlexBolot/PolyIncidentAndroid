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
import polytechnice.si3.ihm.android.database.viewmodel.ProgressViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.UserViewModel;

public class IssueDetailsView extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "IssueDetailsView";
        Log.d(TAG, "Activity start");
        setContentView(R.layout.incident_details);

        Issue issue = new Issue(getIntent());
        TextView title = findViewById(R.id.title);
        title.setText(issue.getTitle());

        //region ====== Fetch data =====
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        CategoryViewModel categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        ImportanceViewModel importanceViewModel = ViewModelProviders.of(this).get(ImportanceViewModel.class);
        ProgressViewModel progressViewModel = ViewModelProviders.of(this).get(ProgressViewModel.class);

        User creator = userViewModel.getByID(issue.getCreatorID()).orElse(null);
        User assignee = userViewModel.getByID(issue.getAssigneeID()).orElse(null);
        Category category = categoryViewModel.getByID(issue.getCategoryID()).orElse(null);
        Importance importance = importanceViewModel.getByID(issue.getImportanceID()).orElse(null);
        Progress progress = progressViewModel.getByID(issue.getProgressID()).orElse(null);
        //endregion

        TextView creatorT = findViewById(R.id.creator);
        creatorT.setText(creator != null ? "Créé par " + creator.getName() : "Créateur inconnu");

        TextView assigneeT = findViewById(R.id.resp);
        assigneeT.setText(assignee != null ? "Assigné à " + assignee.getName() : "Aucun responsable");

        //TODO Listener to start UserActivity on creator or resp

        TextView progressT = findViewById(R.id.progress);
        if (progress != null)
            progressT.setText(progress.getLabel());

        TextView dateT = findViewById(R.id.date);
        dateT.setText(issue.getDate());

        TextView descriptionT = findViewById(R.id.descriptionLayout);
        descriptionT.setText(issue.getDescription());

        TextView importanceT = findViewById(R.id.importance);
        if (importance != null)
            importanceT.setText("Urgence " + importance.getLabel());

        TextView categoryT = findViewById(R.id.category);
        if (category != null)
            categoryT.setText("Catégorie " + category.getLabel());


        Button callButton = findViewById(R.id.phoneCallButton);
        callButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:(+33)" + issue.getEmergencyPhoneNumber().substring(1)));
            startActivity(callIntent);
        });

        Button smsButton = findViewById(R.id.smsButton);
        smsButton.setOnClickListener(v -> {
            String number = "(+33)" + issue.getEmergencyPhoneNumber().substring(1);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
        });
    }
}
