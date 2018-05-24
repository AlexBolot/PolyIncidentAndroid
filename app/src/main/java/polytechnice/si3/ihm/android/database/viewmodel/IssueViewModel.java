package polytechnice.si3.ihm.android.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.Progress;
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

    /**
     * Changes the progress of an issue and saves changes in the database.
     *
     * @param issue       Issue to update with a new progress
     * @param newProgress New progress of the issue
     */
    public void updateProgress(Issue issue, Progress newProgress) {
        updateProgress(issue, newProgress.getId());
    }

    /**
     * Changes the progress of an issue and saves changes in the database.
     *
     * @param issue         Issue to update with a new progress
     * @param newProgressID ID of the new progress of the issue
     */
    public void updateProgress(Issue issue, int newProgressID) {
        issueRepository.updateProgress(newProgressID, issue);
    }

    /**
     * Send issues to the DB to update them.
     *
     * @param issues Issues to update in DB
     */
    public void update(Issue... issues) {
        issueRepository.update(issues);
    }

    public void delete(Issue... issues) {
        issueRepository.delete(issues);
    }

    public void deleteAll() {
        issueRepository.deleteAll();
    }

    public LiveData<List<Issue>> getByProgress(int progressID) {
        return issueRepository.getByProgress(progressID);
    }
}
