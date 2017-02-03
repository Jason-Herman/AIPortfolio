package seclass.gatech.edu.scm;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Jason on 11/11/2015.
 */
public class CustomerSearchActivityTest extends ActivityInstrumentationTestCase2<CustomerSearch> {
    DBUtilities dbu = new DBUtilities();
    private Context context;
    private CustomerSearch act;


    public CustomerSearchActivityTest() {super(CustomerSearch.class); }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        act = getActivity();
    }

    public void testSearchInfoValidatorNone(){
        String Name = "";
        String Email = "";
        String Address = "";
        String ID = "";
        String result = act.searchInfoValidator(Name, Email, Address, ID);
        assertTrue(result.equals("none"));
    }

    public void testSearchInfoValidatorInvalid(){
        String Name = "a'a";
        String Email = "";
        String Address = "";
        String ID = "";
        String result = act.searchInfoValidator(Name, Email, Address, ID);
        assertTrue(result.equals("invalid"));
    }

    public void testSearchInfoValidatorValid(){
        String Name = "Jason";
        String Email = "a@a.com";
        String Address = "123 Street";
        String ID = "123";
        String result = act.searchInfoValidator(Name, Email, Address, ID);
        assertTrue(result.equals("valid"));
    }

    public void testCustomerSearch(){
        Context context = getInstrumentation().getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        Customer[] customers = act.customerSearch("Test Name", "Test@test.com", "Test Address", id);
        assertTrue(customers[0].getName().equals("Test Name"));
    }

    public void testCustomerTrim(){
        Context context = getInstrumentation().getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        id = "  " + id + "  ";
        Customer[] customers = act.customerSearch("", "", "", id);
        assertTrue(customers[0].getName().equals("Test Name"));
    }

    public void testCustomerCase(){
        Context context = getInstrumentation().getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        id = id.toUpperCase();
        Customer[] customers = act.customerSearch("", "", "", id);
        assertTrue(customers[0].getName().equals("Test Name"));
    }

    @Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }

}