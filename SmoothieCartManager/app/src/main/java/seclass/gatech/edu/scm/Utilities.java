package seclass.gatech.edu.scm;

import android.content.Context;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

    public static boolean validateString (String str) {
        if (str == null) {return false;}
        if (str.length() == 0) {return false;}
        if (!validateNoSQL(str)) {return false;}
        return true;
    }

    public static boolean validateEmail (String email) {
        if (email == null) { return false; }
        if (email.length() == 0) { return false; }
        if (!email.contains("@")) { return false; }
        if ( ( email.length() - email.replace("@", "").length() ) > 1 ) { return false; }
        if (!validateNoSQL(email)) {return false; }
        return true;
    }

    public static boolean validateNoSQL (String str) {
        if (str.toUpperCase().contains("SELECT") || str.toUpperCase().contains("DELETE") ||
                str.toUpperCase().contains("UPDATE") || str.toUpperCase().contains("DROP CUSTOMER") ||
                str.toUpperCase().contains("DROP TRANSACTIONS") || str.toUpperCase().contains("TRUNCATE") ||
                str.contains("\'") || str.contains("\"") || str.contains("\\") || str.contains("/")) {return false; }
        return true;
    }

    /**
     * Show message with specified text
     * @param text
     */
    public static void showToast(Context context, String text)
    {
        try {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        catch (Exception e)
        {

        }
    }

    /**
     * Returns String current date
     * @return
     */
    public static String getStringCurrentDate()
    {
        String strCurrentDate = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
            Date tempCurrentDate = new Date();
            strCurrentDate = dateFormat.format(tempCurrentDate);
        }
        catch (Exception e)
        {
            return null;
        }

        return strCurrentDate;
    }

    public static Date getDate(String dateStr)
    {
        Date date = null;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateStr);
        }
        catch (Exception e)
        {

        }

        return date;
    }

	public static String centsToDollars(int amount) {
        String money = "";
        int cents = amount % 100;
        int dollars = (amount - cents) / 100;

        if (cents < 10)
            money = Integer.toString(dollars) + ".0" + Integer.toString(cents);
        else
            money = Integer.toString(dollars) + "." + Integer.toString(cents);

        return money;
    }

    public static boolean checkNewYear(Date lastDate, int currentYear) {
        boolean bNewYear = false;
        Date dCurrentDate = new Date();  // Get the current date
        dCurrentDate.getTime();
        if ((dCurrentDate != null) && (lastDate != null)) 
		{
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyy");
			
			// get last transaction's year
            String sYearLast = dFormat.format(lastDate);
            Integer iYearLast = Integer.parseInt(sYearLast);
			
			Integer iYearCurrent = 0;
			// get current year
			if (currentYear > 0)
				iYearCurrent = currentYear;
			else
			{
				String sYearCurrent = dFormat.format(dCurrentDate);
				iYearCurrent = Integer.parseInt(sYearCurrent);
			}
			
			// check current year after last-purchase-date's year
			bNewYear = iYearCurrent > iYearLast;
        }
        return bNewYear;
    }
}
