package seclass.gatech.edu.scm;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

import edu.gatech.seclass.services.CreditCardService;
import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.services.PaymentService;

public class Purchase extends AppCompatActivity {
    public DBUtilities dbu = null;
    public ContentResolver contentResolver = null;

    private boolean emailSent = false;
    private boolean cardSwiped = false;
    private static Customer currentCustomer;
    private static int quantSmall = 0;
    private static int quantLarge = 0;
    public final int smallSmoothieCost = 299;
    public final int largeSmoothieCost = 399;

    private static TextView tvAvCredit;
    private static TextView tvSmall;
    private static TextView tvLarge;
    private static TextView tvSubtotal;
    private static TextView tvGoldDiscount;
    private static TextView tvCredit;
    private static TextView tvTotalDue;
    private static TextView tvCreditCard;

    private static int subtotal = 0;
    private static int goldDiscount = 0;
    private static int earnedCredit = 0;
    private static int remainingCredit = 0;
    private static int creditApplied = 0;
    private static int totalDue = 0;

    public DialogFragment successEmailDialog;
    public DialogFragment failedEmailDialog;

    private static String creditCardString = "ERR";

    public AlertDialog adFailMessage = null;
    public AlertDialog adExit = null;

    public int getTotal(){ return totalDue; }
    public int getSubtotal() { return subtotal; }
    public boolean getEmailSent() { return emailSent; }

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
            showFailureMessage("Failed to set customer object");
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        if (dbu == null)
            dbu = new DBUtilities();

        if (contentResolver == null)
            contentResolver = getContentResolver();

