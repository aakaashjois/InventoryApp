package com.biryanistudio.inventoryapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Aakaash on 25/03/17 at 12:01 PM at 7:05 PM.
 */

class DatabaseContract {

    static final String CONTENT_AUTHORITY = "com.biryanistudio.inventoryapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_PRODUCTS_TABLE = "products_table";

    static final class ProductsEntry implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse(BASE_CONTENT_URI + "/" + PATH_PRODUCTS_TABLE);
        static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS_TABLE;
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS_TABLE;

        static final String TABLE_NAME = "products";
        static final String COLUMN_PRODUCTS_NAME = "name";
        static final String COLUMN_PRODUCTS_QUANTITY = "quantity";
        static final String COLUMN_PRODUCTS_PRICE = "price";
        static final String COLUMN_PRODUCTS_IMAGE_DATA = "imageData";
    }
}
