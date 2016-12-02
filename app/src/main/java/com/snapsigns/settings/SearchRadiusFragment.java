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

import com.snapsigns.R;


public class SearchRadiusFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        final View dialogView = layoutInflater.inflate(R.layout.search_radius_dialog,null);
        EditText sR = (EditText) dialogView.findViewById(R.id.searchRadius);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sR.setHint("Current: "+sharedPref.getInt("searchRadiusKey",500));


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search Radius in Meters")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText sR = (EditText) dialogView.findViewById(R.id.searchRadius);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sharedPref.edit().putInt(getString(R.string.searchRadiusKey),Integer.parseInt(sR.getEditableText().toString())).apply();
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
