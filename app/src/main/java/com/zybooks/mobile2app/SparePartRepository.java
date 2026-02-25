package com.zybooks.mobile2app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SparePartRepository {

    private final SparePartDbHelper dbHelper;

    public SparePartRepository(Context context) {
        this.dbHelper = new SparePartDbHelper(context);
    }


    // CREATE
    public boolean addItem(String oemNumber, String name, double price, String description, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues sparePartValues = packIntoValues(oemNumber, name, price, description, quantity);
        long row = db.insert(SparePartContract.SparePartEntry.TABLE_NAME, null, sparePartValues);
        return row >= 0;
    }

    // DELETE
    public boolean deleteItem(String oemNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = SparePartContract.SparePartEntry.COLUMN_OEM_NUMBER + " = ?";
        String[] whereArgs = {oemNumber};
        int rowsAffected = db.delete(SparePartContract.SparePartEntry.TABLE_NAME, where, whereArgs );
        return rowsAffected > 0;
    }


    // UPDATE
    public boolean updateItem(String oemNumber, String name, double price, String description, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues updatedValues = packIntoValues(oemNumber, name, price, description, quantity);
        String where = SparePartContract.SparePartEntry.COLUMN_OEM_NUMBER + " = ?";
        String[] whereArgs = {oemNumber};

        int rowsAffected = db.update(SparePartContract.SparePartEntry.TABLE_NAME, updatedValues, where, whereArgs);
        return rowsAffected > 0;
    }

    // COUNT
    public int getNumOfRows() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + SparePartContract.SparePartEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        int rowCount = 0;
        if(cursor.moveToFirst()) {
            //get count, only 1 row returned from COUNT()
            rowCount = cursor.getInt(0);
        }
        cursor.close();
        return rowCount;

    }

    // READ
    //used for syncing to db to get current items
    public ArrayList<SparePart> getAllItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                SparePartContract.SparePartEntry.TABLE_NAME,
                null, //all columns
                null, //no selection
                null,
                null,
                null,
                null

        );


        ArrayList<SparePart> currItemList = new ArrayList<>();

        //loop through db items (cursor)
        if(cursor.moveToFirst()) {
            do {
                SparePart currItem = new SparePart(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getString(4),
                        cursor.getInt(5)
                );
                currItemList.add(currItem);

            } while (cursor.moveToNext());
        }

        cursor.close();

        //return list of all SpareParts
        return currItemList;

    }

    private ContentValues packIntoValues(String oemNumber, String name, double price, String description, int quantity) {
        ContentValues values = new ContentValues();
        values.put(SparePartContract.SparePartEntry.COLUMN_OEM_NUMBER, oemNumber);
        values.put(SparePartContract.SparePartEntry.COLUMN_NAME, name);
        values.put(SparePartContract.SparePartEntry.COLUMN_PRICE, price);
        values.put(SparePartContract.SparePartEntry.COLUMN_DESCRIPTION, description);
        values.put(SparePartContract.SparePartEntry.COLUMN_QUANTITY, quantity);
        return values;
    }


}
