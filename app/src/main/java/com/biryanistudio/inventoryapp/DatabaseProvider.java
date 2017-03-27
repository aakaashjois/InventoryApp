package com.biryanistudio.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.biryanistudio.inventoryapp.DatabaseContract.ProductsEntry;

/**
 * Created by Aakaash on 25/03/17 at 12:53 PM at 7:05 PM.
 */

public class DatabaseProvider extends ContentProvider {

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DatabaseHelper helper;

    static {
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_PRODUCTS_TABLE, PRODUCTS);
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_PRODUCTS_TABLE + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                return helper.getReadableDatabase().query(
                        ProductsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            case PRODUCT_ID:
                return helper.getReadableDatabase().query(
                        ProductsEntry.TABLE_NAME,
                        projection,
                        ProductsEntry._ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                if (values != null) {
                    long id = helper.getWritableDatabase().insert(ProductsEntry.TABLE_NAME, null, values);
                    if (id == -1)
                        return null;
                    return ContentUris.withAppendedId(uri, id);
                }
                return null;
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                return helper.getWritableDatabase().delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
            case PRODUCT_ID:
                return helper.getWritableDatabase().delete(ProductsEntry.TABLE_NAME, ProductsEntry._ID + "=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
            default:
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                return helper.getWritableDatabase().update(ProductsEntry.TABLE_NAME, values, selection, selectionArgs);
            case PRODUCT_ID:
                return helper.getWritableDatabase().update(ProductsEntry.TABLE_NAME, values, ProductsEntry._ID + "=?", new String[]{String.valueOf(ContentUris.parseId(uri))});
            default:
                return 0;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PRODUCTS:
                return ProductsEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
