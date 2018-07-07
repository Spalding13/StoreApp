package com.example.android.storeapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.storeapp.data.ProductContract.CONTENT_AUTHORITY;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.android.storeapp.data.ProductContract.ProductEntry._ID;

public class ProductProvider extends ContentProvider {

    private ProductDbHelper mDbHelper;
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, "/products", PRODUCT);
        sUriMatcher.addURI(CONTENT_AUTHORITY, "/products/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query with this URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product needs a name");
        }
        Integer price = contentValues.getAsInteger(COLUMN_PRICE);
        if (price == null && price < 0) {
            throw new IllegalArgumentException("Product needs a price");
        }
        Integer quantity = contentValues.getAsInteger(COLUMN_QUANTITY);
        if (quantity == null && quantity < 0) {
            throw new IllegalArgumentException("Invalid input for quantity");
        }
        String supplierName = contentValues.getAsString(COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Product needs a name");
        }
        String supplierNumber = contentValues.getAsString(COLUMN_SUPPLIER_PHONE_NUMBER);
        if (supplierNumber == null) {
            throw new IllegalArgumentException("Product needs a name");
        }
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Provider cant perform any action with given Uri" + uri);

        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rows;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                rows = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Provider cant perform any action with given Uri");
        }
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String
            selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Provider cant perform any action with given Uri" + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(COLUMN_NAME)) {
            String name = values.getAsString(COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (values.containsKey(COLUMN_PRICE)) {
            Integer price = values.getAsInteger(COLUMN_PRICE);
            if (price == null || !ProductContract.ProductEntry.isValidPrice(price)) {
                throw new IllegalArgumentException("Product requires valid gender");
            }
        }
        if (values.containsKey(COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        int rows = mDbHelper.getWritableDatabase().update(TABLE_NAME, values, selection, selectionArgs);
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return ProductContract.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
