package com.example.android.storeapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.storeapp.data.ProductContract.ProductEntry;
import com.example.android.storeapp.data.ProductDbHelper;


public class MainActivity extends AppCompatActivity {
    private TextView mProductData;
    private String LOGTAG = MainActivity.class.getName();
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProductData = (TextView) findViewById(R.id.product_data_text);


        FloatingActionButton fab;
        fab = findViewById(R.id.floating_intent_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Enter your product.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        displayData();
    }

    private void displayData() {
        mProductData.setText(R.string.product_text);

        ProductDbHelper mDbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {ProductEntry.COLUMN_NAME, ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY, ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};


        Cursor c = db.query(ProductEntry.TABLE_NAME, projection,
                null, null, null,
                null, null);

        try {
            if (!c.moveToNext()) {
                String noProducts = getString(R.string.product_text_absent);
                mProductData.append("\n" + "\n" + noProducts);
            }

            c.moveToPosition(-1);

            while (c.moveToNext()) {

                int nameIndex = c.getColumnIndex(ProductEntry.COLUMN_NAME);
                String name = c.getString(nameIndex);

                int priceIndex = c.getColumnIndex(ProductEntry.COLUMN_PRICE);
                int price = c.getInt(priceIndex);

                int quantityIndex = c.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
                int quantity = c.getInt(quantityIndex);

                int supplierIndex = c.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
                String supplier = c.getString(supplierIndex);

                int phoneIndex = c.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
                String phone = c.getString(phoneIndex);

                mProductData.append("\n" + "\n" + " " + name + " " + price +
                        " " + quantity + " " + supplier + " " + phone);

            }
        } catch (Exception e) {
            Log.e(LOGTAG, "Cursor could not retrieve data");
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void insertTestData() {
        ProductDbHelper mDbHelper = new ProductDbHelper(this);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_NAME, "Adidas Ultra Boost");
        values.put(ProductEntry.COLUMN_PRICE, 130);
        values.put(ProductEntry.COLUMN_QUANTITY, 5);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "SportDepot");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "0879376294");

        try {
            database.insert(ProductEntry.TABLE_NAME, null, values);
        } catch (SQLException e) {
            Log.e(LOGTAG, "Error inserting column values in " + ProductEntry.TABLE_NAME + " table");
        }
        displayData();
    }

    public void deleteProductData() {
        mContext.deleteDatabase(ProductDbHelper.DATABASE_NAME);
        displayData();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.insert_test_data:
                insertTestData();
                return true;
            case R.id.delete_data:
                deleteProductData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        displayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
}
