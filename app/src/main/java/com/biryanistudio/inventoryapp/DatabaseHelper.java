package com.biryanistudio.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.biryanistudio.inventoryapp.DatabaseContract.ProductsEntry;

/**
 * Created by Aakaash on 25/03/17 at 12:47 PM at 7:05 PM.
 */

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory";
    private static final int DATABASE_VERSION = 1;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + ProductsEntry.TABLE_NAME + " ("
                + ProductsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsEntry.COLUMN_PRODUCTS_NAME + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_PRODUCTS_QUANTITY + " INTEGER NOT NULL, "
                + ProductsEntry.COLUMN_PRODUCTS_PRICE + " INTEGER NOT NULL, "
                + ProductsEntry.COLUMN_PRODUCTS_IMAGE_DATA + " BLOB NOT NULL" + ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
