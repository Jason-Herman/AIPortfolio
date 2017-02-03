package seclass.gatech.edu.scm;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerSearch extends AppCompatActivity {
    DBUtilities dbu;
    ContentResolver contentResolver;
    Customer currentCustomer = null;
    ListView listView;
    TextView tvSelect;
    Customer[] customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);

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
        listView = (ListView) findViewById(R.id.lvCustomer);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // pass Customer object to MainActivity
            Intent intent = new Intent(CustomerSearch.this.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Search for a Customer object
     * @param view
     */
    public void search(View view) {
        //create selection string
        tvSelect = (TextView) findViewById(R.id.tvSelect);
        TextView tvHeader1 = (TextView) findViewById(R.id.tvHeader1);
        TextView tvHeader2 = (TextView) findViewById(R.id.tvHeader2);
        TextView tvHeader3 = (TextView) findViewById(R.id.tvHeader3);
        TextView tvHeader4 = (TextView) findViewById(R.id.tvHeader4);
        EditText etName = (EditText) findViewById(R.id.etName);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);
        EditText etAddress = (EditText) findViewById(R.id.etAddress);
        EditText etID = (EditText) findViewById(R.id.etID);

        String name = etName.getText().toString();
        String email = etEmail.getText().toString();
        String address = etAddress.getText().toString();
        String id = etID.getText().toString();

        //Get the customer array
        String result = searchInfoValidator(name, email, address, id);
        switch (result) {
            case "none":
                tvSelect.setText("No Matches");
                tvHeader1.setText("");
                tvHeader2.setText("");
                tvHeader3.setText("");
                tvHeader4.setText("");
                listView.setAdapter(null);
                break;
            case "invalid":
                AlertDialog.Builder builder1 = new AlertDialog.Builder(CustomerSearch.this);
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
                customers = customerSearch(name, email, address, id);

                displayResults(tvHeader1, tvHeader2, tvHeader3, tvHeader4);
                break;
        }

    }

    private void displayResults(TextView tvHeader1, TextView tvHeader2, TextView tvHeader3, TextView tvHeader4) {
        if (customers.length == 0) {
            tvSelect.setText("No Matches");
            tvHeader1.setText("");
            tvHeader2.setText("");
            tvHeader3.setText("");
            tvHeader4.setText("");
            listView.setAdapter(null);
        } else {
            tvSelect.setText("Select a match:");
            tvHeader1.setText("Name");
            tvHeader2.setText("Email");
            tvHeader3.setText("Address");
            tvHeader4.setText("ID");
            //tvHeaders.setText("Name          Email         Address           ID");
            //Populate list view with customers
            String[] from = new String[]{"col1", "col2", "col3", "col4"};
            int[] to = new int[]{R.id.display_customer_name,
                    R.id.display_customer_email,
                    R.id.display_customer_address,
                    R.id.display_customer_id};

            List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

            for (Customer c : customers) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("col1", c.getName());
                map.put("col2", c.getEmailAddress());
                map.put("col3", c.getBillingAddress());
                map.put("col4", c.getID());
                fillMaps.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.customer_items, from, to);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentCustomer = customers[position];
                    tvSelect.setText(currentCustomer.getName() + " selected");

                }
            });

        }
    }

    /**
     * Return to MainActivity (Dashboard)
     * @param view
     */
    public void ret(View view) {
        //return to main
        Intent intent = new Intent(view.getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (currentCustomer != null) {
            intent.putExtra("customer", currentCustomer);
        }
        startActivityForResult(intent, 0);
        finish();
    }

    public String searchInfoValidator(String name, String email, String address, String id){
        String result = "";
        if ((!Utilities.validateEmail(email) && !email.equals("")) || (!Utilities.validateString(name) && !name.equals("")) || (!Utilities.validateString(address)) && !address.equals("") || (!Utilities.validateString(id)) && !id.equals("")){
            result = "invalid";
        }
        else if (name.equals("") && email.equals("") && address.equals("") && id.equals("")) {
            result = "none";
        }
        else {
            result = "valid";
        }
        return result;
    }

    public Customer[] customerSearch(String name, String email, String address, String id) {
        name = name.trim();
        email = email.trim();
        address = address.trim();
        id = id.trim();
        DBUtilities dbu = new DBUtilities();
        String selection = "";
        if (!(name.equals(""))) {
            selection = selection + DBHelper.CUSTOMER_NAME + " LIKE UPPER('" + name + "')";
        }
        if (!(email.equals(""))) {
            if (!selection.equals("")) {
                selection = selection + " AND ";
            }
            selection = selection + DBHelper.CUSTOMER_EMAIL_ADDRESS + " LIKE UPPER('" + email + "')";
        }
        if (!(address.equals(""))) {
            if (!selection.equals("")) {
                selection = selection + " AND ";
            }
            selection = selection + DBHelper.CUSTOMER_BILLING_ADDRESS + " LIKE UPPER('" + address + "')";
        }
        if (!(id.equals(""))) {
            if (!selection.equals("")) {
                selection = selection + " AND ";
            }
            selection = selection + DBHelper.CUSTOMER_ID + " LIKE UPPER('" + id + "')";
        }

        Customer[] c = dbu.getCustomers(contentResolver, selection);
        return c;
    }

}
