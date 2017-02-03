package seclass.gatech.edu.scm;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import seclass.gatech.edu.scm.R;

public class ViewTransactions extends AppCompatActivity {

    private static Customer currentCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);

        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            Customer customer = b.getParcelable("customer");
            if (customer != null) {
                //Display current info
                TextView tvName = (TextView) findViewById(R.id.tvTransTitleHeader);
                tvName.setText("Viewing Transactions for " + customer.getName() + " (" + customer.getID() + ")");

                // Get all customer transactions and display them
                //Query for customer transaction information from db
                ContentResolver contentResolver = getContentResolver();
                Cursor c = contentResolver.query(
                        SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                        DBHelper.ALL_COLUMNS_TRANSACTION,
                        DBHelper.TRANSACTION_CUSTOMER_ID + " = '" + customer.getID() + "'",
                        null,
                        DBHelper.TRANSACTION_DATE + " DESC");

                int count = c.getCount();
                if (count <= 0) {
                    TextView tvName1 = (TextView) findViewById(R.id.textView);
                    TextView tvHeader1 = (TextView) findViewById(R.id.display_transaction_date);
                    TextView tvHeader2 = (TextView) findViewById(R.id.display_transaction_prediscount_amount);
                    TextView tvHeader3 = (TextView) findViewById(R.id.display_transaction_credits_applied);
                    TextView tvHeader4 = (TextView) findViewById(R.id.display_transaction_gold_status);
                    tvName1.setText("No transactions made.");
                    tvHeader1.setText("");
                    tvHeader2.setText("");
                    tvHeader3.setText("");
                    tvHeader4.setText("");

                    Log.d("ViewTransactions", "Customer " + customer.getID() + " has no transactions in DB.");
                    c.close();
                }

                String[] from = new String[]{"col1", "col2", "col3", "col4"};
                int[] to = new int[]{R.id.display_transaction_date,
                        R.id.display_transaction_prediscount_amount,
                        R.id.display_transaction_credits_applied,
                        R.id.display_transaction_gold_status};

                List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

                int index = 0;
                String goldStatusDiscount;
                while (c.moveToNext()) {

                    if (c.getInt(T_DB_HEADERS.GOLD_STATUS.ordinal()) == 0) {
                        goldStatusDiscount = "N/A";
                    } else {
                        goldStatusDiscount = "$" + Utilities.centsToDollars(goldDiscount(c.getInt(T_DB_HEADERS.PREDISCOUNT_AMOUNT.ordinal())));
                    }

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("col1", c.getString(T_DB_HEADERS.DATE.ordinal()));
                    map.put("col2", "$" + Utilities.centsToDollars(c.getInt(T_DB_HEADERS.PREDISCOUNT_AMOUNT.ordinal())));
                    map.put("col3", "$" + Utilities.centsToDollars(c.getInt(T_DB_HEADERS.CREDITS_APPLIED.ordinal())));
                    map.put("col4", goldStatusDiscount);
                    fillMaps.add(map);

                    index++;
                }

                c.close();

                SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.transaction_items, from, to);
                ListView listView = (ListView) findViewById(android.R.id.list);
                listView.setAdapter(adapter);
            } else {
                return;
            }
        }
    }

    //Transaction DB Headers
    private enum T_DB_HEADERS
    {
        ID,
        CUSTOMER_ID,
        PREDISCOUNT_AMOUNT,
        GOLD_STATUS,
        CREDITS_APPLIED,
        DATE
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // pass Customer object to MainActivity
            Intent intent = new Intent(ViewTransactions.this.getApplicationContext(), MainActivity.class);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_transactions, menu);
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
     * Return to the MainActivity (Dashboard)
     * @param view
     */
    public void ret(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, 0);
        finish();
    }

    public int goldDiscount(int amount) {
        int discount = (int) (amount * .05);
        return discount;
    }

    /**
     * jUnit helper function for setting customer object in class
     * @param tCustomer
     * @return
     */
    public boolean setCustomer(Customer tCustomer)
    {
        try {
            if (currentCustomer == null)
                currentCustomer = new Customer();

            currentCustomer.CopyCustomer(tCustomer);
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }


}
