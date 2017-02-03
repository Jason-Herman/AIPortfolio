package seclass.gatech.edu.scm;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditCustomer extends AppCompatActivity {

    public static Customer currentCustomer;
    public DBUtilities dbu = null;
    public ContentResolver contentResolver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        //initialize dbUtilities
        if (dbu == null) {
            dbu = new DBUtilities();
        }

        if (contentResolver == null) {
            contentResolver = getContentResolver();
        }

        // Get customer and display


        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            currentCustomer = b.getParcelable("customer");
        }
        if (currentCustomer != null) {
            //Display current info
            TextView tvFirstName = (TextView) findViewById(R.id.tvName);
            tvFirstName.setText(currentCustomer.getName());

            TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
            tvEmail.setText(currentCustomer.getEmailAddress());

            TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
            tvAddress.setText(currentCustomer.getBillingAddress());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_customer, menu);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // pass Customer object to MainActivity
            Intent intent = new Intent(EditCustomer.this.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void update(View view) {
        EditText etName = (EditText) findViewById(R.id.etName);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etAddress = (EditText) findViewById(R.id.etAddress);

        String newName = etName.getText().toString();
        String newEmail = etEmail.getText().toString();
        String newAddress = etAddress.getText().toString();

        String result = updateInfoValidator(newName, newEmail, newAddress);
        switch (result) {
            case "none":
                //No changes, just return to MainActivity
                Customer customer = dbu.getCustomer(contentResolver, currentCustomer.getID());
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("customer", customer);
                startActivityForResult(intent, 0);
                break;
            case "invalid":
                AlertDialog.Builder builder1 = new AlertDialog.Builder(EditCustomer.this);
                builder1.setMessage("One or more of the entered values is invalid");
                builder1.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                break;
            case "valid":
                currentCustomer = editCustomerInfo(newName, newEmail, newAddress, currentCustomer.getID());
                ShowExitDialog(view);
                break;
        }
    }


    public String updateInfoValidator(String newName, String newEmail, String newAddress){
        String result = "";

        if ((newName.equals(""))&& (newEmail.equals("")) && (newAddress.equals("")))
        {
            result = "none";
        }
        else if ((!Utilities.validateEmail(newEmail) && !newEmail.equals("")) || (!Utilities.validateString(newName) && !newName.equals("")) || (!Utilities.validateString(newAddress)) && !newAddress.equals("")) {
            result = "invalid";
        }
        else if (!(newName.equals("")) || !(newEmail.equals("")) || !(newAddress.equals(""))) {
            result = "valid";
        }
        return result;
    }

    public Customer editCustomerInfo(String newName, String newEmail, String newAddress, String id){
        DBUtilities dbu = new DBUtilities();
        Customer customer = null;
        ContentValues values = new ContentValues();
        if (!(newName.equals(""))) {
            values.put(DBHelper.CUSTOMER_NAME, newName);
        }
        if (!(newEmail.equals(""))) {

            values.put(DBHelper.CUSTOMER_EMAIL_ADDRESS, newEmail);
        }

        if (!(newAddress.equals(""))) {
            values.put(DBHelper.CUSTOMER_BILLING_ADDRESS, newAddress);
        }

        if (values.size() != 0) {
            dbu.updateCustomer(contentResolver, id, values);
        }
        customer = dbu.getCustomer(contentResolver,id);
        return customer;
    }

    private void ShowExitDialog(View view)
    {
        // Display successful purchase dialog and return current Customer to MainActivity
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCustomer.this);
        builder.setMessage("Customer Update Successful.");
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        // pass Customer object to MainActivity
                        Intent intent = new Intent(EditCustomer.this.getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("customer", currentCustomer);
                        startActivityForResult(intent, 0);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
