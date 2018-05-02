package polytechnice.si3.ihm.android.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Progress {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String label;

    @Ignore
    public Progress(String label) {
        this.id = 0;
        this.label = label;
    }

    @Deprecated
    public Progress(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Progress{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
