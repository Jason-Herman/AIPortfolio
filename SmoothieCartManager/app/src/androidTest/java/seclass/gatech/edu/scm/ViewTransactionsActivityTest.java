package seclass.gatech.edu.scm;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

public class ViewTransactionsActivityTest extends ActivityInstrumentationTestCase2<ViewTransactions> {
    DBUtilities dbu = new DBUtilities();
    private Context context;
    private ViewTransactions vtAct;

    public ViewTransactionsActivityTest() {
        super(ViewTransactions.class);
    }

    @Override
    protected void setUp() throws Exception { //runs before each test
        super.setUp();
        vtAct = getActivity();
    }

    public void testGoldDiscount() {
        Customer testCustomer = new Customer();
        vtAct.setCustomer(testCustomer);
        assertTrue(vtAct.goldDiscount(10000) == 500);
    }

    @Override
    protected void tearDown() throws Exception { //runs after each test
        super.tearDown();
    }

}
