package polytechnice.si3.ihm.android;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import polytechnice.si3.ihm.android.Incidents.IssueAdapterForSearch;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.User;
import polytechnice.si3.ihm.android.database.viewmodel.IssueViewModel;

public class SearchActivity extends AppCompatActivity {


    private List<Issue> issues;
    IssueAdapterForSearch issueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        SearchView searchView = findViewById(R.id.searchView);

        GridView gridView = findViewById(R.id.searchResult);
        IssueViewModel issueViewModel = ViewModelProviders.of(this).get(IssueViewModel.class);

        User userConnected = new User(getIntent());
        issueAdapter = new IssueAdapterForSearch(
                getBaseContext(), new ArrayList<>(), issueViewModel, userConnected);

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