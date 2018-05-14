package polytechnice.si3.ihm.android.database.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import polytechnice.si3.ihm.android.database.GlobalDB;
import polytechnice.si3.ihm.android.database.dao.CategoryDao;
import polytechnice.si3.ihm.android.database.model.Category;

public class CategoryRepository {
    private static CategoryDao categoryDao;
    private LiveData<List<Category>> categories;

    public CategoryRepository(Application application) {
        GlobalDB db = GlobalDB.db(application);
        categoryDao = db.categoryDao();
        categories = categoryDao.getAll();
    }

    //----------------------------------------------------//
    //-------------------- Public API --------------------//
    //----------------------------------------------------//

    public LiveData<List<Category>> getAll() {
        return categories;
    }

    public LiveData<Category> getByLabel(String label){
        return categoryDao.getByLabel(label);
    }

    public void insert(Category... categories) {
        new insertAsyncTask().execute(categories);
    }

    public void delete(Category... categories) {
        new deleteAsyncTask().execute(categories);
    }

    public void deleteAll() {
        new deleteAllAsyncTask().execute();
    }

    //----------------------------------------------------//
    //-------------------- AsyncTasks --------------------//
    //----------------------------------------------------//

    private static class insertAsyncTask extends AsyncTask<Category, Void, Void> {
        @Override
        protected Void doInBackground(final Category... categories) {
            categoryDao.insert(categories);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Category, Void, Void> {
        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.delete(categories);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Category, Void, Void> {
        @Override
        protected Void doInBackground(Category... categories) {
            categoryDao.deleteAll();
            return null;
        }
    }
}
