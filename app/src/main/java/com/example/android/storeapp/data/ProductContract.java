package com.example.android.storeapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Spalding on 5/25/2018.
 */

public final class ProductContract {

    private ProductContract() {
    }

    public static abstract class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "number";
        public static final String COLUMN_SUPPLIER_IMAGE = "image";

        public static boolean isValidPrice(int price) {
            if (price != 0 && price > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String CONTENT_AUTHORITY = "com.example.android.storeapp";

    public static final String PATH_PRODUCTS = "/products";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY + PATH_PRODUCTS);

    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_PRODUCTS);

    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

    static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

}