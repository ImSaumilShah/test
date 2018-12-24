package skillders.com.booksqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Naisargi on 28-Dec-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BookDetails.db";
    public String SQL_CREATE_ENTRIES;

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createTable();
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    public void createTable(){

        SQL_CREATE_ENTRIES="CREATE TABLE "+FeedEntry.TABLE_NAME+" ( "+FeedEntry._ID + " INTEGER PRIMARY KEY, "
                            +FeedEntry.COLUMN_NAME+" TEXT, " + FeedEntry.COLUMN_AUTHOR +" TEXT, "
                            + FeedEntry.COLUMN_PRICE +" REAL, " +FeedEntry.COLUMN_YEAR+" TEXT, " +FeedEntry.COLUMN_IMAGE+" TEXT )";

    }

    public void addBook(String bname, String author, String price, String year, String image) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME,bname);
        values.put(FeedEntry.COLUMN_AUTHOR,author);
        values.put(FeedEntry.COLUMN_PRICE,price);
        values.put(FeedEntry.COLUMN_YEAR,year);
        values.put(FeedEntry.COLUMN_IMAGE,image);
        Log.d("in databasehandler",bname+"" +author+""+price+"");

        long genID = db.insert(FeedEntry.TABLE_NAME,null,values);
        Log.d("Database Handler",genID+"");
    }

    public UserBean getBook(int idx){
        UserBean ub = new UserBean("","",null,"","");
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {FeedEntry._ID,FeedEntry.COLUMN_NAME,FeedEntry.COLUMN_AUTHOR,
                                FeedEntry.COLUMN_PRICE ,FeedEntry.COLUMN_YEAR,FeedEntry.COLUMN_IMAGE};

        String selection = FeedEntry._ID + " = ? ";
        String[] selectionArgs ={(idx)+""};

        Cursor cs = db.query(FeedEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,null);
        while(cs.moveToNext()){

            cs.getColumnIndexOrThrow(FeedEntry._ID);

            ub.setBname(cs.getString(1));
            ub.setAuthor(cs.getString(2));
            ub.setPrice(cs.getFloat(3));
            ub.setYear(cs.getString(4));
            ub.setImage(cs.getString(5));

        }

        return ub;
    }

    public Cursor getAllBook(){

        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {FeedEntry._ID,FeedEntry.COLUMN_NAME,FeedEntry.COLUMN_AUTHOR,
                FeedEntry.COLUMN_PRICE ,FeedEntry.COLUMN_YEAR,FeedEntry.COLUMN_IMAGE};

        Cursor cs = db.query(FeedEntry.TABLE_NAME,projection,null,null,null,null,null);

        return cs;
    }

    public void UpdateBook(int position, String bname, String author, String price, String year, String image){

        ContentValues values =  new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        values.put(FeedEntry.COLUMN_NAME,bname);
        values.put(FeedEntry.COLUMN_AUTHOR,author);
        values.put(FeedEntry.COLUMN_PRICE,price);
        values.put(FeedEntry.COLUMN_YEAR,year);
        values.put(FeedEntry.COLUMN_IMAGE,image);

        String selection = FeedEntry._ID + "=?";

       String[] selectionArgs = {(position)+" "};

        db.update(FeedEntry.TABLE_NAME,values,selection,selectionArgs);

    }

    public void deleteBook(int position){
        UserBean ub = new UserBean("", " ", null, " ", " ");
        SQLiteDatabase db = getWritableDatabase();

        String selection = FeedEntry._ID + " =? ";
        String selectionArgs[] = {(position)+""};
        db.delete(FeedEntry.TABLE_NAME,selection,selectionArgs);
    }

















    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
