package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import polytechnice.si3.ihm.android.database.GlobalDB;
import polytechnice.si3.ihm.android.database.dao.ImportanceDao;
import polytechnice.si3.ihm.android.database.model.Importance;
import polytechnice.si3.ihm.android.database.model.Progress;

public class ImportanceRepository {
    private static ImportanceDao importanceDao;
    private LiveData<List<Importance>> importances;

    public ImportanceRepository(Application application) {
        GlobalDB db = GlobalDB.db(application);
        importanceDao = db.importanceDao();
        importances = importanceDao.getAll();
    }

    //----------------------------------------------------//
    //-------------------- Public API --------------------//
    //----------------------------------------------------//

    public LiveData<List<Importance>> getAll() {
        return importances;
    }

    public LiveData<Importance> getByLabel(String label){
        return importanceDao.getByLabel(label);
    }

    public void insert(Importance... importances) {
        new insertAsyncTask().execute(importances);
    }

    public void delete(Importance... importances) {
        new deleteAsyncTask().execute(importances);
    }

    public void deleteAll() {
        new deleteAllAsyncTask().execute();
    }

    //----------------------------------------------------//
    //-------------------- AsyncTasks --------------------//
    //----------------------------------------------------//

    private static class insertAsyncTask extends AsyncTask<Importance, Void, Void> {
        @Override
        protected Void doInBackground(Importance... importances) {
            importanceDao.insert(importances);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Importance, Void, Void> {
        @Override
        protected Void doInBackground(Importance... importances) {
            importanceDao.delete(importances);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Importance, Void, Void> {
        @Override
        protected Void doInBackground(Importance... importances) {
            importanceDao.deleteAll();
            return null;
        }
    }
}
