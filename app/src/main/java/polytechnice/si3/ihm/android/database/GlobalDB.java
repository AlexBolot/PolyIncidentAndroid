package polytechnice.si3.ihm.android.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import polytechnice.si3.ihm.android.database.dao.CategoryDao;
import polytechnice.si3.ihm.android.database.dao.ImportanceDao;
import polytechnice.si3.ihm.android.database.dao.IssueDao;
import polytechnice.si3.ihm.android.database.dao.ProgressDao;
import polytechnice.si3.ihm.android.database.dao.UserDao;
import polytechnice.si3.ihm.android.database.model.Category;
import polytechnice.si3.ihm.android.database.model.Importance;
import polytechnice.si3.ihm.android.database.model.Issue;
import polytechnice.si3.ihm.android.database.model.Progress;
import polytechnice.si3.ihm.android.database.model.User;

import static android.arch.persistence.room.Room.databaseBuilder;

@Database(version = 1,
        entities = {
                User.class,
                Issue.class,
                Progress.class,
                Category.class,
                Importance.class})

public abstract class GlobalDB extends RoomDatabase {
    private static GlobalDB INSTANCE;

    public static GlobalDB db(final Context context) {
        if (INSTANCE == null) {
            synchronized (GlobalDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = databaseBuilder(context, GlobalDB.class, "GlobalDB").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract IssueDao issueDao();

    public abstract ProgressDao progressDao();

    public abstract CategoryDao categoryDao();

    public abstract ImportanceDao importanceDao();
}
