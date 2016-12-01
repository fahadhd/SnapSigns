package com.snapsigns.utilities;


import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;


import com.snapsigns.R;

//Dialog to add a workout to a current session. Sends resulting data to ExerciseActivity.

public class AddTagDialog extends DialogFragment implements View.OnClickListener{
    public final String TAG =AddTagDialog.class.getSimpleName();
    Button mCancel, mConfirm;
    AutoCompleteTextView mAddTagView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_tag_dialog_fragment, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mCancel = (Button)rootView.findViewById(R.id.dialog_cancel);
        mConfirm = (Button)rootView.findViewById(R.id.dialog_ok);
        mAddTagView = (AutoCompleteTextView) rootView.findViewById(R.id.enter_tag_view);


        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);


        return rootView;
    }



    @Override
    public void onClick(View v) {
        Log.v(TAG,v.getId()+"");
        switch (v.getId()) {
            case R.id.dialog_ok :
                //TODO: Send back tag info
                break;

            case R.id.dialog_cancel:
                dismiss();
                break;
        }

    }




}
