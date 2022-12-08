package com.example.proartbook;

import android.content.ContentProvider;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {


    static final String PROVIDER_NAME = "com.example.proartbook.ArtContentProvider";
    static final String URL = "content://"+PROVIDER_NAME+"/arts";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String NAME = "name";
    static final String IMAGE = "image";
    static final int ARTS = 1;
    private static  HashMap<String,String> ART_PROJECTION_MAP ;

    static  final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
    }

    private SQLiteDatabase sqLiteDatabase;
    static final String DATABASE_NAME = "Arts";
    static final String ARTS_TABLE_NAME= "arts";
    static final String CREATE_DATABASE_TABLE = "CREATE TABLE "+
            ARTS_TABLE_NAME + " (name TEXT NOT NULL," +
            " image BLOB NOT NULL);";



    private static class  DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context) {
            super(context,DATABASE_NAME,null,1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DATABASE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ARTS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    @Override
    public boolean onCreate() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return sqLiteDatabase != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);


        switch (uriMatcher.match(uri)){
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);
                break;

            default:

        }


        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase,strings,s,strings1,null,null,s1);
        cursor.setNotificationUri(getContext().getContentResolver(),uri); // Uri değişiklik olursa haber veriyor.

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long rowId = sqLiteDatabase.insert(ARTS_TABLE_NAME,null,contentValues);

        if (rowId > 0){
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI,rowId); // eklenen verinin adresini ekliyoruz.
            getContext().getContentResolver().notifyChange(newUri,null);
            return newUri;
        }
        throw  new SQLException("Failed");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int rowCount=0;
        switch (uriMatcher.match(uri)){
            case ARTS:
                   rowCount= sqLiteDatabase.delete(ARTS_TABLE_NAME,s,strings);
                break;
            default:
                throw  new IllegalArgumentException("Failed");
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return rowCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        int rowCount=0;

        switch (uriMatcher.match(uri)){
            case ARTS:
               rowCount = sqLiteDatabase.update(ARTS_TABLE_NAME,contentValues,s,strings);
                break;
            default:
                throw new IllegalArgumentException("Failed");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return 0;
    }
}
