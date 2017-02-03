package seclass.gatech.edu.scm;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.widget.Toast;
import android.content.DialogInterface.OnClickListener;

public class SuccessfulEmailDialog extends DialogFragment {

    public static SuccessfulEmailDialog newInstance(int emailType) {
        SuccessfulEmailDialog f = new SuccessfulEmailDialog();
        Bundle args = new Bundle();
        args.putInt("emailType", emailType);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int emailType = getArguments().getInt("emailType");

        String emailTypeString;
        switch(emailType){
            case 1: emailTypeString = "credit notification";
                break;
            case 2: emailTypeString = "gold status notification";
                break;
            default: emailTypeString = "";
                break;
        }

        OnClickListener positiveClick = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setMessage("The " + emailTypeString + " e-mail has been successfully sent.");
        builder.setPositiveButton("Ok", positiveClick);
        builder.setTitle("Email notification");
        Dialog dialog = builder.create();
        return dialog;
    }
}
