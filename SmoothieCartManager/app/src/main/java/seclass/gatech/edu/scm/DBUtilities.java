package seclass.gatech.edu.scm;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBUtilities {

    //Customer Table Headers Enum
    public enum C_DB_HEADERS {
        ID,
        NAME,
        BILLING_ADDRESS,
        EMAIL_ADDRESS,
        CREDIT,
        CREDIT_EXP_DATE,
        GOLD_STATUS,
        TOTAL_PURCHASES }

    //Transaction Table Headers Enum
    public enum T_DB_HEADERS {
        ID,
        CUSTOMER_ID,
        PREDISCOUNT_AMOUNT,
        GOLD_STATUS,
        CREDITS_APPLIED,
        DATE }
    //not used in app so not explicitly tested
    public int deleteCustomer (ContentResolver contentResolver, String customerID) {
        int delRetVal =  contentResolver.delete(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                DBHelper.CUSTOMER_ID + "= '" +  customerID + "'", null);
        if (delRetVal > 0) {
            Log.d("MainActivity", "deleteCustomer by ID - Deleted customer: " + customerID);
        } else {
            Log.d("MainActivity", "deleteCustomer by ID - Customer " + customerID + " does not exist in db.");
        }

        // Remove the current customer from the dashboard
        if (MainActivity.currentCustomer != null && MainActivity.currentCustomer.getID().equals(customerID))
            MainActivity.currentCustomer = null;

        return delRetVal;
    }
    //not used in app so not explicitly tested
    public int deleteTransaction (ContentResolver contentResolver, String transactionID) {
        int delRetVal =  contentResolver.delete(SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                DBHelper.TRANSACTION_ID + " = '" +  transactionID + "'", null);
        if (delRetVal > 0) {
            Log.d("MainActivity", "deleteTransaction by ID - Deleted transaction: " + transactionID);
        } else {
            Log.d("MainActivity", "deleteTransaction by ID - Transaction " + transactionID + " does not exist in db.");
        }
        return delRetVal;
    }
    //not used in app so not explicitly tested
    /**
     * Delete all customers from the database
     * @return number of customers deleted
     */
    public int deleteAllCustomers(ContentResolver contentResolver) {
        int delRetVal = contentResolver.delete(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE, null, null);
        if (delRetVal > 0) {
            Log.d("MainActivity", "deleteAllCustomers - Deleted " + delRetVal + " customers");
        } else {
            Log.d("MainActivity", "No Customers to delete: " + delRetVal);
        }
        return delRetVal;
    }
    //not used in app so not explicitly tested
    public int deleteAllTransactions (ContentResolver contentResolver) {
        int delRetVal =  contentResolver.delete(SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE, null, null);
        if (delRetVal > 0) {
            Log.d("MainActivity", "deleteAllTransactions - Deleted " + delRetVal + " transactions");
        } else {
            Log.d("MainActivity", "No Transactions to delete: " + delRetVal);
        }
        return delRetVal;
    }
    //not used in app so not explicitly tested
    /**
     * Delete customers from Customer table based on a selection string
     * @param selection
     * @return number of customers deleted
     */
    public int deleteCustomerSelection (ContentResolver contentResolver, String selection) {
        int delRetVal = contentResolver.delete(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                selection, null);
        if (delRetVal > 0) {
            Log.d("MainActivity", "deleteCustomerSelection - Deleted " + delRetVal + " customers\n" +
                    "Used following SQL selection string: " + selection);
        } else {
            Log.d("MainActivity", "No customers deleted: " + delRetVal);
        }
        return delRetVal;
    }

    //Not used in app so not explicitly tested
    public int deleteTransactionSelection (ContentResolver contentResolver, String selection) {
        int delRetVal = contentResolver.delete(SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                selection, null);
        if (delRetVal > 0) {
            Log.d("MainActivity", "deleteTransactionSelection - Deleted " + delRetVal + " transactions\n" +
                    "Used following SQL selection string: " + selection);
        } else {
            Log.d("MainActivity", "No transactions (" + delRetVal + ") deleted by SQL selection string: " + selection);
        }
        return delRetVal;
    }

    public int deleteTransactionsForCustomer (ContentResolver contentResolver, String customerID) {
        int delRetVal = contentResolver.delete(SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                DBHelper.TRANSACTION_CUSTOMER_ID + " = '" + customerID + "'", null);
        if (delRetVal > 0) {
            Log.d("MainActivity", "deleteTransactiosnForCustomer - Deleted " + delRetVal +
                    " transactions for customer " + customerID);
        } else {
            Log.d("MainActivity", "No transactions (" + delRetVal + ") deleted for customer " + customerID);
        }
        return delRetVal;
    }

    /**
     * Get the requested customer object from the database by the specified ID
     * @param customerID
     * @return
     */
    public Customer getCustomer (ContentResolver contentResolver, String customerID) {
        Customer customer = null;
        Cursor c = contentResolver.query(
                SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                DBHelper.ALL_COLUMNS_CUSTOMER,
                "_id = '" + customerID + "'",
                null,
                null);
        if (c.getCount() <= 0) {
            Log.d("MainActivity", "getCustomer - Customer " + customerID + " does not exist in db.");
            c.close();
            return customer;
        }
        c.moveToNext();
        boolean goldStatus = false;

        if (c.getInt(C_DB_HEADERS.GOLD_STATUS.ordinal()) == 0) {
            goldStatus = false;
        } else {
            goldStatus = true;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            Log.d("MainActivity", "Date in db is: " + c.getString(C_DB_HEADERS.CREDIT_EXP_DATE.ordinal()));
            date = df.parse(c.getString(C_DB_HEADERS.CREDIT_EXP_DATE.ordinal()));

            customer = new Customer(c.getString(C_DB_HEADERS.ID.ordinal()), c.getString(C_DB_HEADERS.NAME.ordinal()),
                    c.getString(C_DB_HEADERS.BILLING_ADDRESS.ordinal()),
                    c.getString(C_DB_HEADERS.EMAIL_ADDRESS.ordinal()), c.getInt(C_DB_HEADERS.CREDIT.ordinal()), date, goldStatus, c.getLong(C_DB_HEADERS.TOTAL_PURCHASES.ordinal()));
            Log.d("MainActivity", "Date in db is: " + c.getString(5));
        } catch (ParseException e) {
            Log.d("MainActivity", "getCustomer - Failed to parse date string that was retrieved from database.");
        } catch (Exception e) {
            Log.d("MainActivity", "getCustomer - Something went wrong while trying to construct customer object");
        }

        c.close();
        return customer;
    }

    public Customer[] getCustomers (ContentResolver contentResolver, String selection) {
        Cursor c = contentResolver.query(
                SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE,
                DBHelper.ALL_COLUMNS_CUSTOMER,
                selection,
                null,
                null);

        Customer[] customers = new Customer[c.getCount()];
        int i = 0;
        while (c.moveToNext()) {
            Customer customer = getCustomer(contentResolver, c.getString(C_DB_HEADERS.ID.ordinal()));
            customers[i] = customer;
            i++;
        }

        return customers;
    }

    public Date getDateOfLastTransaction (ContentResolver contentResolver, String customerID) {
        Date date = null;
        Cursor c = contentResolver.query(
                SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                DBHelper.ALL_COLUMNS_TRANSACTION,
                DBHelper.TRANSACTION_CUSTOMER_ID + " = '" + customerID + "'",
                null,
                DBHelper.TRANSACTION_DATE + " DESC");

        if (c.getCount() > 0) {
            c.moveToNext();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = df.parse(c.getString(T_DB_HEADERS.DATE.ordinal()));
            } catch (ParseException e) {
                Log.d("MainActivity", "Parsing of date failed. Date in DB: " + c.getString(T_DB_HEADERS.DATE.ordinal()));
                c.close();
                return date;
            }
        } else {
            Log.d("MainActivity", "No transactions in system for customer " + customerID);
        }

        c.close();
        return date;
    }

    //Not explicitly tested, but used in insertTransaction test
    public Transaction getTransaction (ContentResolver contentResolver, int transactionID) {
        Transaction tran = null;
        Cursor c = contentResolver.query(
                SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                DBHelper.ALL_COLUMNS_TRANSACTION,
                "_id = '" + transactionID + "'",
                null,
                null);
        if (c.getCount() <= 0) {
            Log.d("MainActivity", "getTransaction - Transaction " + transactionID + " does not exist in db.");
            c.close();
            return tran;
        }
        c.moveToNext();
        boolean goldStatus = false;

        if (c.getInt(T_DB_HEADERS.GOLD_STATUS.ordinal()) == 0) {
            goldStatus = false;
        } else {
            goldStatus = true;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            Log.d("MainActivity", "Date in db is: " + c.getString(T_DB_HEADERS.DATE.ordinal()));
            date = df.parse(c.getString(T_DB_HEADERS.DATE.ordinal()));
        } catch (ParseException e) {
            Log.d("MainActivity", "getCustomer - Failed to parse date string that was retrieved from database.");
            return tran;
        }

        tran = new Transaction(c.getInt(T_DB_HEADERS.ID.ordinal()), c.getString(T_DB_HEADERS.CUSTOMER_ID.ordinal()),
                c.getInt(T_DB_HEADERS.PREDISCOUNT_AMOUNT.ordinal()), goldStatus,
                c.getInt(T_DB_HEADERS.CREDITS_APPLIED.ordinal()), date);

        c.close();
        return tran;
    }

    public Transaction[] getTransactions (ContentResolver contentResolver, String selection) {
        Cursor c = contentResolver.query(
                SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                DBHelper.ALL_COLUMNS_TRANSACTION,
                selection,
                null,
                null);

        Transaction[] transactions = new Transaction[c.getCount()];
        int i = 0;
        while (c.moveToNext()) {
            Transaction customer = getTransaction(contentResolver, c.getInt(T_DB_HEADERS.ID.ordinal()));
            transactions[i] = customer;
            i++;
        }

        return transactions;
    }

    /**
     * Get all transactions for a given customer based on customer's ID. If a transaction has a malformed date string,
     * the Transaction object returned for that record in the DB will be a default placeholder transaction with no
     * meaningful values.
     * @param contentResolver: ContentResolver for the Activity calling this method
     * @param customerID: ID of customer whose transactions will be returned
     * @return transactions associated with given customerID
     */
    public Transaction[] getTransactionsForCustomer (ContentResolver contentResolver, String customerID) {
        Cursor c = contentResolver.query(
                SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE,
                DBHelper.ALL_COLUMNS_TRANSACTION,
                DBHelper.TRANSACTION_CUSTOMER_ID + " = '" + customerID + "'",
                null,
                DBHelper.TRANSACTION_DATE + " DESC");

        int count = c.getCount();
        if (count <= 0) {
            Log.d("MainActivity", "Customer " + customerID + " has no transactions in DB.");
            c.close();
            return null;
        }

        Transaction[] transactions = new Transaction[count];
        int index = 0;
        while (c.moveToNext()) {
            Transaction tran = null;
            boolean goldStatus = false;

            if (c.getInt(T_DB_HEADERS.GOLD_STATUS.ordinal()) == 0) {
                goldStatus = false;
            } else {
                goldStatus = true;
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                Log.d("MainActivity", "getTransactionsForCustomer - Date in db is: " + c.getString(T_DB_HEADERS.DATE.ordinal()));
                date = df.parse(c.getString(T_DB_HEADERS.DATE.ordinal()));
            } catch (ParseException e) {
                Log.d("MainActivity", "getTransactionsForCustomer - Failed to parse date string that was retrieved from database.");
                transactions[index] = new Transaction();
                index++;
                continue;
            }

            tran = new Transaction(c.getInt(T_DB_HEADERS.ID.ordinal()), c.getString(T_DB_HEADERS.CUSTOMER_ID.ordinal()),
                    c.getInt(T_DB_HEADERS.PREDISCOUNT_AMOUNT.ordinal()), goldStatus,
                    c.getInt(T_DB_HEADERS.CREDITS_APPLIED.ordinal()), date);

            transactions[index] = tran;
            index++;
        }

        c.close();
        return transactions;
    }

    public Uri insertCustomer( ContentResolver contentResolver, String name, String billingAddress, String emailAddress) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.CUSTOMER_NAME, name);
        values.put(DBHelper.CUSTOMER_BILLING_ADDRESS, billingAddress);
        values.put(DBHelper.CUSTOMER_EMAIL_ADDRESS, emailAddress);
        values.put(DBHelper.CUSTOMER_CREDIT, 0);
        values.put(DBHelper.CUSTOMER_CREDIT_EXPIRATION, "1900-01-01");
        values.put(DBHelper.CUSTOMER_GOLD_STATUS, 0);
        Uri customerUri =  contentResolver.insert(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE, values);
        Log.d("MainActivity", "Inserted Customer " + customerUri.getLastPathSegment());
        return customerUri;
    }

    public Uri insertCustomer( ContentResolver contentResolver, String customerID, String name, String billingAddress, String emailAddress) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.CUSTOMER_NAME, name);
        values.put(DBHelper.CUSTOMER_BILLING_ADDRESS, billingAddress);
        values.put(DBHelper.CUSTOMER_EMAIL_ADDRESS, emailAddress);
        values.put(DBHelper.CUSTOMER_CREDIT, 0);
        values.put(DBHelper.CUSTOMER_CREDIT_EXPIRATION, "1900-01-01");
        values.put(DBHelper.CUSTOMER_GOLD_STATUS, 0);
        Uri insertUri = Uri.withAppendedPath(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE, "/" + customerID);
        Uri customerUri =  contentResolver.insert(insertUri, values);
        if (customerUri == null) {
            Log.d("SCMLog - DBUtilities", "customerUri was null - likely a customer already existed with the provided ID");
        } else {
            Log.d("SCMLog - DBUtilities", "Inserted Customer " + customerUri.getLastPathSegment());
        }
        return customerUri;
    }


    public Uri insertTransaction ( ContentResolver contentResolver, Transaction transaction ) {
        int goldStatus = 0;
        if (transaction == null) {
            return null;
        }
        if (transaction.goldStatus) {
            goldStatus = 1;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        ContentValues values = new ContentValues();
        values.put(DBHelper.TRANSACTION_CUSTOMER_ID, transaction.customerID);
        values.put(DBHelper.TRANSACTION_PREDISCOUNT_AMOUNT, transaction.preDiscountAmount);
        values.put(DBHelper.TRANSACTION_GOLD_STATUS, goldStatus);
        values.put(DBHelper.TRANSACTION_CREDITS_APPLIED, transaction.creditsApplied);
        values.put(DBHelper.TRANSACTION_DATE, df.format(transaction.date));

        Uri returnUri = contentResolver.insert(SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE, values);

        if (returnUri == null) {
            Log.d("MainActivity", "Transaction failed to insert");
        } else {
            Log.d("MainActivity", "Transaction " + returnUri.getLastPathSegment() + " added to DB.");
        }

        return returnUri;
    }

    public void updateCustomer( ContentResolver contentResolver, String customerID, ContentValues values) {
        Uri uri = Uri.withAppendedPath(SCMContentProvider.CONTENT_URI_CUSTOMER_TABLE, "/" + customerID);
        contentResolver.update(uri, values, "_id = '" + customerID + "'", null);
    }

    //Not used so not testing.
    public void updateTransaction ( ContentResolver contentResolver, Transaction transaction) {
        Uri uri = Uri.withAppendedPath(SCMContentProvider.CONTENT_URI_TRANSACTION_TABLE, "/" + transaction.ID);
        int goldStatus = 0;
        if (transaction.goldStatus) {
            goldStatus = 1;
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        ContentValues values = new ContentValues();
        values.put(DBHelper.TRANSACTION_CUSTOMER_ID, transaction.customerID);
        values.put(DBHelper.TRANSACTION_PREDISCOUNT_AMOUNT, transaction.preDiscountAmount);
        values.put(DBHelper.TRANSACTION_GOLD_STATUS, goldStatus);
        values.put(DBHelper.TRANSACTION_CREDITS_APPLIED, transaction.creditsApplied);
        values.put(DBHelper.TRANSACTION_DATE, df.format(transaction.date));

        contentResolver.update(uri, values, null, null);
    }




}
