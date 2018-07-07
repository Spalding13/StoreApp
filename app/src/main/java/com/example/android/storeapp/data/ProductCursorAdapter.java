package com.example.android.storeapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeapp.MainActivity;
import com.example.android.storeapp.R;

import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.android.storeapp.data.ProductContract.ProductEntry._ID;

public class ProductCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    public ProductCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button)view.findViewById(R.id.sale_button);

        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int summaryColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String priceSummary = cursor.getString(summaryColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText("Price: " + priceSummary + " $");
        quantityTextView.setText(String.valueOf("Quantity: " + quantity));


      final long id = cursor.getLong(cursor.getColumnIndex(_ID));
      saleButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              activity.clickOnSale(view,id, quantity);
          }
      });
    }

}
