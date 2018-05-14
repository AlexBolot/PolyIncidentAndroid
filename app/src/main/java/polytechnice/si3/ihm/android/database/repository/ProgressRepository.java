package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import polytechnice.si3.ihm.android.database.GlobalDB;
import polytechnice.si3.ihm.android.database.dao.ProgressDao;
import polytechnice.si3.ihm.android.database.model.Progress;

public class ProgressRepository {
    private static ProgressDao progressDao;
    private LiveData<List<Progress>> progresses;

    public ProgressRepository(Application application) {
        GlobalDB db = GlobalDB.db(application);
        progressDao = db.progressDao();
        progresses = progressDao.getAll();
    }

    //----------------------------------------------------//
    //-------------------- Public API --------------------//
    //----------------------------------------------------//

    public LiveData<List<Progress>> getAll() {
        return progresses;
    }

    public LiveData<Progress> getByLabel(String label){
        return progressDao.getByLabel(label);
    }

    public void insert(Progress... categories) {
        new insertAsyncTask().execute(categories);
    }

    public void delete(Progress... categories) {
        new deleteAsyncTask().execute(categories);
    }

    public void deleteAll() {
        new deleteAllAsyncTask().execute();
    }

    //----------------------------------------------------//
    //-------------------- AsyncTasks --------------------//
    //----------------------------------------------------//

    private static class insertAsyncTask extends AsyncTask<Progress, Void, Void> {
        @Override
        protected Void doInBackground(final Progress... categories) {
            progressDao.insert(categories);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Progress, Void, Void> {
        @Override
        protected Void doInBackground(Progress... categories) {
            progressDao.delete(categories);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Progress, Void, Void> {
        @Override
        protected Void doInBackground(Progress... categories) {
            progressDao.deleteAll();
            return null;
        }
    }
}
