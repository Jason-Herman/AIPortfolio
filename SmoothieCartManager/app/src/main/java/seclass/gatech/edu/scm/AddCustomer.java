package seclass.gatech.edu.scm;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AddCustomer extends AppCompatActivity {
    DBUtilities dbu;
    ContentResolver contentResolver;
    Customer currentCustomer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        if (dbu == null)
            dbu = new DBUtilities();

        if (contentResolver == null)
            contentResolver = getContentResolver();

        currentCustomer = null;

        //TextView tvCurrentCustomer = (TextView) findViewById(R.id.tvCurrentCustomer);
        //tvCurrentCustomer.setVisibility(View.GONE);

        TextView tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
        tvErrorMsg.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // pass Customer object to MainActivity
            Intent intent = new Intent(AddCustomer.this.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * @param view
     */
    public void add (View view) {
        Uri uri;

        TextView tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
        tvErrorMsg.setVisibility(View.GONE);

        if (currentCustomer != null)
            currentCustomer = null;

        currentCustomer = new Customer();  // Create object for new Customer

        // Update UI with new customer information
        String name = ( (EditText) findViewById(R.id.etName) ).getText().toString();
        String address = ( (EditText) findViewById(R.id.etAddress) ).getText().toString();
        String email = ( (EditText) findViewById(R.id.etEmail) ).getText().toString();

        if (!Utilities.validateEmail(email) || !Utilities.validateString(name) || !Utilities.validateString(address)) {
            TextView tvErrorMessage = (TextView) findViewById(R.id.tvErrorMsg);
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("One or more of the entered values is invalid");
            return;
        }

        currentCustomer = addCustomer(name, address, email);

        // Pass current customer object back to MainActivity
        if (currentCustomer != null) {
            Log.d("MainActivity", "Name: " + currentCustomer.getName() + " Billing: " + currentCustomer.getBillingAddress() + " Email: " + currentCustomer.getEmailAddress());
            ShowExitDialog(view);
        } else {
            TextView tvErrorMessage = (TextView) findViewById(R.id.tvErrorMsg);
            tvErrorMessage.setVisibility(View.VISIBLE);
            tvErrorMessage.setText("Failed to add customer");
        }
    }


    /**
     * Show dialog for exiting the activity
     */
    private void ShowExitDialog(View view)
    {
        // Display successful purchase dialog and return current Customer to MainActivity
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCustomer.this);
        builder.setMessage("Customer Add Successful.");
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        // pass Customer object to MainActivity
                        Intent intent = new Intent(AddCustomer.this.getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("customer", currentCustomer);
                        startActivityForResult(intent, 0);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public Customer addCustomer (String name, String address, String email) {
        Uri uri;
        Customer customer;
        Cursor c = contentResolver.query(
                SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                DBHelper.ALL_COLUMNS_CUSTOMER,
                DBHelper.CUSTOMER_NAME + " = '" + name + "' and " +
                        DBHelper.CUSTOMER_BILLING_ADDRESS + " = '" + address + "' and " +
                        DBHelper.CUSTOMER_EMAIL_ADDRESS + " = '" + email + "'",
                null,
                null);

        if (c == null) {
            return null;
        }

        if (c.getCount() > 0) {
            c.moveToNext();
            customer = dbu.getCustomer(contentResolver, c.getString(DBUtilities.C_DB_HEADERS.ID.ordinal()));
        } else {
            uri = dbu.insertCustomer(contentResolver, name, address, email);
            customer = dbu.getCustomer(contentResolver, uri.getLastPathSegment());
        }

        c.close();
        return customer;
    }

}
