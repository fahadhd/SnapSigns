package com.snapsigns.utilities;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;


import com.snapsigns.R;

//Dialog to add a workout to a current session. Sends resulting data to ExerciseActivity.

public class AddTagDialog extends DialogFragment implements View.OnClickListener{
    public final String TAG =AddTagDialog.class.getSimpleName();
    Button mCancel, mConfirm;
    AutoCompleteTextView mAddTagView;
    Communicator communicator;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_tag_dialog_fragment, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mCancel = (Button)rootView.findViewById(R.id.dialog_cancel);
        mConfirm = (Button)rootView.findViewById(R.id.dialog_ok);
        mAddTagView = (AutoCompleteTextView) rootView.findViewById(R.id.enter_tag_view);


        mAddTagView.setFocusableInTouchMode(true);
        mAddTagView.requestFocus();
        mAddTagView.setCursorVisible(true);


        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }



    @Override
    public void onClick(View v) {
        Log.v(TAG,v.getId()+"");
        switch (v.getId()) {
            case R.id.dialog_ok :
                if(mAddTagView != null)
                    communicator.addTag(mAddTagView.getEditableText().toString());
                dismiss();

                break;

            case R.id.dialog_cancel:
                dismiss();
                break;
        }

    }


    //Used to send information of a new workout to exercise activity
    public interface Communicator{
        void addTag(String tag);
    }




}
