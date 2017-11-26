package cs175.alphafitness;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {
    private final static String TAG = MyContentProvider.class.getSimpleName();

    static final String PROVIDER = "cs175.alphafitness";
    static final Uri URI1 = Uri.parse("content://" + PROVIDER + "/profile");
    static final Uri URI2 = Uri.parse("content://" + PROVIDER + "/workout");


    Context mcontext;
    static final int PROFILE = 1;
    static final int PROFILE_ID = 2;
    static final int WORKOUT = 3;
    static final int WORKOUT_ID = 4;
    static final UriMatcher uriMatcher;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "profile", PROFILE);
        uriMatcher.addURI(PROVIDER, "profile/#", PROFILE_ID);
        uriMatcher.addURI(PROVIDER, "workout", WORKOUT);
        uriMatcher.addURI(PROVIDER, "workout/#", WORKOUT_ID);
    }

    private static HashMap <String, String> PROFILE_PROJECTION_MAP;
    private static HashMap <String, String> WORKOUT_PROJECTION_MAP;

    private SQLiteDatabase db;
    static final int DATABASE_VERSION = 10;
    static final String DATABASE_NAME = "Workouts_Database";

    //Create Profile table
    static final String KEY_ID = "user_id";
    static final String KEY_NAME = "username";
    static final String KEY_GENDER = "gender";
    static final String KEY_WEIGHT = "weight";
    static final String TABLE_NAME = "Profile";
    static final String CREATE_PROFILE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_NAME + " TEXT, " + KEY_GENDER + " TEXT, " + KEY_WEIGHT +  " DOUBLE);";

    // create workouts table
    static final String TABLE_WORKOUTS = "Workouts";
    static final String ID = "id";
    static final String USER_ID = "user_id";
    static final String KEY_DISTANCE = "distance";
    static final String KEY_TIME = "time";
    static final String KEY_WORKOUTS = "workouts";
    static final String KEY_STEPS = "step_count";
    static final String KEY_CALO = "calo";
    static final String DATE = "date";
    static final String STATUS = "status";

    static final String CREATE_WORKOUT_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            USER_ID + " INTEGER, " + KEY_DISTANCE + " DOUBLE," + KEY_TIME + " TEXT, " + KEY_WORKOUTS +
            " INT, " + DATE + " DATE, "+ KEY_CALO +  " DOUBLE, " +KEY_STEPS +" INT, " + STATUS + " INTEGER DEFAULT 0," +" FOREIGN KEY (" + USER_ID + ") REFERENCES "+TABLE_NAME+"("+KEY_ID+"));";

    public MyContentProvider() {
    }

    private static class DB extends SQLiteOpenHelper{
        DB(Context context){
            super(context, DATABASE_NAME, null,  DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_PROFILE_TABLE);
            db.execSQL(CREATE_WORKOUT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
            onCreate(db);
        }
    }

    private void notifyChange(Uri uri){
        ContentResolver resolver = mcontext.getContentResolver();
        if(resolver != null){
            resolver.notifyChange(uri, null);
        }
    }

    private int getMatchedID(Uri uri){
        int matchedID = uriMatcher.match(uri);
                if (!(matchedID == PROFILE || matchedID == PROFILE_ID || matchedID == WORKOUT || matchedID == WORKOUT_ID)) {
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
                }
        return matchedID;
    }

    private String getIdString(Uri uri){
        int matchedID = uriMatcher.match(uri);
        String id="";
        switch (matchedID) {
            case PROFILE:
                id = (USER_ID + "=" + uri.getPathSegments().get(1));
            case WORKOUT:
                id = (ID + "=" + uri.getPathSegments().get(1));
        }
        return id;
    }

    private String getSelectionWithID(Uri uri, String selection){
        String sel_str = getIdString(uri);
        if(!TextUtils.isEmpty(selection))
            sel_str += " AND (" + selection + ")";
        return  sel_str;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        String sel_str;
        int matchID = getMatchedID(uri);
        if (matchID == PROFILE || matchID == PROFILE_ID) {
            sel_str = (matchID == PROFILE_ID) ? getSelectionWithID(uri, selection) : selection;
            count = db.delete(TABLE_NAME, sel_str, selectionArgs);
        } else{
            sel_str = (matchID == WORKOUT_ID) ? getSelectionWithID(uri, selection) : selection;
             count = db.delete(TABLE_WORKOUTS, sel_str, selectionArgs);
        }
        notifyChange(uri);
        return  count;
    }
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case PROFILE:
                return "vnd.android.cursor.dir/vnd.alphafitness.profile";
            case WORKOUT:
                return "vnd.android.cursor.dir/vnd.alphafitness.workout";
            default:
                return "vnd.android.cursor.item/vnd.alphafitness.profile";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri _uri = null;

        switch (uriMatcher.match(uri)){
            case PROFILE:
                long _ID1 = db.insert(TABLE_NAME, "", values);
                //---if added successfully---
                if (_ID1 > 0) {
                    _uri = ContentUris.withAppendedId(URI1, _ID1);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case WORKOUT:
                long _ID2 = db.insert(TABLE_WORKOUTS, "", values);
                //---if added successfully---
                if (_ID2 > 0) {
                    _uri = ContentUris.withAppendedId(URI2, _ID2);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            default: throw new SQLException("Failed to insert row into " + uri);
        }
        return _uri;
    }

    @Override
    public boolean onCreate() {
        mcontext = getContext();
        if(mcontext == null){
            Log.e(TAG, "Failed to retrieve the context");
            return false;
        }
        DB dhHelper = new DB(mcontext);
        db = dhHelper.getWritableDatabase();
        if(db == null){
            Log.e(TAG, "Failed to create a writable database");
            return false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)){
            case PROFILE:
                sqLiteQueryBuilder.setTables(TABLE_NAME);
                sqLiteQueryBuilder.setProjectionMap(PROFILE_PROJECTION_MAP);
                break;
            case WORKOUT:
                sqLiteQueryBuilder.setTables(TABLE_WORKOUTS);
                sqLiteQueryBuilder.setProjectionMap(WORKOUT_PROJECTION_MAP);
                break;
            default:
                sqLiteQueryBuilder.appendWhere(selection);
        }
        Cursor c = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        c.setNotificationUri(mcontext.getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        String sel_str;
        int matchID = getMatchedID(uri);
        switch (matchID){
            case PROFILE:
                sel_str = (matchID == PROFILE_ID)? getSelectionWithID(uri, selection): selection;
                count = db.update(TABLE_NAME, values, sel_str, selectionArgs);
            case WORKOUT:
                sel_str = (matchID == WORKOUT_ID)? getSelectionWithID(uri, selection): selection;
                count = db.update(TABLE_WORKOUTS, values, sel_str, selectionArgs);
        }
        notifyChange(uri);
        return  count;
    }


}
