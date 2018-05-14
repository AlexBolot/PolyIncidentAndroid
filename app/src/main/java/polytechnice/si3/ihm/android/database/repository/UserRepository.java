package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import polytechnice.si3.ihm.android.database.GlobalDB;
import polytechnice.si3.ihm.android.database.dao.UserDao;
import polytechnice.si3.ihm.android.database.model.User;

public class UserRepository {
    private static UserDao userDao;
    private LiveData<List<User>> users;

    public UserRepository(Application application) {
        GlobalDB db = GlobalDB.db(application);
        userDao = db.userDao();
        users = userDao.getAll();
    }

    //----------------------------------------------------//
    //-------------------- Public API --------------------//
    //----------------------------------------------------//

    public LiveData<List<User>> getAll() {
        return users;
    }

    public void insert(User... users) {
        new insertAsyncTask().execute(users);
    }

    public void delete(User... users) {
        new deleteAsyncTask().execute(users);
    }

    public void deleteAll() {
        new deleteAllAsyncTask().execute();
    }

    //----------------------------------------------------//
    //-------------------- AsyncTasks --------------------//
    //----------------------------------------------------//

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(final User... users) {
            userDao.insert(users);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... users) {
            userDao.deleteAll();
            return null;
        }
    }
}