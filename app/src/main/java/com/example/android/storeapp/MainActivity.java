package com.example.android.storeapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.storeapp.data.ProductContract.ProductEntry;
import com.example.android.storeapp.data.ProductCursorAdapter;
import com.example.android.storeapp.data.ProductDbHelper;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;
import static com.example.android.storeapp.data.ProductContract.BASE_CONTENT_URI;
import static com.example.android.storeapp.data.ProductContract.CONTENT_URI;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.android.storeapp.data.ProductContract.ProductEntry._ID;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private int LOADER_ID = 1;
    private String LOGTAG = MainActivity.class.getName();
    private Context mContext = this;
    private ProductCursorAdapter mProductAdapter;
    private Button mSaleButton;


    String[] mProjection = {_ID,
            COLUMN_NAME, COLUMN_PRICE,
            ProductEntry.COLUMN_QUANTITY, ProductEntry.COLUMN_SUPPLIER_NAME,
            ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSaleButton = (Button) findViewById(R.id.sale_button);
        LinearLayout emptyView = (LinearLayout)findViewById(R.id.empty_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_intent_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                                       startActivity(intent);
                                   }
                               }
        );
        getLoaderManager().initLoader(LOADER_ID, null, this);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        listView.setEmptyView(emptyView);
        mProductAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(mProductAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
    }

    public void clickOnSale(View v, long id, int quantity) {
        int i = quantity;
        if (i == 0) {
            Toast.makeText(this, getString(R.string.unsuccessful_sale),
                    Toast.LENGTH_SHORT).show();
        } else {
            i = i - 1;
            Uri currentPetUri = ContentUris.withAppendedId(CONTENT_URI, id);
            ContentValues values = new ContentValues();
            {
                values.put(COLUMN_QUANTITY, i);
            }
            int rowsAffected = getContentResolver().update(currentPetUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.unsuccessful_sale),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_sale),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void insertTestData() {

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_NAME, "Adidas Ultra Boost");
        values.put(ProductEntry.COLUMN_PRICE, 130);
        values.put(ProductEntry.COLUMN_QUANTITY, 5);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "SportDepot");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "0879376294");

        try {
            Uri uri = getContentResolver().insert(BASE_CONTENT_URI, values);
            Log.v("MainActivity", "New row URI =  " + uri);
        } catch (SQLException e) {
            Log.e(LOGTAG, "Error inserting column values in " + ProductEntry.TABLE_NAME + " table");
        }
    }

    public void deleteProductData() {
        getContentResolver().delete(BASE_CONTENT_URI, null, null);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                BASE_CONTENT_URI,
                mProjection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mProductAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductAdapter.swapCursor(null);
    }
}
