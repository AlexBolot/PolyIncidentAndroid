package polytechnice.si3.ihm.android.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Issue;

@Dao
public interface IssueDao {
    @Query("SELECT * FROM issue")
    LiveData<List<Issue>> getAll();

    @Query("SELECT * FROM issue WHERE progressID LIKE :progressID")
    LiveData<List<Issue>> getByProgress(int progressID);

    @Query("UPDATE issue SET progressID = :newProgress WHERE id=:issueID")
    void updateProgress(int issueID, int newProgress);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Issue... issues);

    @Insert
    void insert(Issue... issues);

    @Delete
    void delete(Issue... issues);

    @Query("DELETE FROM issue")
    void deleteAll();
}