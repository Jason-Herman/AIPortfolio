package seclass.gatech.edu.scm;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import seclass.gatech.edu.scm.R;

public class ViewRewards extends AppCompatActivity {

    Customer currentCustomer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rewards);

        // Get customer and display
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            Customer customer = b.getParcelable("customer");
            customer = updateCustomerFromDatabase(customer);
            if (customer != null) {
                currentCustomer = customer;

                //Display current info
                TextView tvHeader = (TextView) findViewById(R.id.tvRewardsTitleHeader);
                tvHeader.setText("Viewing Rewards for " + customer.getName() + " (" + customer.getID() + ")");

                TextView tvCredit = (TextView) findViewById(R.id.tvRewardsCreditAmt);

                // Determine if it's a new year
                DBUtilities dbu = new DBUtilities();
                ContentResolver contentResolver = getContentResolver();
                Date lastDate = dbu.getDateOfLastTransaction(contentResolver, currentCustomer.getID());
                boolean bNewYear = Utilities.checkNewYear(lastDate, 0);

                // Get credit expiration date
                String creditExpDate = getCreditExpDate(customer.getCreditExpirationDate());

                // Get gold status accumulation expiration date
                Date cDate = new Date();
                cDate.getTime();
                String rewardDate = getGoldStatusAccExpDate(cDate);

                //Get customer purchases needed before achieving gold status
                String purchasesNeeded = getPurchasesNeeded(customer.getTotalPurchases());

                if (customer.getCredit() == 0){
                    tvCredit.setText("Credit: $" + (Utilities.centsToDollars(customer.getCredit())));
                } else {
                    tvCredit.setText("Credit: $" + (Utilities.centsToDollars(customer.getCredit())) + " (Expires " + creditExpDate + ")");
                }

                TextView tvGoldStatus = (TextView) findViewById(R.id.tvRewardsGoldStatus);
                if (!(customer.getGoldStatus()) && bNewYear)
                    tvGoldStatus.setText("Gold Status: Available in $500 before " + rewardDate);
                else if (!(customer.getGoldStatus()) && (customer.getTotalPurchases() < 50000))
                    tvGoldStatus.setText("Gold Status: Available in $" + purchasesNeeded + " before " + rewardDate);
                else
                    tvGoldStatus.setText("Currently has Gold Status.");
            } else {
                return;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // pass Customer object to MainActivity
            Intent intent = new Intent(ViewRewards.this.getApplicationContext(), MainActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_view_rewards, menu);
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

    public void ret(View view) {
        Intent intent = new Intent(ViewRewards.this.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("customer", currentCustomer);
        startActivityForResult(intent, 0);
        finish();
    }

    //Customer DB Headers
    private enum C_DB_HEADERS
    {
        ID,
        NAME,
        BILLING_ADDRESS,
        EMAIL_ADDRESS,
        CREDIT,
        CREDIT_EXP_DATE,
        GOLD_STATUS,
        CUSTOMER_TOTAL_PURCHASES
    };

    public Customer updateCustomerFromDatabase(Customer customer) {

        //Query database
        Cursor c = getContentResolver().query(
                SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                DBHelper.ALL_COLUMNS_CUSTOMER,
                "_id = '" + customer.getID() + "'",
                null,
                null);
        if (c.getCount() <= 0) {
            Log.d("ViewRewards", "updateCustomerFromDatabase - Customer " + customer.getID() + " does not exist in db.");
            c.close();
            return customer;
        }
        c.moveToNext();

        //Update customer credit
        customer.setCredit(c.getInt(C_DB_HEADERS.CREDIT.ordinal()));

        //Update customer credit expiration date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            Log.d("ViewRewards", "Date in db is: " + c.getString(C_DB_HEADERS.CREDIT_EXP_DATE.ordinal()));
            date = df.parse(c.getString(C_DB_HEADERS.CREDIT_EXP_DATE.ordinal()));
            customer.setCreditExpirationDate(date);
        } catch (ParseException e) {
            Log.d("ViewRewards", "updateCustomerFromDatabase - Failed to parse date string that was retrieved from database.");
        } catch (Exception e) {
            Log.d("ViewRewards", "updateCustomerFromDatabase - Something went wrong.");
        }

        //Update customer gold status
        boolean goldStatus = false;
        if (c.getInt(C_DB_HEADERS.GOLD_STATUS.ordinal()) == 0) {
            goldStatus = false;
        } else {
            goldStatus = true;
        }
        customer.setGoldStatus(goldStatus);

        //Update customer total purchases
        customer.setTotalPurchases(c.getInt(C_DB_HEADERS.CUSTOMER_TOTAL_PURCHASES.ordinal()));

        c.close();

        return customer;
    }

    public String getCreditExpDate(Date customerCreditExpDate) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        String creditExpDate = "...";
        if (customerCreditExpDate != null)
            creditExpDate = df.format(customerCreditExpDate);

        return creditExpDate;
    }

    public String getGoldStatusAccExpDate(Date cDate) {
        String rewardDate = "01/01/";
        //Date cDate = null;
        //int iYear = 2016;
        int iYear;
        try {
            // Get current date
            // Get date to earn gold status by (for the calendar year)
            //cDate = new Date();
            //cDate.getTime();
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy");
            String sYear = dFormat.format(cDate);
            iYear = Integer.parseInt(sYear) + 1;
            rewardDate += iYear;
        } catch (Exception e) {

        }

        return rewardDate;
    }

    public String getPurchasesNeeded(long customerTotalPurchases){

        Long lDiff = new Long(50000 - customerTotalPurchases);

        String purchasesNeeded = "";
        long cents = lDiff % 100;
        long dollars = (lDiff - cents) / 100;
        if (cents < 10) {
            purchasesNeeded = Long.toString(dollars) + ".0" + Long.toString(cents);
        } else {
            purchasesNeeded = Long.toString(dollars) + "." + Long.toString(cents);
        }

        return purchasesNeeded;
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

    /**
     * jUnit helper function for adding customer into db
     * @param
     * @return
     */
    public void addCustomer()
    {
        ContentResolver contentResolver = getContentResolver();
        DBUtilities dbu = new DBUtilities();
        dbu.insertCustomer(contentResolver, "b53b7c86ffeeaddbbe352f1f4dcd8e1a",
                "Ralph Hapschatt", "77 Beale St, San Francisco, CA 94111", "ralphhapschatt@yahoo.com");
        ContentValues values = new ContentValues();
        values.put(DBHelper.CUSTOMER_GOLD_STATUS, true);
        values.put(DBHelper.CUSTOMER_CREDIT, 500);
        values.put(DBHelper.CUSTOMER_TOTAL_PURCHASES, 4500);
        values.put(DBHelper.CUSTOMER_CREDIT_EXPIRATION, "2016-01-01");
        dbu.updateCustomer(contentResolver, "b53b7c86ffeeaddbbe352f1f4dcd8e1a", values);
    }

}
