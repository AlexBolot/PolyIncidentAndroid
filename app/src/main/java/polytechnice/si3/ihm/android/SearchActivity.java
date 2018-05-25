package polytechnice.si3.ihm.android;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import polytechnice.si3.ihm.android.Incidents.IssueAdapterForSearch;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.CategoryViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ImportanceViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;
import polytechnice.si3.ihm.android.database.viewmodel.ProgressViewModel;

public class SearchActivity extends AppCompatActivity {


    private List<Issue> issues;
    IssueAdapterForSearch issueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        TextView importance = findViewById(R.id.importance);


        SearchView searchView = findViewById(R.id.searchView);

        GridView gridView = findViewById(R.id.searchResult);
        IssueViewModel issueViewModel = ViewModelProviders.of(this).get(IssueViewModel.class);

        ProgressViewModel progressViewModel = ViewModelProviders.of(this).get(ProgressViewModel.class);
        ImportanceViewModel importanceViewModel = ViewModelProviders.of(this).get(ImportanceViewModel.class);
        CategoryViewModel categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);


        User userConnected = new User(getIntent());
        issueAdapter = new IssueAdapterForSearch(
                getBaseContext(), new ArrayList<>(), issueViewModel, userConnected,
                progressViewModel, importanceViewModel, categoryViewModel);

        LiveData<List<Issue>> liveIssues = issueViewModel.getAll();

        liveIssues.observeForever(issueList -> {
            if (issueList == null || issueList.isEmpty()) return;

            issues = issueList;
            issueAdapter.setIssues(issues);
        });

        gridView.setAdapter(issueAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (issues == null || issues.isEmpty()) return true;
                issueAdapter.setIssues(issues.stream().filter(i -> i.getTitle().toLowerCase().contains(newText.toLowerCase()))
                        .collect(Collectors.toList()));
                return true;
            }
        });
    }
}