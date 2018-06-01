package com.example.android.storeapp.data;

import android.provider.BaseColumns;

/**
 * Created by Spalding on 5/25/2018.
 */

public final class ProductContract{

    private ProductContract() {}

    public static abstract class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "number";


    }
}