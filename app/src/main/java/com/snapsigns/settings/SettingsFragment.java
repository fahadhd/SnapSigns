package com.snapsigns.settings;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.snapsigns.BaseFragment;
import com.snapsigns.R;
import com.snapsigns.SignIn;
import com.snapsigns.SnapSigns;
import com.snapsigns.utilities.FireBaseUtility;

public class SettingsFragment extends BaseFragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_frag, container, false);

        Button logoutButton = (Button) rootView.findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        Button deleteMySignsButton = (Button) rootView.findViewById(R.id.deleteMySigns);
        deleteMySignsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMySigns();
            }
        });

        return rootView;
    }

    private void logout(){
        ((SnapSigns) getActivity().getApplicationContext()).signOut();
    }

    private void deleteMySigns(){
        new FireBaseUtility(getActivity()).deleteUserSigns();
    }

    private void openLink(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
