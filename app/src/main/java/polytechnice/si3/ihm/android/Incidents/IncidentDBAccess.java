package polytechnice.si3.ihm.android.Incidents;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import polytechnice.si3.ihm.android.User;

public class IncidentDBAccess extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    //region Scheme
    private static class IncidentTable implements BaseColumns {
        static final String TABLE_NAME = "incident";
        static final String _ID = "id";
        static final String COLUMN_RESP = "responsable";
        static final String COLUMN_CREAT = "creator";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_DESC = "description";
        static final String COLUMN_LINK_PREVIEW = "link_preview";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_CAT = "category";
        static final String COLUMN_PROGRESS = "progress";
        static final String COLUMN_IMP = "importance";
        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_RESP + " INTEGER REFERENCES user(`id`)," +
                        COLUMN_CREAT + " INTEGER REFERENCES user(`id`)," +
                        COLUMN_TITLE + " TEXT," +
                        COLUMN_DESC + " TEXT," +
                        COLUMN_LINK_PREVIEW + " TEXT," +
                        COLUMN_DATE + " TEXT," +
                        COLUMN_PROGRESS + " INTEGER," +
                        COLUMN_CAT + " INTEGER," +
                        COLUMN_IMP + " INTEGER)";
        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        static void insertBaseValues(SQLiteDatabase db) {
            Incident[] values = {
                    new Incident(1, new User(3, "Lucas"), new User(1, "Alexandre"), "Je veux un raton laveur", "Il est trop mignon",
                            "https://i.imgur.com/VVWVgxp.png",
                            new Date(), 1, 1, 2),
                    new Incident(2, new User(1, "Alexandre"), new User(3, "Lucas"), "Vase cassé", "Je balance pas, mais le vase est cassé",
                            "https://i.imgur.com/URVyanB.png",
                            new Date(), 1, 3, 2),
                    new Incident(3, new User(2, "Olivia"), new User(1, "Alexandre"), "On me suit", "Je me sens épié depuis quelques temps",
                            "https://i.imgur.com/kSQAhKl.png",
                            new Date(), 1, 3, 2),
                    new Incident(4, new User(3, "Lucas"), new User(1, "Alexandre"), "Avoir son permis Raton Laveur", "Décrocher son permis Raton Laveur pour en adopter plein, créer une ferme avec des caméras à reconnaissances (via k-means biensur) pour détecter les images les plus mignnones de raton laveur, les poster sur facebook, se faire max thunes, conquérir le monde.",
                            "https://scontent-cdg2-1.xx.fbcdn.net/v/t1.0-9/31404220_1604233043035876_7114444190011883520_n.jpg?_nc_cat=0&_nc_eui2=v1%3AAeHnrFDc6dw1qaUU0xyVnjgFpB69eO-GWXSP6f2eqtpPkvGm5WTGSIqrFS3unMfnzjwuFA2T55teTFzA7qkrjWYgj4s6p18aZZDOwSAMpoz4IA&oh=6b1c6e2e71fffd9b9a831be11abb0251&oe=5B678C2B",
                            new Date(), 1, 2, 2)};

            for (Incident incident : values) {
                insertIncident(incident, db);
            }
        }

        private static void insertIncident(Incident incident, SQLiteDatabase db) {
            Log.i("Insert Incident", "Insert " + incident.toString());
            ContentValues incContent = new ContentValues();
            incContent.put(_ID, incident.getId());
            incContent.put(COLUMN_RESP, incident.getResponsable().getId());
            incContent.put(COLUMN_CREAT, incident.getCreator().getId());
            incContent.put(COLUMN_TITLE, incident.getTitle());
            incContent.put(COLUMN_DESC, incident.getDescription());
            incContent.put(COLUMN_LINK_PREVIEW, incident.getLinkToPreview());
            incContent.put(COLUMN_DATE, incident.getDate().toString());
            incContent.put(COLUMN_PROGRESS, incident.getProgress());
            incContent.put(COLUMN_CAT, incident.getCategory());
            incContent.put(COLUMN_IMP, incident.getImportance());

            db.insert(TABLE_NAME, null, incContent);
        }
    }

    private static class UserTable implements BaseColumns {
        static final String TABLE_NAME = "user";
        static final String _ID = "id";
        static final String COLUMN_NAME = "name";
        static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME + " TEXT)";
        static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        static void insertBaseValues(SQLiteDatabase db) {
            ContentValues user1 = new ContentValues();
            user1.put(_ID, 1);
            user1.put(COLUMN_NAME, "Alexandre");
            ContentValues user2 = new ContentValues();
            user2.put(_ID, 2);
            user2.put(COLUMN_NAME, "Olivia");
            ContentValues user3 = new ContentValues();
            user3.put(_ID, 3);
            user3.put(COLUMN_NAME, "Lucas");
            db.insert(TABLE_NAME, null, user1);
            db.insert(TABLE_NAME, null, user2);
            db.insert(TABLE_NAME, null, user3);
        }

        static final String SQL_INSERT_VALUES = "INSERT INTO " + TABLE_NAME + "VALUES(" +
                "(1,\"Alexandre\")," +
                "(2,\"Olivia\")," +
                "(3,\"Lucas\")" +
                ")";
    }

    private static class AliasForJoin {
        static final String INC_CREATOR_NAME = "creatorName";
        static final String INC_RESPONSABLE_NAME = "responsableName";
    }


    //endregion

    private static String DB_NAME = "ihm_android_database";
    private final Context myContext;

    public void onCreate(SQLiteDatabase db) {
        //Create tables
        db.execSQL(IncidentTable.SQL_CREATE_ENTRIES);
        db.execSQL(UserTable.SQL_CREATE_ENTRIES);

        //Insert values to work with
        UserTable.insertBaseValues(db);
        IncidentTable.insertBaseValues(db);
    }

    public IncidentDBAccess(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(UserTable.SQL_DELETE_ENTRIES);
        db.execSQL(IncidentTable.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private List<Incident> getAllIncWithProgress(int progress) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT I.*, " +
                        "U." + UserTable.COLUMN_NAME + " as " + AliasForJoin.INC_CREATOR_NAME + ", " +
                        "U2." + UserTable.COLUMN_NAME + " as " + AliasForJoin.INC_RESPONSABLE_NAME + " " +
                        "FROM incident I " +
                        "JOIN user U ON U." + UserTable._ID + " = I." + IncidentTable.COLUMN_CREAT + ' ' +
                        "JOIN user U2 ON U2." + UserTable._ID + " = I." + IncidentTable.COLUMN_RESP + ' ' +
                        "WHERE " + IncidentTable.COLUMN_PROGRESS + " = " + progress + " " +
                        "ORDER BY date DESC",
                null);
        cursor.moveToFirst();
        ArrayList<Incident> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            list.add(getFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return list;

    }

    /**
     * @return All incidents to fix
     */
    public List<Incident> getAllIncTODO() {
        return getAllIncWithProgress(1);
    }

    /**
     * @return all indicent being fixed
     */
    public List<Incident> getAllIncIP() {
        return getAllIncWithProgress(2);
    }

    /**
     * @return all incidents fixed
     */
    public List<Incident> getAllIncDone() {
        return getAllIncWithProgress(3);
    }

    private Incident getFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(IncidentTable._ID));
        User creator = new User(cursor.getInt(cursor.getColumnIndex(IncidentTable.COLUMN_CREAT)),
                cursor.getString(cursor.getColumnIndex(AliasForJoin.INC_CREATOR_NAME)));
        User resp = new User(cursor.getInt(cursor.getColumnIndex(IncidentTable.COLUMN_RESP)),
                cursor.getString(cursor.getColumnIndex(AliasForJoin.INC_RESPONSABLE_NAME)));
        String title = cursor.getString(cursor.getColumnIndex(IncidentTable.COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndex(IncidentTable.COLUMN_DESC));
        String linkToPreview = cursor.getString(cursor.getColumnIndex(IncidentTable.COLUMN_LINK_PREVIEW));

        //TODO Parse Date
        int category = cursor.getInt(cursor.getColumnIndex(IncidentTable.COLUMN_CAT));
        int progress = cursor.getInt(cursor.getColumnIndex(IncidentTable.COLUMN_PROGRESS));
        int importance = cursor.getInt(cursor.getColumnIndex(IncidentTable.COLUMN_IMP));
        return new Incident(id, creator, resp, title, description, linkToPreview, new Date(), category, progress, importance);
    }
}