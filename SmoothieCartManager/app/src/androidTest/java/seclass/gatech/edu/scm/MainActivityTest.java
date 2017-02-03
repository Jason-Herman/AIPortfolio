package seclass.gatech.edu.scm;

import android.content.ContentResolver;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Jason on 11/11/2015.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    DBUtilities dbu = new DBUtilities();
    private Context context;
    private MainActivity act;


    public MainActivityTest() {super(MainActivity.class); }

    public void testScanCustomer(){
        Context context = getInstrumentation().getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
        Customer customer = act.scanCustomer();
        boolean bID = false;
        if (customer == null) {
            bID = true;
        } else {
            String id = customer.getID();
            if (id.equals("b53b7c86ffeeaddbbe352f1f4dcd8e1a") || id.equals("b6acb59441af4ea13129d8373df8145e") || id.equals("f184cd0f0e056d4c58c4b0264e5a6bcc")) {
                bID = true;
            }
        }
        assertTrue(bID);
    }

    public void testInsertBaseCustomers(){
        Context context = getInstrumentation().getTargetContext();
        ContentResolver contentResolver = context.getContentResolver();
       act.insertBaseCustomers();
        Customer c1 = dbu.getCustomer(contentResolver, "b53b7c86ffeeaddbbe352f1f4dcd8e1a");
        Customer c2 = dbu.getCustomer(contentResolver, "b6acb59441af4ea13129d8373df8145e");
        Customer c3 = dbu.getCustomer(contentResolver, "f184cd0f0e056d4c58c4b0264e5a6bcc");
        assertTrue(c1 != null && c2 != null && c3 != null);
    }


    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        act = getActivity();
    }

    @Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }

}

