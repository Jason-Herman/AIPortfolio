package seclass.gatech.edu.scm;

import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;

import java.text.NumberFormat;
import java.util.Locale;

public class SuccessfulPurchaseDialog extends DialogFragment {

    public static SuccessfulPurchaseDialog newInstance(int amountPurchased, int currentCredit, boolean goldStatus) {
        SuccessfulPurchaseDialog f = new SuccessfulPurchaseDialog();
        Bundle args = new Bundle();
        args.putInt("amountPurchased", amountPurchased);
        args.putInt("currentCredit", currentCredit);
        args.putBoolean("goldStatus", goldStatus);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int amountPurchased = getArguments().getInt("amountPurchased");
        int currentCredit = getArguments().getInt("currentCredit");
        boolean goldStatus = getArguments().getBoolean("goldStatus");

        //Convert cents to dollars
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
        String amountPurchasedString = n.format(amountPurchased / 100.0);
        String currentCreditString = n.format(currentCredit / 100.0);

        String goldStatusString;
        if(goldStatus == true) {
            goldStatusString = "Achieved gold status.";
        } else{
            goldStatusString = "";
        }

        return new AlertDialog.Builder(getActivity())
                .setMessage("Purchase has been successfully completed.\n\nTotal Amount: " + amountPurchasedString + "\nCurrent Credit: " + currentCreditString + "\n" + goldStatusString)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //((FragmentAlertDialog) getActivity()).doPositiveClick();
                                    }
                                }
                        )
                        .create();
    }
}