        // reset and init
        resetData();
        initUI();
        loadCustomerData();
    }

    /**
     * Initializes the UI
     */
    private void initUI()
    {
        // Retrieve UI objects
        tvSmall = (TextView) findViewById(R.id.tvSmall);
        if (tvSmall == null)
            returnOnError();

        tvLarge = (TextView) findViewById(R.id.tvLarge);
        if (tvLarge == null)
            returnOnError();

        tvSubtotal = (TextView) findViewById(R.id.tvSubtotal);
        if (tvSubtotal == null)
            returnOnError();

        tvGoldDiscount = (TextView) findViewById(R.id.tvGoldDiscount);
        if (tvGoldDiscount == null)
            returnOnError();

        tvCredit = (TextView) findViewById(R.id.tvCredit);
        if (tvCredit == null)
            returnOnError();

        tvTotalDue = (TextView) findViewById(R.id.tvTotalDue);
        if (tvTotalDue == null)
            returnOnError();

        tvCreditCard = (TextView) findViewById(R.id.tvCreditCard);
        if (tvCreditCard == null)
            returnOnError();

        tvAvCredit = (TextView) findViewById(R.id.tvAvCredit);
        if (tvAvCredit == null)
            returnOnError();

    }

    /**
     * Load the Customer data into the Activity and UI
     */
    private void loadCustomerData()
    {
        //Retrieve customer object data from the calling activity
        Bundle b = this.getIntent().getExtras();

        if (b != null) {
            currentCustomer = b.getParcelable("customer");
            if (currentCustomer == null)
                return;
			
			// Void old credit
            currentCustomer.VoidExpiredCredit();
		
			// Void old purchases on new year, only if they already don't have > $500 in purchases
			Date lastDate = dbu.getDateOfLastTransaction(contentResolver, currentCustomer.getID());
			if (Utilities.checkNewYear(lastDate, 0) && !currentCustomer.getGoldStatus())
				currentCustomer.setTotalPurchases(0);
        
            if (currentCustomer.getGoldStatus())
                tvGoldDiscount.setText("Gold Discount: 5%");
            else
                tvGoldDiscount.setText("Gold Discount: None");

            float credit = (currentCustomer.getCredit() / 100);
            String creditStr = String.format("%.2f", credit).replace(",", ".");

            tvAvCredit.setText("Available Credit: $" + creditStr);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase, menu);
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
            Intent intent = new Intent(Purchase.this.getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("customer", currentCustomer);
            startActivityForResult(intent, 0);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Sends specified email type
     *
     * @param emailType: Email to send - either credit or gold status
     */
    public void sendEmail(int emailType) {
        if (EmailService.sendMail(currentCustomer.getName(), currentCustomer.getID(), ""))
            showSuccessfulEmail(emailType);
        else
            showFailedEmail(emailType);

        emailSent = true;
    }

    /**
     * Add a small smoothie to the purchase
     * @param view: Current View
     */
    public void addSmall(View view) {
        addSmallItem();
    }

    /**
     * Adds small item to the purchase
     */
    public void addSmallItem()
    {
        quantSmall++;
        int amtSmall = smallSmoothieCost * quantSmall;
        String text = "Small Smoothie (Quantity: " + quantSmall + "): $" + Utilities.centsToDollars(amtSmall);
        updateTotals(smallSmoothieCost);
        updateTotalsUI();
        tvSmall.setText(text);
    }

    /**
     * Add a large smoothie to the purchase
     * @param view: Current View
     */
    public void addLarge(View view) {
        addLargeItem();
    }

    /**
     * Adds large item to the purchase
     */
    public void addLargeItem()
    {
        quantLarge++;
        int amtLarge = largeSmoothieCost * quantLarge;
        String text = "Large Smoothie (Quantity: " + quantLarge + "): $" + Utilities.centsToDollars(amtLarge);
        updateTotals(largeSmoothieCost);
        updateTotalsUI();
        tvLarge.setText(text);
    }

    /**
     * Swipe credit card
     *
     * @param view: Current View
     */
    public void swipe(View view) {

        if (cardSwiped) // don't scan if already succeeded
            return;

        String card = swipeCreditCard();
        if (card != "ERR")
            tvCreditCard.setText("Payment Method: Credit Card Accepted");
    }

    /**
     * Swipes Credit Card
     */
    public String swipeCreditCard()
    {
        creditCardString = CreditCardService.readCard();

        if (creditCardString.equals("ERR")) {
            showFailureMessage("Credit card scan failed. Please try again.");
            return creditCardString;
        }
        else  {
            cardSwiped = true;
            return creditCardString;
        }
    }

    /**
     * Cancel purchase
     *
     * @param view: Current View
     */
    public void cancel(View view) {
        resetData();
        finish();
    }

    /**
     * Retrieves information about Customer from the Credit Card
     * @param creditCard: Credit card to retrieve data from
     * @return Hashtable of customer information
     */
    public Hashtable retrieveCustomerCreditCardInfo(String creditCard)
    {
        // Ensure credit card information is present
        if (!cardSwiped || creditCard == null || creditCard.equals("")) {
            showFailureMessage("Cannot process. Please scan credit card.");
            return null;
        }

        // Get Customer values
        String[] customerTokens = creditCard.split("#");
        if (customerTokens.length != 5) {
            showFailureMessage("Cannot process. Invalid credit card data");
            return null;
        }

        // Retrieve the split customer data
        String[] tokens = { "firstName", "lastName", "cardNumber", "expDate", "secCode"};
        Hashtable userData = new Hashtable();
        for (int i = 0; i < tokens.length; i++)
        {
            if (customerTokens[i] != null)
                userData.put((String)tokens[i], customerTokens[i].trim());
        }

        return userData;
    }
    /**
     * Checks whether the credit card is expired or not
     * @return Date: Date of credit card expiration
     */
    public Date getCreditCardExpirationDate(String expDate)
    {
        if (expDate == null || expDate.isEmpty())
            return null;

        // Check expiration date of the card
        SimpleDateFormat format = new SimpleDateFormat("MMddyyyy", Locale.ENGLISH);
        Date ccExpirationDate = null;
        try {
            ccExpirationDate = format.parse(expDate);
        } catch (ParseException e) {
            showFailureMessage("Failed to process credit card. Please try again");
            return null;
        }

        return ccExpirationDate;
    }

    /**
     * Checks whether the credit card is expired or not
     * @return boolean: Boolean whether the credit card is expired or not
     */
    public boolean checkCreditCardExpired(Date ccExpDate)
    {
        if (ccExpDate == null)
            return true;

        Date currentDate = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
            Date tempCurrentDate = new Date();
            String strCurrentDate = dateFormat.format(tempCurrentDate);
            currentDate = dateFormat.parse(strCurrentDate);

            // Check expiration date
            if (currentDate == null || currentDate.after(ccExpDate)) {
                cardSwiped = false; // reset so we can scan another card
                return true;
            }
        }
        catch (Exception e)
        {
            String error = e.getLocalizedMessage();
            error = e.getMessage();
            return true;
        }

        return false;
    }

    /**
     * Process the transaction given the purchase information
     * @param firstName: Customer's first name
     * @param lastName: Customer's last name
     * @param cardNumber: Credit card number
     * @param ccExpDate: Credit card expiration date
     * @param ccSecCode: Credit card security code
     * @param total: Purchase total
     * @return boolean: Boolean wheether the transaction was processed succesfully or not
     */
    public boolean processTransaction(String firstName, String lastName, String cardNumber, Date ccExpDate, String ccSecCode, int total)
    {
        if (firstName == null || firstName.isEmpty()) {
            showFailureMessage("Can't process transaction. Invalid first name. Try again.");
            return false;
        }

        if (lastName == null || lastName.isEmpty()) {
            showFailureMessage("Can't process transaction. Invalid last name. Try again.");
            return false;
        }

        if (cardNumber == null || cardNumber.isEmpty()) {
            showFailureMessage("Can't process transaction. Invalid first name. Try again.");
            return false;
        }

        if (ccExpDate == null) {
            showFailureMessage("Can't process transaction. Credit Card Expiration date is invalid. Try again.");
            return false;
        }

        if (ccSecCode == null || ccSecCode.isEmpty()) {
            showFailureMessage("Can't process transaction. Invalid Credit Card security code. Try again.");
            return false;
        }

        if (total <= 0) {
            showFailureMessage("Can't process transaction. Invalid purchase total. Try again.");
            return false;
        }

        boolean processed = PaymentService.processTransaction(firstName, lastName, cardNumber, ccExpDate, ccSecCode, total);
        if (!processed) {
            showFailureMessage("Failed to process transaction. Please try again");
            return false;
        }

        return true;
    }

    public void processPurchase()
    {
        // Retrieve customer data from scanned credit card
        Hashtable userData = retrieveCustomerCreditCardInfo(creditCardString);
        if (userData == null)
            return;

        String expDate = (String)userData.get("expDate");
        String firstName = (String)userData.get("firstName");
        String lastName = (String)userData.get("lastName");
        String ccNumber = (String)userData.get("cardNumber");
        String securityCode = (String)userData.get("secCode");

        // Check items are in the purchase
        if (subtotal <= 0 && (quantSmall == 0) && (quantLarge == 0)) {
            showFailureMessage("Cannot process. No items have been added for purchase.");
            return;
        }

        // Determine whether credit card is expired or not
        Date ccExpirationDate = getCreditCardExpirationDate(expDate);
        boolean expired = checkCreditCardExpired(ccExpirationDate);
        if (expired) {
            showFailureMessage("Cannot process. Credit Card is expired.");
            return;
        }

        // Process Transaction
        if (subtotal > 0 && totalDue > 0)
        {
            boolean processSuccess = processTransaction(firstName, lastName, ccNumber, ccExpirationDate, securityCode, subtotal);
            if (!processSuccess)
                return;
        }

        boolean purchDateSet = setCustomerLastPurchaseDate();
        if (!purchDateSet)
            return;

        //create transaction and insert in DB
        boolean updatedDatabase = updateDatabaseWithPurchase(null);
        if (!updatedDatabase)
            return;

        // send email for purchase information if any was earned
        if (earnedCredit > 0)
            sendEmail(1);

        showExitDialog();
    }

    /**
     * Process a Purchase transaction
     *
     * @param view: Current View
     */
    public void process(final View view) {
        processPurchase();
    }

    /**
     * Updates the Database to add the new Purchase
     * @return
     */
    public boolean updateDatabaseWithPurchase(Date newDate)
    {
        Date dCurrentDate = newDate;
        if (newDate == null) {
            try {
                dCurrentDate = new Date();  // Get the current date
                dCurrentDate.getTime();
            } catch (Exception e) {
                showFailureMessage("Failed to get current date.");
                return false;
            }
        }

        // collect customer values to be updated
        // set totalPurchases for db update
        ContentValues values = new ContentValues();
        long lifePurchases = currentCustomer.getTotalPurchases() + totalDue;
        currentCustomer.setTotalPurchases(lifePurchases);
        values.put(DBHelper.CUSTOMER_TOTAL_PURCHASES, lifePurchases);

        boolean creditExpDateSet = setCreditExpirationDate();
        if (!creditExpDateSet) {
            showFailureMessage("Failed to set Credit Expiration Date");
            return false;
        }

        String strNextYearDate = getStrCreditExpirationDate();
        if (strNextYearDate == null || strNextYearDate.isEmpty()) {
            showFailureMessage("Failed to get Credit Exipration Date");
            return false;
        }

        // Insert into database
        Transaction transaction = new Transaction(1, currentCustomer.getID(), subtotal, currentCustomer.getGoldStatus(), creditApplied, dCurrentDate);
        dbu.insertTransaction(contentResolver, transaction);
        values.put(DBHelper.CUSTOMER_CREDIT, currentCustomer.getCredit());
        values.put(DBHelper.CUSTOMER_CREDIT_EXPIRATION, strNextYearDate);

        // Send email and update gold status if earned, and they don't already have it
        if (lifePurchases >= 50000 && !(currentCustomer.getGoldStatus()))
        {
            currentCustomer.setGoldStatus(true);            // set gold status for current customer
            values.put(DBHelper.CUSTOMER_GOLD_STATUS, 1);   // set gold status value for db update
            sendEmail(2);                                   // send gold status e-mail
        }

        dbu.updateCustomer(contentResolver, currentCustomer.getID(), values);

        return true;
    }

    /**
     * Get the Credit Expiration Date (year from today)
     * @return
     */
    private String getStrCreditExpirationDate()
    {
        String strNextYearDate;
        try
        {
            String yr = "";
            String mo = "";
            String dy = "";

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date tempCurrentDate = new Date();
            String strCurrentDate = dateFormat.format(tempCurrentDate);

            // Get current date's day and month
            mo = Character.toString(strCurrentDate.charAt(5)) + Character.toString(strCurrentDate.charAt(6));
            dy = Character.toString(strCurrentDate.charAt(8)) + Character.toString(strCurrentDate.charAt(9));
            yr = Character.toString(strCurrentDate.charAt(0)) + Character.toString(strCurrentDate.charAt(1)) +
                    Character.toString(strCurrentDate.charAt(2)) + Character.toString(strCurrentDate.charAt(3));

            int iYr = Integer.parseInt(yr); //convert char string to int so we can update it
            iYr += 1;   // add a year
            yr = Integer.toString(iYr); // update year string with next year's value

            // update the credit and credit expiration date for next year
            strNextYearDate =  yr + "-" + mo + "-" + dy;
        }
        catch (Exception e)
        {
            showFailureMessage("Failed to get Credit Expiration Date");
            return null;
        }

        return strNextYearDate;
    }

    /**
     * Set the Credit Expiration Date
     * @return
     */
    private boolean setCreditExpirationDate()
    {
        try {
            // Set the new credit
            currentCustomer.setCredit(remainingCredit + earnedCredit);

            String yr = "";
            String mo = "";
            String dy = "";

            DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
            Date tempCurrentDate = new Date();
            String strCurrentDate = dateFormat.format(tempCurrentDate);

            // Get current date's day and month
            mo = Character.toString(strCurrentDate.charAt(0)) + Character.toString(strCurrentDate.charAt(1));
            dy = Character.toString(strCurrentDate.charAt(2)) + Character.toString(strCurrentDate.charAt(3));
            yr = Character.toString(strCurrentDate.charAt(4)) + Character.toString(strCurrentDate.charAt(5)) +
                    Character.toString(strCurrentDate.charAt(6)) + Character.toString(strCurrentDate.charAt(7));

            int iYr = Integer.parseInt(yr); //convert char string to int so we can update it
            iYr += 1;   // add a year
            yr = Integer.toString(iYr); // update year string with next year's value

            // update the credit and credit expiration date for next year
            String creditExpDate = mo + "/" + dy + "/" + yr;    // MMddyyyy format
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date creditExpirationDate = sdf.parse(creditExpDate);
            currentCustomer.setCreditExpirationDate(creditExpirationDate);    //Need to update to next year

        } catch (Exception e) {
            showFailureMessage("Failed to process transaction. Please try again");
            return false;
        }

        return true;
    }

    /**
     * Update the last purchase date for the Customer
     * @return boolean: Returns boolean of whether the operation succeeded or not
     */
    public boolean setCustomerLastPurchaseDate()
    {
        // Reset total purchases on new year
        String strPurchExpDate = "01-01";
        Date dCurrentDate = null;
        Date dPurchExpDate = null;
        int iYear = 2016;
        try {
            dCurrentDate = new Date();  // Get the current date
            dCurrentDate.getTime();

            dPurchExpDate = new Date();
            dPurchExpDate.getTime();    // Get current year's date to get the next new year's date

            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy");
            String sYear = dFormat.format(dPurchExpDate);
            iYear = Integer.parseInt(sYear) + 1;
            strPurchExpDate = Integer.toString(iYear) + "-" + strPurchExpDate;

            DateFormat dPEDdf = new SimpleDateFormat("yyyy-MM-dd");
            dPurchExpDate = dPEDdf.parse(strPurchExpDate);
        } catch (Exception e) {
            showFailureMessage("Failed to process payment. Try again");
            return true;
        }

        return true;
    }

    /**
     * Return to the MainActivity if error occurs
     */
    private void returnOnError()
    {
        Utilities.showToast(getApplicationContext(), "Error occurred");

        // pass Customer object to MainActivity
        Intent intent = new Intent(Purchase.this.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("customer", currentCustomer);
        startActivityForResult(intent, 0);
        finish();
    }

    /**
     * Show a failure message dialog
     *
     * @param message: Message to display
     */
    public void showFailureMessage(String message) {
        // Show failure dialog if unsuccessful
        AlertDialog.Builder adBuilderFailureMessage = new AlertDialog.Builder(Purchase.this);
        adBuilderFailureMessage.setMessage(message);
        adBuilderFailureMessage.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        adFailMessage = adBuilderFailureMessage.create();
        adFailMessage.show();
    }

    /**
     * Show dialog for exiting the activity
     */
    public void showExitDialog() {
        // Display successful purchase dialog and return current Customer to MainActivity
        AlertDialog.Builder adBuilderExitMessage = new AlertDialog.Builder(Purchase.this);
        adBuilderExitMessage.setMessage("Purchase successful.");
        adBuilderExitMessage.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        // pass Customer object to MainActivity
                        Intent intent = new Intent(Purchase.this.getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("customer", currentCustomer);
                        startActivityForResult(intent, 0);
                        finish();
                    }
            });

        adExit = adBuilderExitMessage.create();
        adExit.show();
    }

    /**
     * Shows a successful email dialog message
     *
     * @param emailType: Type of email to send - credit or gold status
     */
    public void showSuccessfulEmail(int emailType) {
        successEmailDialog = null;
        successEmailDialog = SuccessfulEmailDialog.newInstance(emailType);
        successEmailDialog.setCancelable(true);
        successEmailDialog.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Shows an unsuccessful email dialog message
     *
     * @param emailType: Type of email to send - credit or gold status
     */
    public void showFailedEmail(int emailType) {
        failedEmailDialog = null;
        failedEmailDialog = UnsuccessfulEmailDialog.newInstance(emailType);
        failedEmailDialog.setCancelable(true);
        failedEmailDialog.show(getSupportFragmentManager(), "dialog");
    }


    /**
     * Update purchase money values
     *
     * @param itemCost: Cost of the item to add to the purchase
     */
    public boolean updateTotals(int itemCost) {

        if (currentCustomer == null)
            return false;

        int customerCredit = currentCustomer.getCredit();

        int tempValue = itemCost + subtotal;
        if (tempValue < 0)
            return false;

        subtotal += itemCost;

        // Apply the 5% discount
        if (currentCustomer.getGoldStatus())
            goldDiscount = (int) (subtotal * .05);
        else
            goldDiscount = 0;


        totalDue = subtotal - goldDiscount;

        // Determine to use or add credit
        if (totalDue >= customerCredit) {
            totalDue -= customerCredit;
            creditApplied = customerCredit;
            remainingCredit = 0;
        } else {
            creditApplied = totalDue;
            remainingCredit = customerCredit - totalDue;
            totalDue = 0;
        }

        // if customer spends $50 give $5 credit
        if (totalDue >= 50 * 100)
            earnedCredit = 500;

        return true;
    }

    private void updateTotalsUI()
    {
        // Update UI
        tvSubtotal.setText("Subtotal: $" + Utilities.centsToDollars(subtotal));
        tvCredit.setText("Used Credit: $" + Utilities.centsToDollars(creditApplied));
        tvAvCredit.setText("Available Credit: $" + Utilities.centsToDollars(remainingCredit));

        if (goldDiscount > 0)
            tvGoldDiscount.setText("Gold Discount: " + Utilities.centsToDollars(goldDiscount));
        else
            tvGoldDiscount.setText("Gold Discount: None");

        tvTotalDue.setText("TOTAL DUE: $" + Utilities.centsToDollars(totalDue));
    }

    /**
     * Reset member data
     */
    public void resetData() {
        cardSwiped = false;
        subtotal = 0;
        totalDue = 0;
        remainingCredit = 0;
        earnedCredit = 0;

        quantLarge = 0;
        quantSmall = 0;

        adFailMessage = null;
        adExit = null;

        goldDiscount = 0;

        currentCustomer = null;
        emailSent = false;
    }

}