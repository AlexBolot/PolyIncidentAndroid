package polytechnice.si3.ihm.android.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.stream.Collectors;

import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.repository.IssueRepository;

public class IssueViewModel extends AndroidViewModel {

    private IssueRepository issueRepository;
    private LiveData<List<Issue>> issues;

    public IssueViewModel(@NonNull Application application) {
        super(application);
        issueRepository = new IssueRepository(application);
        issues = issueRepository.getAll();
    }

    public LiveData<List<Issue>> getAll() {
        return issues;
    }

    public void insert(Issue... issues) {
        issueRepository.insert(issues);
    }

    public void delete(Issue... issues) {
        issueRepository.delete(issues);
    }

    public void deleteAll() {
        issueRepository.deleteAll();
    }

    public List<Issue> getAll(int progressID) {
        return getAll().getValue().stream().filter(issue -> issue.getProgressID() == progressID).collect(Collectors.toList());
    }
}
