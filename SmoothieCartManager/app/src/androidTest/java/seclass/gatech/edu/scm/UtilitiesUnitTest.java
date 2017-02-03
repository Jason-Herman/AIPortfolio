package seclass.gatech.edu.scm;

import android.test.suitebuilder.annotation.MediumTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilitiesUnitTest extends android.test.ActivityUnitTestCase<MainActivity> {


    public UtilitiesUnitTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
    }

    @MediumTest
    public void testValidateString() {
        assertTrue(!Utilities.validateString(null));
        assertTrue(!Utilities.validateString(""));
        assertTrue(Utilities.validateString("Test String"));
    }

    @MediumTest
    public void testValidateEmail() {
        assertTrue(!Utilities.validateEmail("testWithMultiple@@signs"));
        assertTrue(!Utilities.validateEmail(""));
        assertTrue(!Utilities.validateEmail(null));
        assertTrue(!Utilities.validateEmail("testWithoutAtSign"));
        assertTrue(Utilities.validateEmail("allowsnormal@email.formats"));
    }

    @MediumTest
    public void testValidateNoSQL() {
        assertTrue(!Utilities.validateNoSQL("Testdrop customerTest"));
        assertTrue(!Utilities.validateNoSQL("Testdrop transactionsTest"));
        assertTrue(!Utilities.validateNoSQL("TestdeleteTest"));
        assertTrue(!Utilities.validateNoSQL("TestTruncateTest"));
        assertTrue(!Utilities.validateNoSQL("Test'singlequotes'"));
        assertTrue(!Utilities.validateNoSQL("Test\"doublequotes\""));
        assertTrue(!Utilities.validateNoSQL("Test/forwardslash"));
        assertTrue(!Utilities.validateNoSQL("Test\\backslash"));
        assertTrue(Utilities.validateNoSQL("allowsnormal@email.formats"));
    }

    @MediumTest
    public void testCentsToDollarsEQ0() {
        String result = Utilities.centsToDollars(0);
        assertTrue(result.equals("0.00"));
    }

    @MediumTest
    public void testCentsToDollarsLT10GT0() {
        String result = Utilities.centsToDollars(8);
        assertTrue(result.equals("0.08"));
    }

    @MediumTest
    public void testCentsToDollarsGT10LT100() {
        String result = Utilities.centsToDollars(99);
        assertTrue(result.equals("0.99"));
    }

    @MediumTest
    public void testCentsToDollarsGT100LT110() {
        String result = Utilities.centsToDollars(101);
        assertTrue(result.equals("1.01"));
    }

    @MediumTest
    public void testCentsToDollarsGT110LT200() {
        String result = Utilities.centsToDollars(150);
        assertTrue(result.equals("1.50"));
    }

    @MediumTest
    public void testCentsToDollarsEQ200() {
        String result = Utilities.centsToDollars(200);
        assertTrue(result.equals("2.00"));
    }

    @MediumTest
    public void testCentsToDollarsEQ310() {
        String result = Utilities.centsToDollars(310);
        assertTrue(result.equals("3.10"));
    }

    @MediumTest
    public void testCheckNewYearTrue() {
        DateFormat dPEDdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dLastDate = null;
        try {
            dLastDate = dPEDdf.parse("2014-09-15");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean bNewYear = Utilities.checkNewYear(dLastDate, 0);
        assertTrue(bNewYear);
    }

    @MediumTest
    public void testCheckNewYearFalse() {
        Date dLastDate = new Date();  // Get the current date
        dLastDate.getTime();
        boolean bNewYear = Utilities.checkNewYear(dLastDate, 0);
        assertTrue(!(bNewYear));
    }
}
