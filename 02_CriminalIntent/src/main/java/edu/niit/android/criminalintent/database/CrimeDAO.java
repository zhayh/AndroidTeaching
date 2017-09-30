package edu.niit.android.criminalintent.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import edu.niit.android.criminalintent.Crime;

/**
 * Created by zhayh on 2017-9-26.
 *
 * 完成Crime的CRUD操作
 */

public class CrimeDAO {
    private CrimeBaseHelper dbHelper;
    private SQLiteDatabase db;

    public CrimeDAO(Context context) {
        dbHelper = new CrimeBaseHelper(context);
    }

    public Cursor selectCursor() {
        db = dbHelper.getReadableDatabase();
        return db.query(CrimeDbSchema.CrimeTable.NAME, null, null, null, null, null, null);
//        return db.rawQuery("SELECT  * FROM " + CrimeDbSchema.CrimeTable.NAME, null);
    }

    public List<Crime> selectAll() {
        List<Crime> crimes = new ArrayList<>();

        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(CrimeDbSchema.CrimeTable.NAME, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                Crime crime = new Crime(UUID.fromString(cursor.getString(1)));
                crime.setTitle(cursor.getString(2));
                crime.setDate(new Date(cursor.getLong(3)));
                crime.setSolved(cursor.getInt(4) != 0);
                crime.setSuspect(cursor.getString(5));

                crimes.add(crime);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return crimes;
    }

    public void insert(Crime crime) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(crime);

        // 参数：表名，用于在未指定添加数据的情况下给某些可为空的列自动赋值null，添加值的ContentValues对象
        db.insert(CrimeDbSchema.CrimeTable.NAME, null, values);

//        String sql = "insert into crime(uuid, title, date, solved, suspect) values (?,?,?,?,?)";
//        db.execSQL(sql,
//                new String[]{crime.getId().toString(),
//                crime.getTitle(),
//                String.valueOf(crime.getDate().getTime()),
//                String.valueOf(crime.isSolved() ? 1 : 0),
//                crime.getSuspect()});
    }

    public void update(Crime crime) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(crime);

        // 参数：表名，修改值的ContentValues对象，SQL语句的where部分，第3个参数的每个占位符的内容的String数组
        db.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] {crime.getId().toString()});

//        String sql = "update student set title=?, date=?, solved=?, suspect=? where uuid=?";
//        db.execSQL(sql,
//                new String[]{crime.getTitle(),
//                        String.valueOf(crime.getDate().getTime()),
//                        String.valueOf(crime.isSolved() ? 1 : 0),
//                        crime.getSuspect()});
    }

    public void delete(UUID uuid) {
        db = dbHelper.getWritableDatabase();
        // 参数：表名，SQL语句的where部分，第3个参数的每个占位符的内容的String数组
        db.delete(CrimeDbSchema.CrimeTable.NAME, "uuid=?", new String[] {uuid.toString()} );

//        String sql = "delete from student where uuid=?";
//        db.execSQL(sql, new String[]{uuid.toString()});
    }

    public Crime select(String uuid) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from crime where uuid=?", new String[] {uuid});
        if(cursor != null) {
            cursor.moveToFirst();
            Crime crime = new Crime();

            return crime;
        }
        return null;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }
}
