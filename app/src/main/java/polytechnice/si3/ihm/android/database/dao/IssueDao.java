package polytechnice.si3.ihm.android.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.Issue;

@Dao
public interface IssueDao {
    @Query("SELECT * FROM issue")
    LiveData<List<Issue>> getAll();

    @Query("SELECT * FROM issue WHERE assigneeID LIKE :userID")
    LiveData<Issue> getByAssignedUser(int userID);

    @Query("SELECT * FROM issue WHERE creatorID LIKE :userID")
    LiveData<Issue> getByCreatorUser(int userID);

    @Insert
    void insert(Issue... issues);

    @Delete
    void delete(Issue... issues);

    @Query("DELETE FROM issue")
    void deleteAll();
}