package polytechnice.si3.ihm.android.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Importance;

@Dao
public interface ImportanceDao {
    @Query("SELECT * FROM importance")
    LiveData<List<Importance>> getAll();

    @Query("SELECT * FROM importance WHERE id LIKE :id")
    Importance getByID(int id);

    @Insert
    void insert(Importance... importances);

    @Delete
    void delete(Importance... importances);

    @Query("DELETE FROM importance")
    void deleteAll();
}