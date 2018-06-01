package com.example.android.storeapp;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.storeapp.data.ProductContract.ProductEntry;
import com.example.android.storeapp.data.ProductDbHelper;

import java.io.IOException;

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQiantityEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;
    private Button mButton;
    private String LOGTAG = EditorActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.edit_text_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_price);
        mQiantityEditText = (EditText) findViewById(R.id.edit_text_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_text_supplier);
        mPhoneEditText = (EditText) findViewById(R.id.edit_text_phone);
        mButton = (Button) findViewById(R.id.enter_product_button);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertProduct();
                Snackbar.make(view, "Product data saved.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            }
        });
    }


    private void insertProduct() {
        try {
            String Name = mNameEditText.getText().toString().trim();

            String PriceString = mPriceEditText.getText().toString().trim();
            int Price = Integer.parseInt(PriceString);

            String QuantityString = mQiantityEditText.getText().toString().trim();
            int Quantity = Integer.parseInt(QuantityString);

            String Supplier = mSupplierEditText.getText().toString().trim();

            String PhoneString = mPhoneEditText.getText().toString().trim();


            ProductDbHelper mDbHelper = new ProductDbHelper(this);

            SQLiteDatabase database = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_NAME, Name);
            values.put(ProductEntry.COLUMN_PRICE, Price);
            values.put(ProductEntry.COLUMN_QUANTITY, Quantity);
            values.put(ProductEntry.COLUMN_SUPPLIER_NAME, Supplier);
            values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, PhoneString);
            long newRowId = 0;

            try {
                newRowId = database.insert(ProductEntry.TABLE_NAME, null, values);
            } catch (SQLException e) {
                Log.e(LOGTAG, "Error inserting column values in " + ProductEntry.TABLE_NAME + " table");
            }

            if (newRowId == -1) {
                Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "row id: " + newRowId, Toast.LENGTH_SHORT).show();


            }
        }
        catch (NumberFormatException e){
            Log.e(LOGTAG,"Input error");
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
            Snackbar.make(null, "Input error", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
    }
}
