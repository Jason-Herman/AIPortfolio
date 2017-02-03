package seclass.gatech.edu.scm;

import android.content.ContentResolver;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class PurchaseActivityTest extends ActivityInstrumentationTestCase2<Purchase> {
    DBUtilities dbu = new DBUtilities();
    private Context context;
    private Purchase pAct;

    public PurchaseActivityTest() {
        super(Purchase.class);
    }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        pAct = getActivity();
    }

	@Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }

    @UiThreadTest
	public void testAddSmall()
	{
		context = getInstrumentation().getContext();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(0);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);

		//context.getApplicationContext();
        pAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pAct.addSmallItem();
            }
        });

        int total = pAct.getTotal();
        int smallAmount = pAct.smallSmoothieCost;
        assertTrue("testAddSmall", (total == smallAmount));
	}

    @UiThreadTest
    public void testAddLarge()
    {
        context = getInstrumentation().getContext();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(0);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);

        //context.getApplicationContext();
        pAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pAct.addSmallItem();
            }
        });

        int total = pAct.getTotal();
        int smallAmount = pAct.smallSmoothieCost;
        assertTrue("testAddLarge", (total == smallAmount));
    }

    public void testSwipeCardError()
    {
        pAct.resetData();
        String card = "ERR";
        while ((card = pAct.swipeCreditCard()) != "ERR") { }

        assertTrue("testSwipeCardError", (card == "ERR"));
    }

    public void testSwipeCardSuccessNoCardSwipe()
    {
        pAct.resetData();
        String ccInfo = "Mark#Arita#3113765838214281#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);
        assertTrue("testSwipeCardSuccessNoCardSwipe", (cInfo == null));
    }

    public void testSwipeCardSuccessCardSwipe()
    {
        pAct.resetData();
        String card = "ERR";
        while ((card = pAct.swipeCreditCard()) == "ERR") { }

        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(card);
        assertTrue("testSwipeCardSuccessCardSwipe", (cInfo != null));
    }

    public void testRetrieveCreditCardInfo_NullInfo()
    {
        pAct.resetData();
        String card = "ERR";
        while ((card = pAct.swipeCreditCard()) == "ERR") { }

        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(null);
        assertTrue("testRetrieveCreditCardInfo_NullInfo", (cInfo == null));
    }

    public void testRetrieveCreditCardInfo_NoInfo()
    {
        pAct.resetData();
        String card = "ERR";
        while ((card = pAct.swipeCreditCard()) == "ERR") { }

        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo("");
        assertTrue("testRetrieveCreditCardInfo_NoInfo", (cInfo == null));
    }

    public void testRetrieveCreditCardInfo_MissingInfo_SecurityCode()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#3113765838214281#12312015";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        assertTrue("testRetrieveCreditCardInfo_MissingInfo_SecurityCode", (cInfo == null));
    }

    public void testRetrieveCreditCardInfo_MissingInfo_CCExpDate()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#3113765838214281#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        assertTrue("testRetrieveCreditCardInfo_MissingInfo_CCExpDate", (cInfo == null));
    }

    public void testRetrieveCreditCardInfo_MissingInfo_CardNumber ()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        assertTrue("testRetrieveCreditCardInfo_MissingInfo_CardNumber", (cInfo == null));
    }

    public void testRetrieveCreditCardInfo_MissingInfo_LastName()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#3113765838214281#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        assertTrue("testRetrieveCreditCardInfo_MissingInfo_LastName", (cInfo == null));
    }

    public void testRetrieveCreditCardInfo_MissingInfo_FirstName()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Arita#3113765838214281#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        assertTrue("testRetrieveCreditCardInfo_MissingInfo_FirstName", (cInfo == null));
    }

    public void testRetrieveCreditCardInfo_Success_FirstName()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#3113765838214281#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        String firstName = (String)(cInfo.get("firstName"));
        assertTrue("testRetrieveCreditCardInfo_Success_FirstName", (firstName.equals("Mark")));
    }

    public void testRetrieveCreditCardInfo_Success_LastName()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#3113765838214281#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        String lastName = (String)(cInfo.get("lastName"));
        assertTrue("testRetrieveCreditCardInfo_Success_LastName", (lastName.equals("Arita")));
    }

    public void testRetrieveCreditCardInfo_Success_CardNumber()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#3113765838214281#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        String cardNumber = (String)(cInfo.get("cardNumber"));
        assertTrue("testRetrieveCreditCardInfo_Success_CardNumber", (cardNumber.equals("3113765838214281")));
    }

    public void testRetrieveCreditCardInfo_Success_CardExpiration()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#3113765838214281#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        String expDate = (String)(cInfo.get("expDate"));
        assertTrue("testRetrieveCreditCardInfo_Success_CardExpiration", (expDate.equals("12312015")));
    }

    public void testRetrieveCreditCardInfo_Success_CardSecurityCode()
    {
        pAct.resetData();
        while (pAct.swipeCreditCard() == "ERR") { }

        String ccInfo = "Mark#Arita#3113765838214281#12312015#123";
        Hashtable cInfo = pAct.retrieveCustomerCreditCardInfo(ccInfo);

        String secCode = (String)(cInfo.get("secCode"));
        assertTrue("testRetrieveCreditCardInfo_Success_CardSecurityCode", (secCode.equals("123")));
    }

    public void testGetCreditCardExpirationDate_null()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate(null);
        assertTrue("testGetCreditCardExpirationDate_null", (ccExp == null));
    }

    public void testGetCreditCardExpirationDate_empty()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate(null);
        assertTrue("testGetCreditCardExpirationDate_empty", (ccExp == null));
    }

    public void testGetCreditCardExpirationDate_Success()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12312015");
        assertTrue("testGetCreditCardExpirationDate_Success", (ccExp != null));
    }

    public void testCreditCardExpirationDate_null()
    {
        pAct.resetData();
        boolean expired = pAct.checkCreditCardExpired(null);
        assertTrue("testCreditCardExpirationDate_null", (expired == true));
    }

    public void testCreditCardExpirationDate_empty()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("");
        boolean expired = pAct.checkCreditCardExpired(ccExp);
        assertTrue("testCreditCardExpirationDate_empty", (expired == true));
    }

    public void testCreditCardExpirationDate_old()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12011999");
        boolean expired = pAct.checkCreditCardExpired(ccExp);
        assertTrue("testCreditCardExpirationDate_old", (expired == true));
    }

    public void testCreditCardExpirationDate_valid()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean expired = pAct.checkCreditCardExpired(ccExp);
        assertTrue("testCreditCardExpirationDate_valid", (expired == false));
    }

    public void testProcessTransaction_nullFirstName()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction(null, "Arita", "3113765838214281", ccExp, "123", 500);
        assertTrue("testProcessTransaction_nullFirstName", (success == false));
    }

    public void testProcessTransaction_nullLastName()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", null, "3113765838214281", ccExp, "123", 500);
        assertTrue("testProcessTransaction_nullLastName", (success == false));
    }

    public void testProcessTransaction_nullCreditCard()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "Arita", null, ccExp, "123", 500);
        assertTrue("testProcessTransaction_nullCreditCard", (success == false));
    }

    public void testProcessTransaction_nullCreditCardExpiration()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "Arita", "3113765838214281", null, "123", 500);
        assertTrue("testProcessTransaction_nullCreditCardExpiration", (success == false));
    }

    public void testProcessTransaction_nullSecurityCode()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "Arita", "3113765838214281", ccExp, null, 500);
        assertTrue("testProcessTransaction_nullSecurityCode", (success == false));
    }

    public void testProcessTransaction_emptyFirstName()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("", "Arita", "3113765838214281", ccExp, "123", 500);
        assertTrue("testProcessTransaction_emptyFirstName", (success == false));
    }

    public void testProcessTransaction_emptyLastName()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "", "3113765838214281", ccExp, "123", 500);
        assertTrue("testProcessTransaction_emptyLastName", (success == false));
    }

    public void testProcessTransaction_emptyCreditCard()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "Arita", "", ccExp, "123", 500);
        assertTrue("testProcessTransaction_emptyCreditCard", (success == false));
    }

    public void testProcessTransaction_emptyCreditCardExpiration()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("");
        boolean success = pAct.processTransaction("Mark", "Arita", "3113765838214281", ccExp, "123", 500);
        assertTrue("testProcessTransaction_emptyCreditCardExpiration", (success == false));
    }

    public void testProcessTransaction_emptySecurityCode()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "Arita", "3113765838214281", ccExp, "", 500);
        assertTrue("testProcessTransaction_emptySecurityCode", (success == false));
    }

    public void testProcessTransaction_negativeTotal()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "Arita", "3113765838214281", ccExp, "123", -100);
        assertTrue("testProcessTransaction_negativeTotal", (success == false));
    }

    public void testProcessTransaction_zeroTotal()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = pAct.processTransaction("Mark", "Arita", "3113765838214281", ccExp, "123", 0);
        assertTrue("testProcessTransaction_zeroTotal", (success == false));
    }

    public void testProcessTransaction_genericFailure()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = false;
        while ((success = pAct.processTransaction("Mark", "Arita", "3113765838214281", ccExp, "123", 500))) { }

        assertTrue("testProcessTransaction_genericFailure", (success == false));
    }

    public void testProcessTransaction_success()
    {
        pAct.resetData();
        Date ccExp = pAct.getCreditCardExpirationDate("12012050");
        boolean success = false;
        while (!(success = pAct.processTransaction("Mark", "Arita", "3113765838214281", ccExp, "123", 500))) { }

        assertTrue("testProcessTransaction_success", (success == true));
    }

    public void testFailureMessageShowing()
    {
        pAct.resetData();
        pAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pAct.showFailureMessage("Showing failure message");
            }
        });

        int count = 20;
        while (pAct.adFailMessage == null && count-- > 0) { try { Thread.sleep(500); } catch (Exception e) { } }
        boolean visible = false;
        if (pAct.adFailMessage != null)
            visible = pAct.adFailMessage.isShowing();

        assertTrue("testFailureMessageShowing", (visible == true));
    }

    public void testExitMessageShowing()
    {
        pAct.resetData();
        pAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pAct.showExitDialog();
            }
        });

        int count = 20;
        while (pAct.adExit == null && count-- > 0) { try { Thread.sleep(500); } catch (Exception e) { } }
        boolean visible = false;
        if (pAct.adExit != null)
            visible = pAct.adExit.isShowing();

        assertTrue("testExitMessageShowing", (visible == true));
    }

    public void testTotals_negativeValue()
    {
        pAct.resetData();
        pAct.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pAct.updateTotals(pAct.largeSmoothieCost);
            }
        });

        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_negativeValue total !equals subtotal", (total == subtotal));
        assertTrue("testTotals_negativeValue total == 0", (total == 0));
    }

    public void testTotals_addSmall_noDiscounts()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(0);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);
        pAct.updateTotals(pAct.smallSmoothieCost);
        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addSmall_noDiscounts", (total == subtotal));
    }

    public void testTotals_addLarge_noDiscounts()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(0);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);
        pAct.updateTotals(pAct.largeSmoothieCost);
        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addLarge_noDiscounts", (total == subtotal));
    }

    public void testTotals_addLarge_goldDiscount()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(true);
        testCustomer.setCredit(0);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);
        pAct.updateTotals(pAct.largeSmoothieCost);
        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addLarge_goldDiscount equals", (total != subtotal));
        assertTrue("", (subtotal == 399));
        assertTrue("", (total == 380));
    }

    public void testTotals_addSmall_goldDiscount()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(true);
        testCustomer.setCredit(0);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);
        pAct.updateTotals(pAct.smallSmoothieCost);
        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addSmall_goldDiscount", (total != subtotal));
        assertTrue("", (subtotal == 299));
        assertTrue("", (total == 285));
    }


    public void testTotals_addSmall_wFiveDollarsCredit()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);

        pAct.updateTotals(pAct.smallSmoothieCost);
        pAct.updateTotals(pAct.smallSmoothieCost);
        pAct.updateTotals(pAct.smallSmoothieCost);

        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addSmall_wFiveDollarsCredit", (total != subtotal));
        assertTrue("", (subtotal == 897));
        assertTrue("", (total == 397));
    }

    public void testTotals_addLarge_wFiveDollarsCredit()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);

        pAct.updateTotals(pAct.largeSmoothieCost);
        pAct.updateTotals(pAct.largeSmoothieCost);
        pAct.updateTotals(pAct.largeSmoothieCost);

        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addLarge_wFiveDollarsCredit", (total != subtotal));
        assertTrue("", (subtotal == 1197));
        assertTrue("", (total == 697));
    }

    public void testTotals_addSmall_wCreditAndGoldDiscount()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(true);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);

        pAct.updateTotals(pAct.smallSmoothieCost);
        pAct.updateTotals(pAct.smallSmoothieCost);
        pAct.updateTotals(pAct.smallSmoothieCost);

        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addSmall_wFiveDollarsCredit", (total != subtotal));
        assertTrue("", (subtotal == 897));
        assertTrue("", (total == 353));
    }

    public void testTotals_addLarge_wCreditAndGoldDiscount()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(true);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(0);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");
        pAct.setCustomer(testCustomer);

        pAct.updateTotals(pAct.largeSmoothieCost);
        pAct.updateTotals(pAct.largeSmoothieCost);
        pAct.updateTotals(pAct.largeSmoothieCost);

        int total = pAct.getTotal();
        int subtotal = pAct.getSubtotal();
        assertTrue("testTotals_addLarge_wFiveDollarsCredit", (total != subtotal));
        assertTrue("", (subtotal == 1197));
        assertTrue("", (total == 638));
    }

    public void testCreditExpirationDatePast()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(true);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(5000);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");

        Date oldExpDate = pAct.getCreditCardExpirationDate("01012000");
        testCustomer.setCreditExpirationDate(oldExpDate);
        testCustomer.VoidExpiredCredit();

        pAct.setCustomer(testCustomer);

        // create transaction less than $50
        pAct.updateTotals(pAct.largeSmoothieCost);
        pAct.swipeCreditCard();
        pAct.processPurchase();

        int credit = testCustomer.getCredit();

        assertTrue("testCreditExpirationDatePast", (credit == 0));
    }

    public void testGoldEmailAlreadyGold()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(true);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(5000);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");

        Date oldExpDate = pAct.getCreditCardExpirationDate("01012020");
        testCustomer.setCreditExpirationDate(oldExpDate);
        testCustomer.VoidExpiredCredit();

        pAct.setCustomer(testCustomer);

        // create transaction for >= $50
        pAct.updateTotals(pAct.largeSmoothieCost * 40);
        pAct.swipeCreditCard();
        pAct.processPurchase();

        boolean emailSent = pAct.getEmailSent();

        assertTrue("testCreditExpirationDatePast", (!emailSent));
    }

	public void testPurchases_NewYear()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(false);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(50000);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");

        Date oldExpDate = pAct.getCreditCardExpirationDate("01012020");
        testCustomer.setCreditExpirationDate(oldExpDate);
        testCustomer.VoidExpiredCredit();

        pAct.setCustomer(testCustomer);

        assertTrue("Total purchases == $5000", (testCustomer.getTotalPurchases() == 50000));

        ContentResolver contentResolver = null;
        if (contentResolver == null)
            contentResolver = pAct.getContentResolver();

		// void old purchases on new year
		Date lastDate = dbu.getDateOfLastTransaction(contentResolver, testCustomer.getID());
        if (Utilities.checkNewYear(lastDate, 2016))
            testCustomer.setTotalPurchases(0);

        long purchases = testCustomer.getTotalPurchases();

        assertTrue("testCreditExpirationDatePast", (purchases == 0));
    }
	
    public void testAddOldPurchase()
    {
        pAct.resetData();
        Customer testCustomer = new Customer();
        testCustomer.setGoldStatus(true);
        testCustomer.setCredit(500);
        testCustomer.setName("Mark Arita");
        testCustomer.setTotalPurchases(5000);
        testCustomer.setID("e242bc1f0e056d4c58c4b0264e5a6bcc");

        Date oldExpDate = pAct.getCreditCardExpirationDate("01012020");
        testCustomer.setCreditExpirationDate(oldExpDate);
        testCustomer.VoidExpiredCredit();
        pAct.setCustomer(testCustomer);

        // create transaction for >= $50
        pAct.updateTotals(pAct.largeSmoothieCost * 40);
        pAct.swipeCreditCard();

        Date date = Utilities.getDate("2000-10-30");
        pAct.updateDatabaseWithPurchase(date);

        //assertTrue("testCreditExpirationDatePast", (!emailSent));
    }
}
