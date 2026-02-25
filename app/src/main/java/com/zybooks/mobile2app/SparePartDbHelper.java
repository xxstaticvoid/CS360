package com.zybooks.mobile2app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SparePartDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "inventory.db";
    private static final int DB_VERSION = 3;
    private final Context context;

    public SparePartDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_QUERY =
                "CREATE TABLE " + SparePartContract.SparePartEntry.TABLE_NAME + " ( " +
                        SparePartContract.SparePartEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        SparePartContract.SparePartEntry.COLUMN_OEM_NUMBER + " VARCHAR(50) UNIQUE NOT NULL, " +
                        SparePartContract.SparePartEntry.COLUMN_NAME + " VARCHAR(50) NOT NULL, " +
                        SparePartContract.SparePartEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                        SparePartContract.SparePartEntry.COLUMN_DESCRIPTION + " VARCHAR(75) NOT NULL, " +
                        SparePartContract.SparePartEntry.COLUMN_QUANTITY + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_QUERY);
        loadInventory(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DROP_QUERY =
                "DROP TABLE IF EXISTS " + SparePartContract.SparePartEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_QUERY);
        onCreate(db);
    }


    //method called once, only on Helper onCreate()
    private void loadInventory(SQLiteDatabase db) {
        //load items in

        //file located in app/res/raw/
        try (InputStream is = context.getAssets().open("inventory_state.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            //enable rollback if interrupted
            db.beginTransaction();

            String line;
            boolean first = true;

            while( (line = reader.readLine()) != null) {
                if(first) {
                    first = false;
                    continue;
                }

                String[] cols = line.split(",");

                for(int i = 0; i < cols.length; i++) {
                    cols[i] = cols[i].replace("\"", "").trim();
                }

                insertItem(
                        db,
                        cols[0],
                        cols[1],
                        Double.parseDouble(cols[2]),
                        cols[3],
                        Integer.parseInt(cols[4]));
            }

            //file parsed successfully
            db.setTransactionSuccessful();

        } catch(Exception e) {
            System.out.println("Error loading data from file");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    //only called on initial onCreate to load parts db
    private void insertItem(SQLiteDatabase db, String oemNumber, String name, double cost, String description, int quantity ) {
        ContentValues sparePartValues = new ContentValues();
        sparePartValues.put(SparePartContract.SparePartEntry.COLUMN_OEM_NUMBER, oemNumber);
        sparePartValues.put(SparePartContract.SparePartEntry.COLUMN_NAME, name);
        sparePartValues.put(SparePartContract.SparePartEntry.COLUMN_PRICE, cost);
        sparePartValues.put(SparePartContract.SparePartEntry.COLUMN_DESCRIPTION, description);
        sparePartValues.put(SparePartContract.SparePartEntry.COLUMN_QUANTITY, quantity);
        db.insert(SparePartContract.SparePartEntry.TABLE_NAME, null, sparePartValues);
    }



}
