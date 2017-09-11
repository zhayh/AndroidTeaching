package edu.niit.android.criminalintent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.niit.android.criminalintent.CrimeDbSchema.CrimeTable;

/**
 * Created by zhayh on 2017-9-7.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crime.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeTable.NAME +
                " (_id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED + ", " +
                CrimeTable.Cols.SUSPECT + ")"
        ) ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
