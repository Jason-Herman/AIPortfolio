package seclass.gatech.edu.scm;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * ContentProvider implementation for Smoothie Cart Manager.
 */
public class SCMContentProvider extends ContentProvider {

    private static final String AUTHORITY = "seclass.gatech.edu.scm.provider";
    private static final String BASE_PATH_CUSTOMER = DBHelper.TABLE_CUSTOMER;
    private static final String BASE_PATH_TRANSACTION = DBHelper.TABLE_TRANSACTION;
    public static final Uri CONTENT_URI_CUSTOMER_TABLE =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_CUSTOMER);
    public static final Uri CONTENT_URI_TRANSACTION_TABLE =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH_TRANSACTION);

    private static final int CUSTOMER = 1;
    private static final int CUSTOMER_ID = 2;
    private static final int TRANSACTION = 3;
    private static final int TRANSACTION_ID = 4;

    private static SecureRandom random = new SecureRandom();

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH_CUSTOMER, CUSTOMER);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_CUSTOMER + "/*", CUSTOMER_ID);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_TRANSACTION, TRANSACTION);
        uriMatcher.addURI(AUTHORITY, BASE_PATH_TRANSACTION + "/#", TRANSACTION_ID);
    }

    private SQLiteDatabase db;


    @Override
    public boolean onCreate() {
        DBHelper helper = new DBHelper(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case CUSTOMER:
                cursor = db.query(DBHelper.TABLE_CUSTOMER, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CUSTOMER_ID: throw new SQLException("Querying for a Customer ID in URI not yet implemented");

            case TRANSACTION:
                cursor = db.query(DBHelper.TABLE_TRANSACTION, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case TRANSACTION_ID: throw new SQLException("Querying for a Transaction ID in URI not yet implemented");

            default: throw new SQLException("Failure to query from " + uri);

        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private String nextID() {
        return new BigInteger(128, random).toString(16);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = null;
        String id;
        long insertRetVal;

        switch (uriMatcher.match(uri)) {
            case CUSTOMER:
                id = nextID();
                values.put(DBHelper.CUSTOMER_ID, id);
                insertRetVal = db.insert(DBHelper.TABLE_CUSTOMER, null, values);
                if ( insertRetVal > 0 ) {
                    returnUri = Uri.withAppendedPath(uri, "/" + id);
                }
                break;

            case CUSTOMER_ID:
                values.put(DBHelper.CUSTOMER_ID, uri.getLastPathSegment());
                insertRetVal = db.insert(DBHelper.TABLE_CUSTOMER, null, values);
                if (insertRetVal > 0) {
                    returnUri = uri;
                }
                break;

            case TRANSACTION:
                insertRetVal = db.insert(DBHelper.TABLE_TRANSACTION, null, values);
                if ( insertRetVal > 0 ) {
                    returnUri = Uri.withAppendedPath(uri, "/" + insertRetVal);
                }
                break;

            case TRANSACTION_ID:
                values.put(DBHelper.TRANSACTION_ID, uri.getLastPathSegment());
                insertRetVal = db.insert(DBHelper.TABLE_TRANSACTION, null, values);
                if (insertRetVal > 0) {
                    returnUri = uri;
                }
                break;

            default: throw new SQLException("Failure to insert row into " + uri);

        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CUSTOMER:
                return db.delete(DBHelper.TABLE_CUSTOMER, selection, selectionArgs);

            case TRANSACTION:
                return db.delete(DBHelper.TABLE_TRANSACTION, selection, selectionArgs);

            case CUSTOMER_ID:
                throw new SQLException("Deletion by customer ID in URI not implemented yet");

            case TRANSACTION_ID:
                throw new SQLException("Deletion by transaction ID in URI not implemented yet");

            default: throw new SQLException("Failure to delete from " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CUSTOMER:
                return db.update(DBHelper.TABLE_CUSTOMER, values, selection, selectionArgs);

            case TRANSACTION:
                return db.update(DBHelper.TABLE_TRANSACTION, values, selection, selectionArgs);

            case CUSTOMER_ID:
                String customerID = uri.getLastPathSegment();
                return db.update(DBHelper.TABLE_CUSTOMER, values, "_id = '" + customerID + "'", null);

            case TRANSACTION_ID:
                String tranID = uri.getLastPathSegment();
                return db.update(DBHelper.TABLE_TRANSACTION, values, "_id = '" + tranID + "'", null);

            default: throw new SQLException("Failure to update " + uri);
        }
    }
}
