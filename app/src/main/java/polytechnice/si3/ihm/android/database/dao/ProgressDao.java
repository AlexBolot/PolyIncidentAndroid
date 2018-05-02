package polytechnice.si3.ihm.android.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Progress;

@Dao
public interface ProgressDao {
    @Query("SELECT * FROM progress")
    LiveData<List<Progress >> getAll();

    @Query("SELECT * FROM progress WHERE id LIKE :id")
    LiveData<Progress> getByID(int id);

    @Query("SELECT * FROM progress WHERE label LIKE :label")
    LiveData<Progress> getByLabel(String label);

    @Insert
    void insert(Progress... progresses);

    @Delete
    void delete(Progress... progresses);

    @Query("DELETE FROM progress")
    void deleteAll();
}