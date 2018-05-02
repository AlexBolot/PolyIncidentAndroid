package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

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

    public void insert(Issue... issues) {
        new insertAsyncTask().execute(issues);
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

    private static class insertAsyncTask extends AsyncTask<Issue, Void, Void> {
        @Override
        protected Void doInBackground(final Issue... users) {
            issueDao.insert(users);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Issue, Void, Void> {
        @Override
        protected Void doInBackground(Issue... users) {
            issueDao.delete(users);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Issue, Void, Void> {
        @Override
        protected Void doInBackground(Issue... users) {
            issueDao.deleteAll();
            return null;
        }
    }
}
