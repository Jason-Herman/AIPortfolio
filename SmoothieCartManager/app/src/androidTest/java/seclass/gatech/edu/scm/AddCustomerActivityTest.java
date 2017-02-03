package seclass.gatech.edu.scm;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class AddCustomerActivityTest extends ActivityInstrumentationTestCase2<AddCustomer> {
    DBUtilities dbu = new DBUtilities();
    private Context context;
    private AddCustomer act;

    public AddCustomerActivityTest() {
        super(AddCustomer.class);
    }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        act = getActivity();
    }

    public void testAddCustomer() {
        context = getInstrumentation().getContext();
        Customer customer1 = act.addCustomer("Shan Gupta", "2731 Lake Shore Harbour Dr.", "shanrgupta@yahoo.com");
        Customer customer2 = dbu.getCustomer(context.getContentResolver(), customer1.getID());
        assertTrue(customer1.getID().equals(customer2.getID()));
        assertTrue(customer2.getName().equals("Shan Gupta"));
        assertTrue(customer2.getBillingAddress().equals("2731 Lake Shore Harbour Dr."));
        assertTrue(customer2.getEmailAddress().equals("shanrgupta@yahoo.com"));
    }

    public void testAddCustomerIDLength() {
        context = getInstrumentation().getContext();
        Customer customer = act.addCustomer("Shan Gupta", "2731 Lake Shore Harbour Dr.", "shanrgupta@yahoo.com");
        Customer retCustomer = dbu.getCustomer(context.getContentResolver(), customer.getID());
        assertTrue(retCustomer.getID().length() == 32);
    }

    public void testAddCustomerIDisHex() {
        boolean result = true;
        context = getInstrumentation().getContext();
        Customer customer = act.addCustomer("Shan Gupta", "2731 Lake Shore Harbour Dr.", "shanrgupta@yahoo.com");
        try {
            BigInteger bi = new BigInteger(customer.getID(), 16);
        } catch (NumberFormatException e) {
            result = false;
        }

        assertTrue(result);
    }

    public void testAdd10000Customers() {
        List<Customer> list = new LinkedList<Customer>();
        context = getInstrumentation().getContext();
        for (int i = 0; i < 10000; i++) {
            Customer customer = act.addCustomer("Shan Gupta", "2731 Lake Shore Harbour Dr.", "shanrgupta@yahoo.com");
            list.add(customer);
        }
        assertTrue(list.size() == 10000);
    }

    @Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }
}
