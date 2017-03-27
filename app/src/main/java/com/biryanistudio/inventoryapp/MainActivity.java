package com.biryanistudio.inventoryapp;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.biryanistudio.inventoryapp.DatabaseContract.ProductsEntry;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GET_IMAGE = 200;
    private static final int PRODUCT_LOADER = 201;
    private InventoryAdapter adapter;
    private final ContentValues values = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView productsList = (ListView) findViewById(R.id.products_list);
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        productsList.setEmptyView(emptyView);
        adapter = new InventoryAdapter(getApplicationContext());
        productsList.setAdapter(adapter);
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentProductUri = ContentUris.withAppendedId(ProductsEntry.CONTENT_URI, id);
                createDetailDialog(MainActivity.this, currentProductUri);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.add_menu)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        createAddProductDialog(MainActivity.this);
                        return true;
                    }
                }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    private void createDetailDialog(final Context context, final Uri productUri) {
        final Dialog detailDialog = new Dialog(context);
        detailDialog.setContentView(R.layout.detail_item_dialog);

        final TextView detailTitle = (TextView) detailDialog.findViewById(R.id.detail_title);
        final TextView detailQuantity = (TextView) detailDialog.findViewById(R.id.detail_quantity);
        final TextView detailPrice = (TextView) detailDialog.findViewById(R.id.detail_price);
        final ImageView detailImage = (ImageView) detailDialog.findViewById(R.id.detail_image);
        final Button detailBuy = (Button) detailDialog.findViewById(R.id.detail_buy);
        final Button detailSell = (Button) detailDialog.findViewById(R.id.detail_sell);
        final Button detailOrder = (Button) detailDialog.findViewById(R.id.detail_order);
        final Button detailDelete = (Button) detailDialog.findViewById(R.id.detail_delete);
        final Button detailDone = (Button) detailDialog.findViewById(R.id.detail_done);

        final Cursor cursor = getContentResolver().query(productUri,
                new String[]{
                        ProductsEntry._ID,
                        ProductsEntry.COLUMN_PRODUCTS_NAME,
                        ProductsEntry.COLUMN_PRODUCTS_QUANTITY,
                        ProductsEntry.COLUMN_PRODUCTS_PRICE,
                        ProductsEntry.COLUMN_PRODUCTS_IMAGE_DATA},
                null,
                null,
                null);

        if (cursor != null) {
            int titleColumnIndex = cursor.getColumnIndexOrThrow(
                    ProductsEntry.COLUMN_PRODUCTS_NAME);
            int quantityColumnIndex = cursor.getColumnIndexOrThrow(
                    ProductsEntry.COLUMN_PRODUCTS_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndexOrThrow(
                    ProductsEntry.COLUMN_PRODUCTS_PRICE);
            int imageColumnIndex = cursor.getColumnIndexOrThrow(
                    ProductsEntry.COLUMN_PRODUCTS_IMAGE_DATA);
            if (!cursor.moveToFirst())
                cursor.moveToFirst();
            if (titleColumnIndex == -1
                    && quantityColumnIndex == -1
                    && priceColumnIndex == -1
                    && imageColumnIndex == -1)
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            else {
                detailTitle.setText(cursor.getString(titleColumnIndex));
                detailQuantity.setText(String.format(Locale.getDefault(), "%d",
                        cursor.getInt(quantityColumnIndex)));
                detailPrice.setText(String.format(Locale.getDefault(), "%d",
                        cursor.getInt(priceColumnIndex)));
                byte[] imageData = cursor.getBlob(imageColumnIndex);
                detailImage.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
            }
        }

        detailBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor != null) {
                    int quantity = cursor.getInt(
                            cursor.getColumnIndex(ProductsEntry.COLUMN_PRODUCTS_QUANTITY));
                    quantity++;
                    ContentValues values = new ContentValues();
                    values.put(ProductsEntry.COLUMN_PRODUCTS_QUANTITY, quantity);
                    int rowAffected = getContentResolver().update(productUri, values, null, null);
                    if (rowAffected == 0)
                        Toast.makeText(context, R.string.cannot_update_data, Toast.LENGTH_SHORT).show();
                    else
                        detailQuantity.setText(quantity);
                }
            }
        });

        detailSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor != null) {
                    int quantity = cursor.getInt(cursor.getColumnIndex(
                            ProductsEntry.COLUMN_PRODUCTS_QUANTITY));
                    if (quantity > 0) {
                        quantity++;
                        ContentValues values = new ContentValues();
                        values.put(ProductsEntry.COLUMN_PRODUCTS_QUANTITY, quantity);
                        int rowAffected = getContentResolver().update(productUri,
                                values,
                                null,
                                null);
                        if (rowAffected == 0)
                            Toast.makeText(context, R.string.cannot_update_data,
                                    Toast.LENGTH_SHORT).show();
                        else
                            detailQuantity.setText(quantity);
                    } else
                        detailQuantity.setText(R.string.no_stock);
                }
            }
        });

        detailOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO,
                                Uri.parse("mailto:")),
                        getString(R.string.order_stock)));
                detailDialog.dismiss();
            }
        });

        detailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.delete_confirmation_title);
                builder.setMessage(R.string.delete_confirmation_message);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowDeleted = getContentResolver().delete(productUri, null, null);
                        if (rowDeleted == 0)
                            Toast.makeText(MainActivity.this, R.string.cannot_update_data,
                                    Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, R.string.delete_successful,
                                    Toast.LENGTH_SHORT).show();
                        detailDialog.dismiss();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        detailDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailDialog.dismiss();
            }
        });
        if (cursor != null) {
            cursor.close();
        }
    }

    private void createAddProductDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_item_dialog);
        dialog.setTitle(R.string.add_product);
        Button addImage = (Button) dialog.findViewById(R.id.input_product_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        "Select Image"),
                        GET_IMAGE);
            }
        });
        Button save = (Button) dialog.findViewById(R.id.input_product_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout productName = (TextInputLayout) dialog
                        .findViewById(R.id.input_product_name);
                EditText name = productName.getEditText();
                TextInputLayout productQuantity = (TextInputLayout) dialog
                        .findViewById(R.id.input_product_quantity);
                EditText quantity = productQuantity.getEditText();
                TextInputLayout productPrice = (TextInputLayout) dialog
                        .findViewById(R.id.input_product_price);
                EditText price = productQuantity.getEditText();
                if (name != null && name.getText() != null) {
                    if (!name.getText().toString().isEmpty()) {
                        if (quantity != null && quantity.getText() != null) {
                            if (!quantity.getText().toString().isEmpty() &&
                                    Integer.parseInt(quantity.getText().toString()) >= 0) {
                                if (price != null && price.getText() != null) {
                                    if (!price.getText().toString().isEmpty() &&
                                            Integer.parseInt(price.getText().toString()) >= 0) {
                                        if (values.getAsString(
                                                ProductsEntry.COLUMN_PRODUCTS_IMAGE_DATA) != null)
                                            if (!values.getAsString(ProductsEntry
                                                    .COLUMN_PRODUCTS_IMAGE_DATA).isEmpty()) {
                                                values.put(ProductsEntry.COLUMN_PRODUCTS_NAME,
                                                        name.getText().toString().trim());
                                                values.put(ProductsEntry.COLUMN_PRODUCTS_QUANTITY,
                                                        Integer.parseInt(quantity.getText()
                                                                .toString()));
                                                values.put(ProductsEntry.COLUMN_PRODUCTS_PRICE,
                                                        Integer.parseInt(price.getText()
                                                                .toString()));
                                                getContentResolver().insert(
                                                        ProductsEntry.CONTENT_URI, values);
                                                getLoaderManager().restartLoader(PRODUCT_LOADER, null, MainActivity.this).forceLoad();
                                                dialog.dismiss();
                                            } else
                                                Toast.makeText(context,
                                                        R.string.select_valid_image,
                                                        Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(context,
                                                    R.string.select_valid_image,
                                                    Toast.LENGTH_SHORT).show();
                                    } else
                                        productPrice.setError(getString(R.string.select_valid_price));
                                }
                            } else
                                productQuantity.setError(getString(R.string.select_valid_quantity));
                        }
                    } else
                        productName.setError(getString(R.string.select_valid_name));
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_IMAGE && resultCode == RESULT_OK && data != null) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor imageCursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            if (imageCursor != null) {
                imageCursor.moveToFirst();
                Bitmap image = BitmapFactory.decodeFile(imageCursor.getString(imageCursor.getColumnIndex(filePathColumn[0])));
                imageCursor.close();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] bytes;
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bytes = stream.toByteArray();
                values.put(ProductsEntry.COLUMN_PRODUCTS_IMAGE_DATA, bytes);
            }

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ProductsEntry.CONTENT_URI,
                new String[]{
                        ProductsEntry._ID,
                        ProductsEntry.COLUMN_PRODUCTS_NAME,
                        ProductsEntry.COLUMN_PRODUCTS_QUANTITY,
                        ProductsEntry.COLUMN_PRODUCTS_PRICE,
                        ProductsEntry.COLUMN_PRODUCTS_IMAGE_DATA},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
