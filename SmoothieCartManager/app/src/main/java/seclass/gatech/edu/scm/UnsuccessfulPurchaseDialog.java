package seclass.gatech.edu.scm;

import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;

public class UnsuccessfulPurchaseDialog extends DialogFragment {

    public static UnsuccessfulPurchaseDialog newInstance() {
        UnsuccessfulPurchaseDialog f = new UnsuccessfulPurchaseDialog();
        Bundle args = new Bundle();
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setMessage("Purchase was not successful.")
                .setPositiveButton("Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //((FragmentAlertDialog) getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //((FragmentAlertDialog) getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }
}
