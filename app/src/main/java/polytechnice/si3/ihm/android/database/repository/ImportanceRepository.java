package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import polytechnice.si3.ihm.android.database.GlobalDB;
import polytechnice.si3.ihm.android.database.dao.ImportanceDao;
import polytechnice.si3.ihm.android.database.model.Importance;

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

    public Optional<Importance> getByID(int id) {
        try {
            return Optional.ofNullable(new getByID().execute(id).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Optional.empty();
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

    private static class getByID extends AsyncTask<Integer, Void, Importance> {
        @Override
        protected Importance doInBackground(Integer... integers) {
            return importanceDao.getByID(integers[0]);
        }
    }

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
