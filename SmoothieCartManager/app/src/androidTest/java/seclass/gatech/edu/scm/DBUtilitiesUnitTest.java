package seclass.gatech.edu.scm;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class DBUtilitiesUnitTest extends ActivityInstrumentationTestCase2<MainActivity> {
    DBUtilities dbu = new DBUtilities();
    private Context context;
    private ContentResolver contentResolver;

    public DBUtilitiesUnitTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        context = getInstrumentation().getContext();
        contentResolver = context.getContentResolver();
    }

    @Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }

    /*
    getCustomer tests.
     */
    public void testGetCustomer() {
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        Customer customer = dbu.getCustomer(contentResolver,id);
        assertTrue(customer.getName().equals("Test Name") && customer.getEmailAddress().equals("Test@test.com") && customer.getBillingAddress().equals("Test Address"));
    }

    public void testGetCustomerInvalidID() {
        Customer customer = dbu.getCustomer(contentResolver, "Non-Existent ID");
        assertTrue(customer == null);
    }

    public void testGetCustomerNullID() {
        Customer customer = dbu.getCustomer(contentResolver, null);
        assertTrue(customer == null);
    }


    /*
    getCustomers tests.
     */
    public void testGetCustomers(){
        Uri uri1 = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id1 = uri1.getLastPathSegment().toString();
        String selection = DBHelper.CUSTOMER_NAME + " = 'Test Name' AND " + DBHelper.CUSTOMER_EMAIL_ADDRESS + " = 'Test@test.com' AND " + DBHelper.CUSTOMER_BILLING_ADDRESS + " = 'Test Address' AND " + DBHelper.CUSTOMER_ID + " = '" + id1 + "'";
        Customer[] customers = dbu.getCustomers(contentResolver, selection);
        boolean bID = customers[0].getID().equals(id1);
        assertTrue(bID);
    }

    public void testGetCustomersMultiple(){
        String randomName = UUID.randomUUID().toString();
        dbu.insertCustomer(contentResolver, randomName, "Test Address 1", "Test1@test.com");
        dbu.insertCustomer(contentResolver, randomName, "Test Address 2", "Test2@test.com");
        String selection = DBHelper.CUSTOMER_NAME + " = '" + randomName + "'";
        Customer[] customers = dbu.getCustomers(contentResolver, selection);
        boolean bNames = customers[0].getName().equals(customers[1].getName());
        assertTrue(bNames);
    }

    public void testGetCustomersNoMatch(){
        String selection = DBHelper.CUSTOMER_NAME + " = 'Non-existent'";
        Customer[] customers = dbu.getCustomers(contentResolver, selection);
        boolean bLength = customers.length==0;
        assertTrue(bLength);
    }

    public void testGetCustomersEmptySelection(){
        String selection = "";
        Customer[] customers = dbu.getCustomers(contentResolver, selection);
        Cursor c = contentResolver.query(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                DBHelper.ALL_COLUMNS_CUSTOMER, "", null, null);
        int count = 0;
        if (c != null) {
            count = c.getCount();
        }
        assertTrue(customers.length == count);
    }

    public void testGetCustomersNullSelection(){
        String selection = null;
        Customer[] customers = dbu.getCustomers(contentResolver, selection);
        Cursor c = contentResolver.query(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                DBHelper.ALL_COLUMNS_CUSTOMER, null, null, null);
        int count = 0;
        if (c != null) {
            count = c.getCount();
        }
        assertTrue(customers.length == count && count > 0);
    }

    /*
    getDateOfLastTransaction tests.
     */
    public void testGetDateOfLastTransaction(){
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String customerID = uri.getLastPathSegment().toString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date oldDate = null;
        try {
            oldDate = df.parse("2000-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate = null;
        try {
            newDate = df.parse("2001-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Transaction oldTransaction = new Transaction(customerID, 500, true, 300, oldDate);
        dbu.insertTransaction(contentResolver, oldTransaction);
        Transaction newTransaction = new Transaction(customerID, 500, true, 300, newDate);
        dbu.insertTransaction(contentResolver, newTransaction);
        assertTrue(dbu.getDateOfLastTransaction(contentResolver, customerID).equals(newDate));
    }

    public void testGetDateOfLastTransactionSameDate(){
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String customerID = uri.getLastPathSegment().toString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date oldDate = null;
        Date newDate = null;
        try {
            oldDate = df.parse("2001-01-01");
            newDate = df.parse("2001-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Transaction tran1 = new Transaction(customerID, 500, true, 300, oldDate);
        dbu.insertTransaction(contentResolver, tran1);
        Transaction tran2 = new Transaction(customerID, 500, true, 300, newDate);
        dbu.insertTransaction(contentResolver, tran2);
        assertTrue(dbu.getDateOfLastTransaction(contentResolver, customerID).equals(newDate));
    }

    public void testGetDateOfLastTransactionNoTrans(){
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String customerID = uri.getLastPathSegment().toString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        assertTrue(dbu.getDateOfLastTransaction(contentResolver, customerID) == null);
    }

    public void testGetDateOfLastTransactionInvalidCustomerID() {
        assertTrue(dbu.getDateOfLastTransaction(contentResolver, "Invalid ID") == null);
    }


    /*
    insertCustomer (without ID) tests.
     */
    public void testInsertCustomer() {
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        Customer customer = dbu.getCustomer(contentResolver, id);
        assertTrue(customer.getName().equals("Test Name") && customer.getEmailAddress().equals("Test@test.com") && customer.getBillingAddress().equals("Test Address"));
    }

    public void testInsertCustomerDuplicate(){
        Uri uri1 = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id1 = uri1.getLastPathSegment().toString();
        Customer customer1 = dbu.getCustomer(contentResolver, id1);
        Uri uri2 = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id2 = uri2.getLastPathSegment().toString();
        Customer customer2 = dbu.getCustomer(contentResolver,id2);
        boolean bName = customer1.getName().equals(customer2.getName());
        boolean bAddress = customer1.getBillingAddress().equals(customer2.getBillingAddress());
        boolean bEmail = customer1.getEmailAddress().equals(customer2.getEmailAddress());
        assertTrue(bName && bAddress && bEmail && !customer1.getID().equals(customer2.getID()));
    }

    public void testInsertCustomerNullValues() {
        Uri uri = dbu.insertCustomer(contentResolver, null, "Test Address", null);
        String id = uri.getLastPathSegment().toString();
        Customer customer = dbu.getCustomer(contentResolver, id);
        assertTrue(customer.getName() == null &&
                customer.getEmailAddress() == null &&
                customer.getBillingAddress().equals("Test Address"));
    }

    public void testInsertCustomerEmptyStrings() {
        Uri uri = dbu.insertCustomer(contentResolver, "", "Test Address", "");
        String id = uri.getLastPathSegment().toString();
        Customer customer = dbu.getCustomer(contentResolver,id);
        assertTrue(customer.getName().equals("") &&
                customer.getEmailAddress().equals("") &&
                customer.getBillingAddress().equals("Test Address"));
    }


    /*
    insertCustomer (with ID) tests.
    */
    public void testInsertCustomerID() {
        String randomName = UUID.randomUUID().toString();
        Uri uri = dbu.insertCustomer(contentResolver, randomName, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        Customer customer = dbu.getCustomer(contentResolver, id);
        assertTrue(customer.getName().equals("Test Name") &&
                customer.getEmailAddress().equals("Test@test.com") && customer.getBillingAddress().equals("Test Address"));
    }

    public void testInsertCustomerIDDuplicate(){
        String randomName = UUID.randomUUID().toString();
        dbu.insertCustomer(contentResolver, randomName, "Test Name", "Test Address", "Test@test.com");
        Uri uri = dbu.insertCustomer(contentResolver, randomName, "Test Name", "Test Address", "Test@test.com");
        assertTrue(uri == null);
    }

    public void testInsertCustomerIDNullValues() {
        String randomName = UUID.randomUUID().toString();
        Uri uri = dbu.insertCustomer(contentResolver, randomName, null, "Test Address", null);
        String id = uri.getLastPathSegment().toString();
        Customer customer = dbu.getCustomer(contentResolver, id);
        assertTrue(customer.getName() == null && customer.getEmailAddress() == null &&
                customer.getBillingAddress().equals("Test Address") && customer.getID().equals(randomName));
    }

    public void testInsertCustomerIDEmptyStrings() {
        String randomName = UUID.randomUUID().toString();
        Uri uri = dbu.insertCustomer(contentResolver, randomName, "", "Test Address", "");
        String id = uri.getLastPathSegment().toString();
        Customer customer = dbu.getCustomer(contentResolver, id);
        assertTrue(customer.getName().equals("") && customer.getEmailAddress().equals("") &&
                customer.getBillingAddress().equals("Test Address") && customer.getID().equals(randomName));
    }


    /*
    insertTransaction Tests.
     */
    public void testInsertTransaction() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String randomName = UUID.randomUUID().toString();
        Transaction tran = new Transaction(randomName, 100, true, 50, date);
        Uri returnUri = dbu.insertTransaction(contentResolver, tran);
        int id = Integer.parseInt(returnUri.getLastPathSegment());
        Transaction retTran = dbu.getTransaction(contentResolver, Integer.parseInt(returnUri.getLastPathSegment()));
        assertTrue(retTran.ID == id && retTran.customerID.equals(randomName) && retTran.preDiscountAmount == 100 &&
                retTran.goldStatus == true && retTran.creditsApplied == 50 && df.format(retTran.date).equals(df.format(date)));
    }

    public void testInsertTransactionNullValues() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Transaction tran = new Transaction(null, 100, true, 50, date);
        Uri returnUri = dbu.insertTransaction(contentResolver, tran);
        int id = Integer.parseInt(returnUri.getLastPathSegment());
        Transaction retTran = dbu.getTransaction(contentResolver, Integer.parseInt(returnUri.getLastPathSegment()));
        assertTrue(retTran.ID == id && retTran.customerID == null && retTran.preDiscountAmount == 100 &&
                retTran.goldStatus == true && retTran.creditsApplied == 50 && df.format(retTran.date).equals(df.format(date)));
    }

    public void testInsertTransactionNullTransaction() {
        Uri returnUri = dbu.insertTransaction(contentResolver, null);
        assertTrue(returnUri == null);
    }

    public void testInsertTransactionDateFormat() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String randomName = UUID.randomUUID().toString();
        Transaction tran = new Transaction(randomName, 100, true, 50, date);
        Uri returnUri = dbu.insertTransaction(contentResolver, tran);
        int id = Integer.parseInt(returnUri.getLastPathSegment());
        Transaction retTran = dbu.getTransaction(contentResolver, Integer.parseInt(returnUri.getLastPathSegment()));
        assertTrue(retTran.ID == id && retTran.customerID.equals(randomName) && retTran.preDiscountAmount == 100 &&
                retTran.goldStatus == true && retTran.creditsApplied == 50 &&
                df.format(retTran.date).equals(df.format(date)) && retTran.date.compareTo(date) != 0);
    }

    public void testInsertTransactionWithID() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = df.parse("2001-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Transaction transaction = new Transaction("b6acb59441af4ea13129d8373df8145e", 500, true, 300, newDate);
        Uri uri = dbu.insertTransaction(contentResolver, transaction);
        String idStr = uri.getLastPathSegment().toString();
        int id = Integer.parseInt(idStr);
        transaction = dbu.getTransaction(contentResolver, id);
        boolean bCustomerID = transaction.customerID.equals("b6acb59441af4ea13129d8373df8145e");
        boolean bPreAmount = transaction.preDiscountAmount == 500;
        boolean bGold = transaction.goldStatus;
        boolean bCredit = transaction.creditsApplied == 300;
        boolean bDate = transaction.date.equals(newDate);
        assertTrue(bCustomerID && bPreAmount && bGold && bCredit && bDate);
    }


    /*
    updateCustomer Tests.
     */
    public void testUpdateCustomer() {
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CUSTOMER_NAME, "Edit Name");
        values.put(DBHelper.CUSTOMER_EMAIL_ADDRESS, "Edit@edit.com");
        values.put(DBHelper.CUSTOMER_BILLING_ADDRESS, "Edit Address");
        values.put(DBHelper.CUSTOMER_GOLD_STATUS, "1");
        values.put(DBHelper.CUSTOMER_CREDIT, "500");
        values.put(DBHelper.CUSTOMER_CREDIT_EXPIRATION, "2000-01-01");
        values.put(DBHelper.CUSTOMER_TOTAL_PURCHASES, "50000");
        dbu.updateCustomer(contentResolver, id, values);
        Customer customer = dbu.getCustomer(contentResolver, id);
        boolean bName = customer.getName().equals("Edit Name");
        boolean bEmail = customer.getEmailAddress().equals("Edit@edit.com");
        boolean bAddress = customer.getBillingAddress().equals("Edit Address");
        boolean bGold = customer.getGoldStatus();
        int credit = customer.getCredit();
        Date date = customer.getCreditExpirationDate();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        boolean bDate = df.format(date).equals("2000-01-01");
        long totalPurchases = customer.getTotalPurchases();

        assertTrue(bName && bEmail && bAddress && bGold && credit == 500 && bDate && totalPurchases == 50000);

    }

    public void testUpdateCustomerPartial() {
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CUSTOMER_NAME, "Edit Name");
        values.put(DBHelper.CUSTOMER_EMAIL_ADDRESS, "Edit@edit.com");
        dbu.updateCustomer(contentResolver, id, values);
        Customer customer = dbu.getCustomer(contentResolver, id);
        boolean bName = customer.getName().equals("Edit Name");
        boolean bEmail = customer.getEmailAddress().equals("Edit@edit.com");
        boolean bAddress = customer.getBillingAddress().equals("Test Address");
        boolean bGold = customer.getGoldStatus();
        int credit = customer.getCredit();
        Date date = customer.getCreditExpirationDate();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        boolean bDate = df.format(date).equals("1900-01-01");
        long totalPurchases = customer.getTotalPurchases();

        assertTrue(bName && bEmail && bAddress && !bGold && credit == 0 && bDate && totalPurchases == 0);
    }

    public void testUpdateCustomerEmptyValues() {
        try {
            Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
            String id = uri.getLastPathSegment().toString();
            ContentValues values = new ContentValues();
            dbu.updateCustomer(contentResolver, id, values);
            Customer customer = dbu.getCustomer(contentResolver, id);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testUpdateCustomerNonExistentCustomerID() {
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        ContentValues values = new ContentValues();
        values.put(DBHelper.CUSTOMER_NAME, "Edit Name");
        values.put(DBHelper.CUSTOMER_EMAIL_ADDRESS, "Edit@edit.com");
        dbu.updateCustomer(contentResolver, "Fake ID", values);
        Customer customer = dbu.getCustomer(contentResolver, id);
        assertTrue(customer.getName().equals("Test Name") && customer.getEmailAddress().equals("Test@test.com") &&
            customer.getBillingAddress().equals("Test Address"));
    }

}
