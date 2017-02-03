package seclass.gatech.edu.scm;

import java.util.Date;

public class Transaction {
    public int ID;
    public String customerID;
    public int preDiscountAmount;
    public boolean goldStatus;
    public int creditsApplied;
    public Date date;

    public Transaction () {
        ID = 0;
        customerID = "";
        preDiscountAmount = 0;
        goldStatus = false;
        creditsApplied = 0;
        date = new Date();
    }

    public Transaction (int pID, String pCustomerID, int pPreDiscountAmount, boolean pGoldStatus,
                        int pCreditsApplied, Date pDate) {
        ID = pID;
        customerID = pCustomerID;
        preDiscountAmount = pPreDiscountAmount;
        goldStatus = pGoldStatus;
        creditsApplied = pCreditsApplied;
        date = pDate;
    }

    public Transaction (String pCustomerID, int pPreDiscountAmount, boolean pGoldStatus,
                        int pCreditsApplied, Date pDate) {
        customerID = pCustomerID;
        preDiscountAmount = pPreDiscountAmount;
        goldStatus = pGoldStatus;
        creditsApplied = pCreditsApplied;
        date = pDate;
    }
}
