package seclass.gatech.edu.scm;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.gatech.seclass.services.QRCodeService;

public class MainActivity extends AppCompatActivity {

    public static Customer currentCustomer;
    private static TextView tvCurrentCustomer;
    private DBUtilities dbu = null;
    private ContentResolver contentResolver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (dbu == null) {
            dbu = new DBUtilities();
        }

        if (contentResolver == null) {
            contentResolver = getContentResolver();
        }

        insertBaseCustomers();

        // Updated current customer with information from called activity
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            Customer passedCustomer = b.getParcelable("customer");
            if (passedCustomer != null)
            {
                if (currentCustomer == null)
                    currentCustomer = new Customer();

                currentCustomer.CopyCustomer(passedCustomer);
            }
        }

        // Set Dashboard with Customer ID
        if (currentCustomer != null) {
            tvCurrentCustomer = (TextView) findViewById(R.id.tvCurrentCustomer);
            tvCurrentCustomer.setText("Current Customer: " + currentCustomer.getName() + " (" + currentCustomer.getID() + ")");
        }
        else {
            tvCurrentCustomer = (TextView) findViewById(R.id.tvCurrentCustomer);
            tvCurrentCustomer.setText("Scan or Add a New Customer");
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    public void insertBaseCustomers() {
        DBUtilities dbu = new DBUtilities();
        try {
            dbu.insertCustomer(contentResolver, "b53b7c86ffeeaddbbe352f1f4dcd8e1a",
                    "Ralph Hapschatt", "77 Beale St, San Francisco, CA 94111", "ralphhapschatt@yahoo.com");
        } catch (Exception e) {
            Log.d("MainActivity", "Insert Ralph Hapschatt failed");
        }

        try {
            dbu.insertCustomer(contentResolver, "b6acb59441af4ea13129d8373df8145e", "Betty Monroe", "5 Embarcadero Center, San Francisco, CA 94111", "bettymonroe@yahoo.com");
        } catch (Exception e) {
            Log.d("MainActivity", "Insert Betty Monroe failed");
        }

        try {
            dbu.insertCustomer(contentResolver, "f184cd0f0e056d4c58c4b0264e5a6bcc", "Everett Scott", "12466 Kingsride Lane, Houston, TX 77024", "everettscott@gmail.com");
        } catch (Exception e) {
            Log.d("MainActivity", "Insert Everett Scott failed");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /***
     * Create a new Purchase for the current customer
     * @param view
     */
    public void newPurchase(View view) {
        if (currentCustomer == null) {
            showAlert("Please add new customer, scan customer card, or search for a customer.");
        } else {
            Intent intent = new Intent(view.getContext(), Purchase.class);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
        }
    }

    /***
     * Edit the current customer's information
     * @param view
     */
    public void editCustomer(View view) {
        if (currentCustomer == null) {
            showAlert("Please add new customer, scan customer card, or search for a customer.");
        } else {
            Intent intent = new Intent(view.getContext(), EditCustomer.class);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * View the current customer's rewards
     * @param view
     */
    public void viewRewards(View view) {
        if (currentCustomer == null) {
            showAlert("Please add new customer, scan customer card, or search for a customer.");
        } else {
            Intent intent = new Intent(view.getContext(), ViewRewards.class);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * View the current customer's transactions
     * @param view
     */
    public void viewTransactions(View view) {
        if (currentCustomer == null) {
            showAlert("Please add new customer, scan customer card, or search for a customer.");
        } else {
            Intent intent = new Intent(view.getContext(), ViewTransactions.class);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
        }
    }

    /**
     * Create a new customer
     * @param view
     */
    public void newCustomer(View view) {
        Intent intent = new Intent(view.getContext(), AddCustomer.class);
        intent.putExtra("customer", currentCustomer);
        startActivityForResult(intent, 0);
    }

    public void customerSearch(View view) {
        Intent intent = new Intent(view.getContext(), CustomerSearch.class);
        intent.putExtra("customer", currentCustomer);
        startActivityForResult(intent, 0);
    }

    /**
     * Scan a customer card
     * @param view
     */
    public void scan(View view) {
        Customer customer = scanCustomer();
        if (customer == null) {
            showAlert("Customer card scan failed. Please try again.");
        }
        else {
            currentCustomer = customer;
            tvCurrentCustomer.setText("Current Customer: " + currentCustomer.getName() + " (" + currentCustomer.getID() + ")");
        }
    }

    public Customer scanCustomer() {
        DBUtilities dbu = new DBUtilities();
        Customer customer = null;
        String id = QRCodeService.scanQRCode();
        if (!id.equals("ERR")) {
            customer = dbu.getCustomer(contentResolver, id);
        }
        return customer;
    }

    private void showAlert(String message){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(message);
        builder1.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //Method Code used for System testing
//    public void magic(View view) {
//        //Give customer gold Status
//        String id = currentCustomer.getID();
//        ContentValues values = new ContentValues();
//        values.put(DBHelper.CUSTOMER_GOLD_STATUS, 1);
//        dbu.updateCustomer(contentResolver, id, values);
//        currentCustomer = dbu.getCustomer(contentResolver,id);
//        //Give customer credit expiration date
//        String id = currentCustomer.getID();
//        ContentValues values = new ContentValues();
//        values.put(DBHelper.CUSTOMER_CREDIT_EXPIRATION, "2015-11-01");
//        values.put(DBHelper.CUSTOMER_CREDIT, 500);
//        dbu.updateCustomer(contentResolver, id, values);
//        currentCustomer = dbu.getCustomer(contentResolver,id);
//        //Add an arbitrary transaction for a customer and apply some total purchases.
//        String id = currentCustomer.getID();
//        ContentValues values = new ContentValues();
//        values.put(DBHelper.CUSTOMER_TOTAL_PURCHASES, 7000);
//        dbu.updateCustomer(contentResolver, id, values);
//        currentCustomer = dbu.getCustomer(contentResolver,id);
//
//        int subtotal = 7581;
//        int creditApplied = 500;
//        DateFormat dPEDdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date dPastDate = null;
//        try {
//            dPastDate = dPEDdf.parse("2014-11-01");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Transaction transaction = new Transaction(1, currentCustomer.getID(), subtotal, currentCustomer.getGoldStatus(), creditApplied, dPastDate);
//        dbu.insertTransaction(contentResolver, transaction);
//    }
}