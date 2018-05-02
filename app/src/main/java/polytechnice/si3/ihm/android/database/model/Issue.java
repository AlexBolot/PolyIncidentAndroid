package polytechnice.si3.ihm.android.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "assigneeID", onDelete = CASCADE),
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "creatorID", onDelete = CASCADE),
        @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryID", onDelete = CASCADE),
        @ForeignKey(entity = Progress.class, parentColumns = "id", childColumns = "progressID", onDelete = CASCADE),
        @ForeignKey(entity = Importance.class, parentColumns = "id", childColumns = "importanceID", onDelete = CASCADE)
})
public class Issue {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private int assigneeID;

    @ColumnInfo
    private int creatorID;

    @ColumnInfo
    private String title;

    @ColumnInfo
    private String description;

    @ColumnInfo
    private String linkToPreview;

    @ColumnInfo
    private String date;

    @ColumnInfo
    private int categoryID;

    @ColumnInfo
    private int progressID;

    @ColumnInfo
    private int importanceID;

    @Ignore
    public Issue(int assigneeID, int creatorID, String title, String description, String linkToPreview, String date, int categoryID, int progressID, int importanceID) {
        this.id = 0;
        this.assigneeID = assigneeID;
        this.creatorID = creatorID;
        this.title = title;
        this.description = description;
        this.linkToPreview = linkToPreview;
        this.date = date;
        this.categoryID = categoryID;
        this.progressID = progressID;
        this.importanceID = importanceID;
    }

    @Deprecated
    public Issue(int id, int assigneeID, int creatorID, String title, String description, String linkToPreview, String date, int categoryID, int progressID, int importanceID) {
        this.id = id;
        this.assigneeID = assigneeID;
        this.creatorID = creatorID;
        this.title = title;
        this.description = description;
        this.linkToPreview = linkToPreview;
        this.date = date;
        this.categoryID = categoryID;
        this.progressID = progressID;
        this.importanceID = importanceID;
    }

    //region --------------- Getters and Setters ---------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssigneeID() {
        return assigneeID;
    }

    public void setAssigneeID(int assigneeID) {
        this.assigneeID = assigneeID;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLinkToPreview() {
        return linkToPreview;
    }

    public void setLinkToPreview(String linkToPreview) {
        this.linkToPreview = linkToPreview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getProgressID() {
        return progressID;
    }

    public void setProgressID(int progressID) {
        this.progressID = progressID;
    }

    public int getImportanceID() {
        return importanceID;
    }

    public void setImportanceID(int importanceID) {
        this.importanceID = importanceID;
    }


    //endregion

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", assigneeID=" + assigneeID +
                ", creatorID=" + creatorID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", linkToPreview='" + linkToPreview + '\'' +
                ", date='" + date + '\'' +
                ", categoryID=" + categoryID +
                ", progressID=" + progressID +
                ", importanceID=" + importanceID +
                '}';
    }
}
