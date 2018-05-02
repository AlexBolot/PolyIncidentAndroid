package polytechnice.si3.ihm.android.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Category;
import polytechnice.si3.ihm.android.database.model.Importance;

@Dao
public interface ImportanceDao {
    @Query("SELECT * FROM importance")
    LiveData<List<Importance >> getAll();

    @Query("SELECT * FROM importance WHERE id LIKE :id")
    LiveData<Importance> getByID(int id);

    @Query("SELECT * FROM importance WHERE label LIKE :label")
    LiveData<Importance> getByLabel(String label);

    @Insert
    void insert(Category... categories);

    @Delete
    void delete(Category... categories);

    @Query("DELETE FROM category")
    void deleteAll();
}