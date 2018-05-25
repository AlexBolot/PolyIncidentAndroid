package polytechnice.si3.ihm.android.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import polytechnice.si3.ihm.android.database.model.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM user WHERE id LIKE :id")
    User getByID(int id);

    @Query("SELECT * FROM user WHERE name =:name AND phoneNumber=:phoneNumber")
    User getByNameAndPhoneNumber(String name, String phoneNumber);

    @Insert
    void insert(User... users);

    @Delete
    void delete(User... users);

    @Query("DELETE FROM user")
    void deleteAll();
}



