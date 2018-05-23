package polytechnice.si3.ihm.android.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private boolean admin;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String phoneNumber;

    @Ignore
    public User(boolean admin, String name, String phoneNumber) {
        this(0, admin, name, phoneNumber);
    }

    @Deprecated
    public User(int id, boolean admin, String name, String phoneNumber) {
        this.id = id;
        this.admin = admin;
        this.name = name;
        this.phoneNumber = phoneNumber;
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

    public String getPhoneNumber() { return this.phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                admin == user.admin &&
                Objects.equals(name, user.name) &&
                Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, admin, name, phoneNumber);
    }
}