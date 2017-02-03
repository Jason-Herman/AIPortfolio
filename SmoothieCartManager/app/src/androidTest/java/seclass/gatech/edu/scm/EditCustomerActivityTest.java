package seclass.gatech.edu.scm;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Jason on 11/11/2015.
 */
public class EditCustomerActivityTest extends ActivityInstrumentationTestCase2<EditCustomer> {
    DBUtilities dbu = new DBUtilities();
    private Context context;
    private EditCustomer act;


    public EditCustomerActivityTest() {super(EditCustomer.class); }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        act = getActivity();
    }

    public void testUpdateInfoValidatorNone(){
        String Name = "";
        String Email = "";
        String Address = "";
        String result = act.updateInfoValidator(Name, Email, Address);
        assertTrue(result.equals("none"));
    }

    public void testUpdateInfoValidatorInvalid(){
        String Name = "a'a";
        String Email = "";
        String Address = "";
        String result = act.updateInfoValidator(Name, Email, Address);
        assertTrue(result.equals("invalid"));
    }

    public void testUpdateInfoValidatorValid(){
        String Name = "Jason";
        String Email = "a@a.com";
        String Address = "123 Street";
        String result = act.updateInfoValidator(Name, Email, Address);
        assertTrue(result.equals("valid"));
    }

    public void testEditCustomerInfoReturn(){
        Context context = getInstrumentation().getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        Customer customer = act.editCustomerInfo("New Name", "", "", id);
        assertTrue(customer.getName().equals("New Name"));
    }

    public void testEditCustomerInfoDatabase(){
        Context context = getInstrumentation().getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = dbu.insertCustomer(contentResolver, "Test Name", "Test Address", "Test@test.com");
        String id = uri.getLastPathSegment().toString();
        act.editCustomerInfo("New Name", "", "", id);
        Customer customer = dbu.getCustomer(contentResolver, id);
        assertTrue(customer.getName().equals("New Name"));
    }

    @Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }

}
