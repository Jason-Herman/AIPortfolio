package seclass.gatech.edu.scm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB Helper Class
 */
public class DBHelper extends SQLiteOpenHelper {

    // Values for creating database
    private static final String DB_NAME = "SmoothieCartManager.db";
    private static final int DB_VERSION = 1;

    // Values for creating customer table
    public static final String TABLE_CUSTOMER = "customer";
    public static final String CUSTOMER_ID = "_id";
    public static final String CUSTOMER_NAME = "name";
    public static final String CUSTOMER_BILLING_ADDRESS = "billing_address";
    public static final String CUSTOMER_EMAIL_ADDRESS = "email";
    public static final String CUSTOMER_CREDIT = "credit";
    public static final String CUSTOMER_CREDIT_EXPIRATION = "credit_expiration_date";
    public static final String CUSTOMER_GOLD_STATUS = "gold_status";
    public static final String CUSTOMER_TOTAL_PURCHASES = "total_purchases";

    public static final String[] ALL_COLUMNS_CUSTOMER =
            {CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_BILLING_ADDRESS, CUSTOMER_EMAIL_ADDRESS,
            CUSTOMER_CREDIT, CUSTOMER_CREDIT_EXPIRATION, CUSTOMER_GOLD_STATUS, CUSTOMER_TOTAL_PURCHASES};

    private static final String TABLE_CUSTOMER_CREATE =
            "CREATE TABLE " + TABLE_CUSTOMER + " (" +
                    CUSTOMER_ID + " TEXT PRIMARY KEY, " +
                    CUSTOMER_NAME + " TEXT, " +
                    CUSTOMER_BILLING_ADDRESS + " TEXT, " +
                    CUSTOMER_EMAIL_ADDRESS + " TEXT, " +
                    CUSTOMER_CREDIT + " INTEGER, " +
                    CUSTOMER_CREDIT_EXPIRATION + " TEXT, " +
                    CUSTOMER_GOLD_STATUS + " INTEGER, " +
                    CUSTOMER_TOTAL_PURCHASES + " INTEGER" +
                    ")";

    // Values for creating transaction table
    public static final String TABLE_TRANSACTION = "transactions";
    public static final String TRANSACTION_ID = "_id";
    public static final String TRANSACTION_CUSTOMER_ID = "customer_id";
    public static final String TRANSACTION_PREDISCOUNT_AMOUNT = "amount_pre";
    public static final String TRANSACTION_GOLD_STATUS = "gold_status";
    public static final String TRANSACTION_CREDITS_APPLIED = "credit_used";
    public static final String TRANSACTION_DATE = "date";

    public static final String[] ALL_COLUMNS_TRANSACTION =
            {TRANSACTION_ID, TRANSACTION_CUSTOMER_ID, TRANSACTION_PREDISCOUNT_AMOUNT, TRANSACTION_GOLD_STATUS,
            TRANSACTION_CREDITS_APPLIED, TRANSACTION_DATE};

    private static final String TABLE_TRANSACTION_CREATE =
            "CREATE TABLE " + TABLE_TRANSACTION + " (" +
                    TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRANSACTION_CUSTOMER_ID + " TEXT, " +
                    TRANSACTION_PREDISCOUNT_AMOUNT + " INTEGER, " +
                    TRANSACTION_GOLD_STATUS + " INTEGER, " +
                    TRANSACTION_CREDITS_APPLIED + " INTEGER, " +
                    TRANSACTION_DATE + " TEXT" +
                    ")";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CUSTOMER_CREATE);
        db.execSQL(TABLE_TRANSACTION_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_CREATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_CREATE);
        onCreate(db);
    }
}
