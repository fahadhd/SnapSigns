package com.snapsigns.settings;


import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.snapsigns.BaseFragment;
import com.snapsigns.R;
import com.snapsigns.SnapSigns;
import com.snapsigns.login.SignInActivity;
import com.snapsigns.utilities.FireBaseUtility;

public class SettingsFragment extends BaseFragment{
    SnapSigns app;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_frag, container, false);
        app = (SnapSigns) getActivity().getApplicationContext();

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

        Button searchRadiusButton = (Button) rootView.findViewById(R.id.searchRadius);
        searchRadiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSearchRadius();
            }
        });

        return rootView;
    }

    private void logout(){
        app.signOut();
        startActivityForResult(new Intent(getActivity(), SignInActivity.class),SignInActivity.SIGN_IN_REQUEST_CODE);
    }

    private void deleteMySigns(){
        new FireBaseUtility(getActivity()).deleteUserSigns();
    }

    private void changeSearchRadius(){
        FragmentManager mgr = getActivity().getFragmentManager();
        new SearchRadiusFragment().show(mgr,"search_radius");
    }


}
