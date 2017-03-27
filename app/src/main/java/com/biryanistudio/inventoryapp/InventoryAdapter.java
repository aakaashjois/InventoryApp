package com.biryanistudio.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.biryanistudio.inventoryapp.DatabaseContract.ProductsEntry;

import java.util.Locale;

/**
 * Created by Aakaash on 25/03/17 at 3:55 PM at 7:05 PM.
 */

class InventoryAdapter extends CursorAdapter {

    InventoryAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final TextView title = (TextView) view.findViewById(R.id.product_title);
        final TextView quantity = (TextView) view.findViewById(R.id.product_quantity);
        final TextView price = (TextView) view.findViewById(R.id.product_price);
        final Button sell = (Button) view.findViewById(R.id.product_sell);

        final int titleColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_PRODUCTS_NAME);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_PRODUCTS_QUANTITY);
        final int priceColumnIndex = cursor.getColumnIndex(ProductsEntry.COLUMN_PRODUCTS_PRICE);

        title.setText(cursor.getString(titleColumnIndex));
        quantity.setText(String.format(Locale.getDefault(), "%d", cursor.getInt(quantityColumnIndex)));
        price.setText(String.format(Locale.getDefault(), "%d", cursor.getInt(priceColumnIndex)));

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productQuantity = cursor.getInt(quantityColumnIndex);
                if (productQuantity > 0) {
                    productQuantity--;
                    ContentValues values = new ContentValues();
                    values.put(ProductsEntry.COLUMN_PRODUCTS_QUANTITY, productQuantity);
                    int rowsAffected = context.getContentResolver()
                            .update(ContentUris.withAppendedId(
                                    ProductsEntry.CONTENT_URI,
                                    cursor.getInt(cursor.getColumnIndex(ProductsEntry._ID))),
                                    values,
                                    null,
                                    null);
                    if (rowsAffected == 0)
                        Toast.makeText(context, R.string.cannot_update_data, Toast.LENGTH_SHORT).show();
                    else
                        quantity.setText(String.format(Locale.getDefault(), "%d", productQuantity));
                } else
                    quantity.setText(R.string.no_stock);
            }
        });
    }
}
