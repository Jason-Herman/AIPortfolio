package seclass.gatech.edu.scm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author: Team 71
 */
public class Customer implements Parcelable
{
    private String name = "";
    private String billingAddress = "";
    private String emailAddress = "";
    private String customerID = "";

    private int credit = 0;
    private Date creditExpirationDate = null;
    private Boolean goldStatus = false;
    private long totalPurchases = 0;

    public static final Parcelable.Creator CREATOR =
    new Parcelable.Creator() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public Customer()
    {

    }

    public Customer(Parcel in) {
        readFromParcel(in);
    }

    // Secondary constructor for Customer
    public Customer(String dName, String dBillingAddress, String dEmailAddress)
    {
        name = dName;
        billingAddress = dBillingAddress;
        emailAddress = dEmailAddress;
    }

    public Customer (String pCustomerID, String pName, String pBillingAddress, String pEmailAddress,
                     int pCredit, Date pCreditExpirationDate, boolean pGoldStatus, long pTotalPurchases) {
        customerID = pCustomerID;
        name = pName;
        billingAddress = pBillingAddress;
        emailAddress = pEmailAddress;
        credit = pCredit;
        creditExpirationDate = pCreditExpirationDate;
        goldStatus = pGoldStatus;
        totalPurchases = pTotalPurchases;

        // Do we want to add in a calculation for total purchases and set that value as well?
    }

    public void CopyCustomer(Customer copyCustomer)
    {
        this.setName(copyCustomer.getName());
        this.setID(copyCustomer.getID());
        this.setBillingAddress(copyCustomer.getBillingAddress());
        this.setEmailAddress(copyCustomer.getEmailAddress());
        this.setGoldStatus(copyCustomer.getGoldStatus());
        this.setCreditExpirationDate(copyCustomer.getCreditExpirationDate());
        this.setTotalPurchases(copyCustomer.getTotalPurchases());
        this.setCredit(copyCustomer.getCredit());
    }

    // Getters
    public String getName(){ return name; }

    public String getBillingAddress(){ return billingAddress; }

    public String getEmailAddress(){ return emailAddress; }

    public String getID(){ return customerID; }

    public int getCredit(){ return credit; }

    public Date getCreditExpirationDate(){ return creditExpirationDate; }

    public Boolean getGoldStatus(){ return goldStatus; }

    public long getTotalPurchases(){ return totalPurchases; }

    // Setters
    public void setName(String nName){ name = nName; }

    public void setBillingAddress(String nBillingAddress){ billingAddress = nBillingAddress; }

    public void setEmailAddress(String nEmailAddress){ emailAddress = nEmailAddress; }

    public void setID(String nID){ customerID = nID; }

    public void addCredit(int nCredit){ credit += nCredit; }

    public void setCredit(int nCredit){ credit = nCredit; }

    public void setCreditExpirationDate(Date nCreditExpirationDate){ creditExpirationDate = nCreditExpirationDate; }

    public void setGoldStatus(Boolean nGoldStatus){ goldStatus = nGoldStatus; }

    public void addTotalPurchases(long nTotalPurchase){ totalPurchases += nTotalPurchase; }

    public void setTotalPurchases(long nTotalPurchases){ totalPurchases = nTotalPurchases; }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public void readFromParcel(Parcel src)
    {
        try {
            name = src.readString();
            billingAddress = src.readString();
            emailAddress = src.readString();
            customerID = src.readString();
            credit = src.readInt();

            //Get the expiration date
            try {
                String tempExpDate = src.readString();
                SimpleDateFormat dFormat = new SimpleDateFormat("MMddyyyy");
                creditExpirationDate = dFormat.parse(tempExpDate);

            } catch (Exception e) {

            }
            String gold = src.readString();
            goldStatus = (gold.equals("true")) ? true : false;
            totalPurchases = src.readLong();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void writeToParcel(Parcel dest, int d)
    {
        dest.writeString(name);
        dest.writeString(billingAddress);
        dest.writeString(emailAddress);
        dest.writeString(customerID);
        dest.writeInt(credit);

        if (creditExpirationDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy");
            String strExpDate = sdf.format(creditExpirationDate);
            dest.writeString(strExpDate);
        }
        else
            dest.writeString("");

        dest.writeString(goldStatus.toString());
        dest.writeLong(totalPurchases);
    }

    /**
     * Expires Customer credit if past the expiration date
     */
    public void VoidExpiredCredit()
    {
        if (creditExpirationDate == null) {
            credit = 0;
            return;
        }


        Date currentDate = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
            Date tempCurrentDate = new Date();
            String strCurrentDate = dateFormat.format(tempCurrentDate);
            currentDate = dateFormat.parse(strCurrentDate);

            // Check expiration date
            if (currentDate == null || currentDate.after(creditExpirationDate)) {
                credit = 0;
            }
        }
        catch (Exception e){
            return;
        }
    }
}
