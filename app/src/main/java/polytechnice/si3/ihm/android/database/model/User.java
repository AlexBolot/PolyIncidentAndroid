package polytechnice.si3.ihm.android.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private boolean admin;

    @ColumnInfo
    private String name;

    @Ignore
    public User(boolean admin, String name) {
        this.admin = admin;
        this.name = name;
    }

    @Deprecated
    public User(int id, boolean admin, String name) {
        this.id = id;
        this.admin = admin;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", admin=" + admin +
                ", name='" + name + '\'' +
                '}';
    }
}