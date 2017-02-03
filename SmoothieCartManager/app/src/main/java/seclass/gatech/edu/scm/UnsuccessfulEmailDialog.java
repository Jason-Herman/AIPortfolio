package seclass.gatech.edu.scm;

import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.Toast;
import edu.gatech.seclass.services.EmailService;

public class UnsuccessfulEmailDialog extends DialogFragment {

    public static UnsuccessfulEmailDialog newInstance(int emailType) {
        UnsuccessfulEmailDialog f = new UnsuccessfulEmailDialog();
        Bundle args = new Bundle();
        args.putInt("emailType", emailType);
        f.setArguments(args);
        return f;
    }

    // member email type
    private int m_emailT = 0;

    /**
     * Gets the email type as a String
     * @return
     */
    private String GetEmailType()
    {
        switch(m_emailT){
            case 1:
                return "Credit notification";
            case 2:
                return "Gold status notification";
        }
        return "";
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        m_emailT = getArguments().getInt("emailType");
        String emailTypeString = GetEmailType();

        OnClickListener positiveClick = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Purchase)getActivity()).sendEmail(m_emailT);
            }
        };

        // Exit the dialog; no action
        OnClickListener negativeClick = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());
        builder.setMessage("The " + emailTypeString + " e-mail failed to send.");
        builder.setNegativeButton("Cancel", negativeClick);
        builder.setPositiveButton("Resend", positiveClick);
        builder.setTitle("Email notification");
        Dialog dialog = builder.create();
        return dialog;
    }
}
