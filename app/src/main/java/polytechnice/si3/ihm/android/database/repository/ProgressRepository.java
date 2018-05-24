package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

    public Optional<Progress> getByID(int id) {

        try {
           return Optional.ofNullable(new getByID().execute(id).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Optional.empty();
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

    private static class getByID extends AsyncTask<Integer, Void, Progress> {
        @Override
        protected Progress doInBackground(Integer... integers) {
            return progressDao.getByID(integers[0]);
        }
    }

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
