package com.snapsigns.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.snapsigns.R;
import com.snapsigns.utilities.FireBaseUtility;


public class DeleteSignsDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View dialogView = layoutInflater.inflate(R.layout.delete_dialog,null);



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete all of your Image Signs?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FireBaseUtility fireBaseUtility = new FireBaseUtility(getActivity());
                        fireBaseUtility.deleteUserSigns();
                        Toast.makeText(getActivity(),"Your signs have been deleted!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setView(dialogView);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
