package com.snapsigns.create_sign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.snapsigns.BaseFragment;
import com.snapsigns.MainActivity;
import com.snapsigns.R;

public class CameraFragment extends BaseFragment {

    private CameraPreview mPreview;
    private static final String TAG = CameraFragment.class.getSimpleName();
    FrameLayout preview;
    ImageButton mCaptureButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.camera_fragment,container,false);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview((MainActivity) getActivity());
        preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        mCaptureButton = (ImageButton) getActivity().findViewById(R.id.button_capture);
        mCaptureButton.setEnabled(true);



        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCaptureButton.setEnabled(false);
                mPreview.takePicture();


            }
        });


        return  rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreview.openCamera();
        mPreview.startCameraPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreview.releaseCameraAndPreview();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPreview.releaseCameraAndPreview();
    }
}