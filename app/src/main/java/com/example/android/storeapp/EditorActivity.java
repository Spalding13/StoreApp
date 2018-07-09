package com.example.android.storeapp;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.storeapp.data.ImagePicker;
import com.example.android.storeapp.data.ProductContract.ProductEntry;

import static com.example.android.storeapp.data.ProductContract.CONTENT_URI;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_IMAGE;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME;
import static com.example.android.storeapp.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER;
import static com.example.android.storeapp.data.ProductContract.ProductEntry._ID;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mCurrentProduct;
    private int LOADER_ID = 1;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQiantityEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;
    private EditText mAdjustQuantity;
    private ImageButton mPlusButton;
    private ImageButton mMinusButton;
    public ImageView mProductImage;
    private static final int PICK_IMAGE_REQUEST = 0;
    public Uri actualUri;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public Context mContext = this;
    private String LOGTAG = EditorActivity.class.getName();
    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mNameEditText = (EditText) findViewById(R.id.edit_text_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_text_price);
        mQiantityEditText = (EditText) findViewById(R.id.edit_text_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_text_supplier);
        mPhoneEditText = (EditText) findViewById(R.id.edit_text_phone);
        mPlusButton = (ImageButton) findViewById(R.id.increase_quantity);
        mMinusButton = (ImageButton) findViewById(R.id.decrease_quantity);
        mProductImage = (ImageView) findViewById(R.id.product_image);
        mCurrentProduct = getIntent().getData();
        if (mCurrentProduct != null) {
            setTitle(R.string.edit_product_title);
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            setTitle(R.string.enter_product_title);
            mProductImage.setImageResource(R.drawable.add_picture_icon);
        }

        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOneToQuantity();
                mProductHasChanged = true;
            }
        });
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveOneToQuantity();
                mProductHasChanged = true;
            }
        });
        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.tryToOpenImageSelector(getBaseContext(), EditorActivity.this);
            }
        });

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQiantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mProductImage.setOnTouchListener(mTouchListener);
    }

    private void AddOneToQuantity() {
        String previousValueString = mQiantityEditText.getText().toString();
        int value;
        if (previousValueString.isEmpty()) {
            value = 0;
        } else {
            if (Integer.valueOf(previousValueString) < 0) {
                Toast.makeText(this, getString(R.string.invalid_input),
                        Toast.LENGTH_SHORT).show();
                mQiantityEditText.setText("0");
            }
            value = Integer.parseInt(previousValueString);
        }
        mQiantityEditText.setText(String.valueOf(value + 1));
    }

    private void RemoveOneToQuantity() {
        String previousValueString = mQiantityEditText.getText().toString();
        int value = Integer.valueOf(previousValueString);
        if (value == 0) {
            Toast.makeText(this, getString(R.string.invalid_input),
                    Toast.LENGTH_SHORT).show();
            mQiantityEditText.setText("0");
        } else {
            if (previousValueString.isEmpty()) {
                value = 0;
            } else {
                if (Integer.valueOf(previousValueString) < 0) {
                    Toast.makeText(this, getString(R.string.invalid_input),
                            Toast.LENGTH_SHORT).show();
                    mQiantityEditText.setText("0");
                }
                value = Integer.parseInt(previousValueString);
            }
            mQiantityEditText.setText(String.valueOf(value - 1));
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_product_data);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveProduct() {

        String name = mNameEditText.getText().toString().trim();

        String price = mPriceEditText.getText().toString().trim();

        String quantity = mQiantityEditText.getText().toString().trim();

        String Supplier = mSupplierEditText.getText().toString().trim();

        String PhoneString = mPhoneEditText.getText().toString().trim();

        String image;


        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(Supplier) ||
                TextUtils.isEmpty(PhoneString) ||
                TextUtils.isEmpty(price) ||
                TextUtils.isEmpty(PhoneString)) {
            Toast.makeText(this, getString(R.string.need_input),
                    Toast.LENGTH_SHORT).show();
        } else {
            if (mCurrentProduct == null &&
                    TextUtils.isEmpty(name) && TextUtils.isEmpty(price) &&
                    TextUtils.isEmpty(quantity) &&
                    TextUtils.isEmpty(Supplier) &&
                    TextUtils.isEmpty(PhoneString)) {
                return;
            }

            int Quantity = 0;
            if (!TextUtils.isEmpty(quantity)) {
                Quantity = Integer.parseInt(quantity);
            }
            int Price = 0;
            if (!TextUtils.isEmpty(price)) {
                Price = Integer.parseInt(price);
            }


            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_NAME, name);
            values.put(ProductEntry.COLUMN_PRICE, Price);
            values.put(ProductEntry.COLUMN_QUANTITY, Quantity);
            values.put(ProductEntry.COLUMN_SUPPLIER_NAME, Supplier);
            values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, PhoneString);

            if (actualUri == null) {
                Log.i(LOGTAG, "Image uri path: null");
            } else {
                image = actualUri.toString();
                values.put(ProductEntry.COLUMN_SUPPLIER_IMAGE, image);
                Log.i(LOGTAG, "Image uri path: " + image);
            }


            if (mCurrentProduct == null) {

                Uri newUri = getContentResolver().insert(CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.failed_update),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.successful_save),
                            Toast.LENGTH_SHORT).show();
                }
            } else {

                int rowsAffected = getContentResolver().update(mCurrentProduct, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.failed_update),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.successful_update),
                            Toast.LENGTH_SHORT).show();
                }
            }

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_order:
                String phone = mPhoneEditText.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, getString(R.string.need_input_phone), Toast.LENGTH_SHORT).show();
                } else {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) this, new
                                String[]{android.Manifest.permission.CALL_PHONE}, 0);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phone));
                        try {
                            startActivity(intent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(this, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {_ID,
                COLUMN_NAME, COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY, ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, ProductEntry.COLUMN_SUPPLIER_IMAGE};

        return new CursorLoader(this,
                mCurrentProduct,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE_NUMBER);
            int imageColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quanitity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String image;
            if (cursor.getString(imageColumnIndex) != null) {
                image = cursor.getString(imageColumnIndex);
                mProductImage.setImageURI(Uri.parse(image));
            }
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQiantityEditText.setText(Integer.toString(quanitity));
            mSupplierEditText.setText(supplier);
            mPhoneEditText.setText(phone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mPriceEditText.setText(null);
        mQiantityEditText.setText(Integer.toString(0));
        mSupplierEditText.setText(null);
        mPhoneEditText.setText(null);
        mProductImage.setImageURI(null);
    }

    private void showDeleteConfirmationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.keep, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deletePet() {
        if (mCurrentProduct != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProduct, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.unsuccessful_delete),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful_delete),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker.openImageSelector(EditorActivity.this);
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (data != null) {
                actualUri = data.getData();
                mProductImage.setImageURI(actualUri);
                mProductImage.invalidate();
            }
        }
    }

}
