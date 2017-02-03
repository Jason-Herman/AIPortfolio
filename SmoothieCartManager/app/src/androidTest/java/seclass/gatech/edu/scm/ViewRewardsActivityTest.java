package seclass.gatech.edu.scm;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ViewRewardsActivityTest extends ActivityInstrumentationTestCase2<ViewRewards> {
    DBUtilities dbu = new DBUtilities();

    private Context context;
    private ViewRewards vrAct;

    public ViewRewardsActivityTest() {
        super(ViewRewards.class);
    }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        vrAct = getActivity();
    }

    public void testUpdateCustomerFromDatabase() {

        //Create customer
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(0);
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("b53b7c86ffeeaddbbe352f1f4dcd8e1a");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = "01-01-1999";
        try {
            Date dateP = sdf.parse(date);
            testCustomer.setCreditExpirationDate(dateP);
        } catch(java.text.ParseException e) {
            e.printStackTrace();
        }

        vrAct.setCustomer(testCustomer);

        //Set a customer in db
        vrAct.addCustomer();

        //Update customer with info from db
        testCustomer = vrAct.updateCustomerFromDatabase(testCustomer);

        //Assert
        assertTrue(testCustomer.getGoldStatus() == true);
        assertTrue(testCustomer.getCredit() == 500);
        assertTrue(testCustomer.getTotalPurchases() == 4500);

        String dateUpdated = "01-01-2016";
        try {
            Date dateU = sdf.parse(dateUpdated);
            assertTrue(testCustomer.getCreditExpirationDate().equals(dateU));
        } catch(java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public void testGetCreditExpDate() {
        Customer testCustomer = new Customer();
        vrAct.setCustomer(testCustomer);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String datePurchase = "12-10-1999";
        try {
            Date dateP = sdf.parse(datePurchase);
            String creditExpDate = vrAct.getCreditExpDate(dateP);
            assertTrue(creditExpDate.equals("10/12/1999"));
        } catch(java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public void testGetGoldStatusAccExpDate() {
        Customer testCustomer = new Customer();
        vrAct.setCustomer(testCustomer);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String datePurchase = "12-10-1999";
        try {
            Date dateP = sdf.parse(datePurchase);
            String goldStatusAccExpDate = vrAct.getGoldStatusAccExpDate(dateP);
            assertTrue(goldStatusAccExpDate.equals("01/01/2000"));
        } catch(java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public void testGetPurchasesNeeded() {
        context = getInstrumentation().getContext();
        Customer testCustomer = new Customer();
        vrAct.setCustomer(testCustomer);

        double d1 = 32540;
        String t1 = vrAct.getPurchasesNeeded((long) d1);
        assertTrue(t1.equals("174.60"));
    }

    @Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }
}
