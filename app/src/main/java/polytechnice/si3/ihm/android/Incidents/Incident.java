package polytechnice.si3.ihm.android.Incidents;


import android.support.annotation.Nullable;

import java.util.Date;

import polytechnice.si3.ihm.android.User;

class Incident {

    private int id;
    private User responsable;
    private User creator;
    private String title;
    private String description;
    private String linkToPreview;
    private Date date;
    private int category;
    private int progress;
    private int importance;

    public Incident(int id, User creator, User responsable, String title, String description, @Nullable String linkToPreview, Date date, int category, int progress, int importance) {
        this.id = id;
        this.responsable = responsable;
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.linkToPreview = linkToPreview;
        this.date = date;
        this.category = category;
        this.progress = progress;
        this.importance = importance;
    }

    public int getId() {
        return id;
    }

    public User getResponsable() {
        return responsable;
    }

    public User getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public int getCategory() {
        return category;
    }

    public int getProgress() {
        return progress;
    }

    public int getImportance() {
        return importance;
    }

    public String getLinkToPreview() {
        return linkToPreview;
    }

    public String getCategoryLabel() {
        switch (category) {
            case 1:
                return "Maintenance";
            case 2:
                return "Utilitaire";
            case 3:
                return "Réparation";
            default:
                return "Non renseignée";
        }
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id=" + id +
                ", responsable=" + responsable +
                ", creator=" + creator +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", linkToPreview='" + linkToPreview + '\'' +
                ", date=" + date +
                ", category=" + getCategoryLabel() +
                ", progress=" + progress +
                ", importance=" + importance +
                '}';
    }
}
