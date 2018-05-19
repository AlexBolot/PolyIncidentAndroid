package polytechnice.si3.ihm.android.database.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Category;
import polytechnice.si3.ihm.android.database.repository.CategoryRepository;

public class CategoryViewModel extends AndroidViewModel {

    private CategoryRepository categoryRepository;
    private LiveData<List<Category>> categories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        categories = categoryRepository.getAll();
    }

    public LiveData<List<Category>> getAll() {
        return categories;
    }

    public void insert(Category... categories) {
        categoryRepository.insert(categories);
    }

    public void delete(Category... categories) {
        categoryRepository.delete(categories);
    }

    public void deleteAll() {
        categoryRepository.deleteAll();
    }

}
