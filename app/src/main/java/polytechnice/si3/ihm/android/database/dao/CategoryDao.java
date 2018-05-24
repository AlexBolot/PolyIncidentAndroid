package polytechnice.si3.ihm.android.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Category;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category")
    LiveData<List<Category>> getAll();

    @Query("SELECT * FROM category WHERE id LIKE :id")
    Category getByID(int id);

    @Insert
    void insert(Category... categories);

    @Delete
    void delete(Category... categories);

    @Query("DELETE FROM category")
    void deleteAll();
}