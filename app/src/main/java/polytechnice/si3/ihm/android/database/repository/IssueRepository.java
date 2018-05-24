package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import polytechnice.si3.ihm.android.database.GlobalDB;
import polytechnice.si3.ihm.android.database.dao.IssueDao;
import polytechnice.si3.ihm.android.database.model.Issue;

public class IssueRepository {
    private static IssueDao issueDao;
    private LiveData<List<Issue>> issues;

    public IssueRepository(Application application) {
        GlobalDB db = GlobalDB.db(application);
        issueDao = db.issueDao();
        issues = issueDao.getAll();
    }

    //----------------------------------------------------//
    //-------------------- Public API --------------------//
    //----------------------------------------------------//

    public LiveData<List<Issue>> getAll() {
        return issues;
    }

    public LiveData<List<Issue>> getByProgress(int progressID) {

        MediatorLiveData<List<Issue>> result = new MediatorLiveData<>();

        new getByProgressAsyncTask(result).execute(progressID);

        return result;
    }

    public void insert(Issue... issues) {
        new insertAsyncTask().execute(issues);
    }

    public void update(Issue... issues) {
        new updateAsyncTask().execute(issues);
    }

    public void updateProgress(int newProgressID, Issue... issues) {
        new updateProgressAsyncTask(newProgressID).execute(issues);
    }

    public void delete(Issue... issues) {
        new deleteAsyncTask().execute(issues);
    }

    public void deleteAll() {
        new deleteAllAsyncTask().execute();
    }

    //----------------------------------------------------//
    //-------------------- AsyncTasks --------------------//
    //----------------------------------------------------//

    private static class getByProgressAsyncTask extends AsyncTask<Integer, Void, LiveData<List<Issue>>> {

        private MediatorLiveData<List<Issue>> issues;

        getByProgressAsyncTask(MediatorLiveData<List<Issue>> issues) {
            this.issues = issues;
            this.issues.setValue(new ArrayList<>());
        }

        @Override
        protected LiveData<List<Issue>> doInBackground(Integer... integers) {
            return issueDao.getByProgress(integers[0]);
        }

        @Override
        protected void onPostExecute(LiveData<List<Issue>> issues) {
            issues.observeForever(issueList -> {
                if (this.issues.getValue() == null || issueList == null) return;

                this.issues.getValue().clear();
                this.issues.getValue().addAll(issueList);
            });
        }
    }

    private static class insertAsyncTask extends AsyncTask<Issue, Void, Void> {
        @Override
        protected Void doInBackground(final Issue... issues) {
            issueDao.insert(issues);
            return null;
        }
    }

    private static class updateProgressAsyncTask extends AsyncTask<Issue, Void, Void> {

        private int newProgressID;

        public updateProgressAsyncTask(int newProgressID) {
            this.newProgressID = newProgressID;
        }

        @Override
        protected Void doInBackground(Issue... issues) {

            Arrays.stream(issues).forEach(issue -> issueDao.updateProgress(issue.getId(), newProgressID));

            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Issue, Void, Void> {

        @Override
        protected Void doInBackground(Issue... issues) {
            issueDao.update(issues);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Issue, Void, Void> {
        @Override
        protected Void doInBackground(Issue... issues) {
            issueDao.delete(issues);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Issue, Void, Void> {
        @Override
        protected Void doInBackground(Issue... issues) {
            issueDao.deleteAll();
            return null;
        }
    }
}
