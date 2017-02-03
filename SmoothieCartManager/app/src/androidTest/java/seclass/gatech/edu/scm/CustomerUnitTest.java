package seclass.gatech.edu.scm;

import android.test.suitebuilder.annotation.MediumTest;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomerUnitTest extends android.test.ActivityUnitTestCase<MainActivity> {
    private Customer customer;
    private Date date;

    public CustomerUnitTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        customer = new Customer();
    }

    @MediumTest
    public void testAddCreditPositive() {
        customer.setCredit(109);
        customer.addCredit(91);
        assertTrue(customer.getCredit() == 200);
    }

    @MediumTest
    public void testAddCreditPositive2() {
        Date date = new Date();
        date.getTime();
        Customer customer = new Customer("b6acb59441af4ea13129d8373df8145e", "Betty Monroe", "5 Embarcadero Center, San Francisco, CA 94111", "bettymonroe@yahoo.com", 500, date, true, 0);
        customer.addCredit(123);
        assertTrue(customer.getCredit() == 623);
    }

    @MediumTest
    public void testAddCreditNegative() {
        customer.setCredit(200);
        customer.addCredit(-91);
        assertTrue(customer.getCredit() == 109);
    }

    @MediumTest
    public void testAddCreditNegative2() {
        Date date = new Date();
        date.getTime();
        Customer customer = new Customer("b6acb59441af4ea13129d8373df8145e", "Betty Monroe", "5 Embarcadero Center, San Francisco, CA 94111", "bettymonroe@yahoo.com", 500, date, true, 0);
        customer.addCredit(-123);
        assertTrue(customer.getCredit() == 377);
    }

    @MediumTest
    public void testAddCreditNegativeResultLT0() {
        customer.setCredit(10);
        customer.addCredit(-15);
        assertTrue(customer.getCredit() == -5);
    }

    @MediumTest
    public void testSetGoldStatus() {
        Date date = new Date();
        date.getTime();
        Customer customer = new Customer("b6acb59441af4ea13129d8373df8145e", "Betty Monroe", "5 Embarcadero Center, San Francisco, CA 94111", "bettymonroe@yahoo.com", 500, date, false, 0);
        customer.setGoldStatus(true);
        assertTrue(customer.getGoldStatus());
    }

    @MediumTest
    public void testVoidExpiredCredit() {
        Customer customer = new Customer();
        customer.setCredit(5);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        date = cal.getTime();
        customer.setCreditExpirationDate(date);
        customer.VoidExpiredCredit();
        assertTrue(customer.getCredit() == 0);
    }

    @MediumTest
    public void testVoidExpiredCreditDOE() {
        Customer customer = new Customer();
        customer.setCredit(5);
        Calendar cal = Calendar.getInstance();
        date = cal.getTime();
        customer.setCreditExpirationDate(date);
        customer.VoidExpiredCredit();
        assertTrue(customer.getCredit() == 5);
    }

    @MediumTest
    public void testVoidExpiredCreditNotExpired() {
        Customer customer = new Customer();
        customer.setCredit(5);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        date = cal.getTime();
        customer.setCreditExpirationDate(date);
        customer.VoidExpiredCredit();
        assertTrue(customer.getCredit() == 5);
    }

    @Override
    protected void tearDown() throws Exception { //runs before each test
        super.tearDown();
    }
}
