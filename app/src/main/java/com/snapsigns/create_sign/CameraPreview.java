package com.snapsigns.create_sign;

import android.app.Activity;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.snapsigns.ImageSign;
import com.snapsigns.MainActivity;
import com.snapsigns.SnapSigns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final int MEDIA_TYPE_IMAGE = 1;
    private SurfaceHolder mHolder;
    StorageReference mStorageRef;

    private Camera mCamera;

    //In charge of opening the camera in a separate thread
    private CameraHandlerThread mThread = null;

    MainActivity mActivity;
    private static final String TAG = CameraPreview.class.getSimpleName();

    /**
     * Initializing Camera and SurfaceView for preview
     * @param activity
     */
    public CameraPreview(MainActivity activity) {
        super(activity);
        this.mActivity = activity;
        this.mStorageRef = ((SnapSigns)mActivity.getApplication()).getFirebaseStorageRef();
        openCamera();
        setUpHolder();
    }


    /**Sets up surface holder to host camera preview**/
    private void setUpHolder(){
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**SurfaceHolder Lifecycle method
     * Surface is now created so start camera preview.
     * @param holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG,"SURFACE CREATED CALLED");
        openCamera();
    }

    /**SurfaceHolder Lifecycle method
     * Called when switching between apps or if onDestroy is called.
     * Releases camera and its preview if it hasn't been done already.
     * Destroys the area the preview was displayed in.
     * @param holder
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG,"SURFACE DESTROYED CALLED");
        releaseCameraAndPreview();
        destroyDrawingCache();
    }

    /**SurfaceHolder Lifecycle method
     * Gets called when phone changes views such as portrait to landscape.
     * Figures out what position phone is in and positions the camera accordingly.
     * @param holder
     * @param format
     * @param w
     * @param h
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG,"SURFACE CHANGED CALLED");

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }
        // Stopping preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            Log.d(TAG, "Error during surface changed: " + e.getMessage());

        }
        //Responsible for repositioning camera and activating preview
        startCameraPreview();
    }

    /******************** Opening and release camera methods **********************/

    /** Opens camera in a separate worker thread **/
    public void openCamera() {
        if (mThread == null) {
            mThread = new CameraHandlerThread();
        }

        synchronized (mThread) {
            mThread.openCamera();
        }
    }
    /**Releases camera and sets it to null**/
    public void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    /** Getting camera instance in a safe way. */
    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            Log.d(TAG, "Error getting camera instance: " + e.getMessage());
        }
        return c;
    }



    /**Starts the camera preview.
     * Sets up camera and surface-holder for the preview if they are null.**/
    public void startCameraPreview(){
        if(mCamera == null) return;
        if(mHolder == null || mHolder.getSurface() == null) setUpHolder();

        //Repositioning camera
        setCameraDisplayOrientation(mActivity,0,mCamera);

        //Displaying preview
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }
        catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    /****************************** Position Methods ************************************/

    /************* Setting the camera orientation based on display ***********/
    private void setCameraDisplayOrientation(Activity activity,
                                            int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);

        //Setting orientation for saved pictures
        setSavedPictureDisplayOrientation();
    }

    private void setSavedPictureDisplayOrientation(){
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
            case Surface.ROTATION_90: degrees = 90; break; //Landscape left
            case Surface.ROTATION_180: degrees = 180; break;//Upside down
            case Surface.ROTATION_270: degrees = 270; break;//Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;

        //STEP #2: Set the 'rotation' parameter
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotate);
        mCamera.setParameters(params);
    }

    /******************************** Background Operations ******************************/

    /***************** Opening camera in separate thread *****************/
    private class CameraHandlerThread extends HandlerThread {
        Handler mHandler = null;

        CameraHandlerThread() {
            super("CameraHandlerThread");
            start();
            mHandler = new Handler(getLooper());
        }

        synchronized void notifyCameraOpened() {
            notify();
        }

        void openCamera() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mCamera == null) mCamera = getCameraInstance();
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            }
            catch (InterruptedException e) {
                Log.w(TAG, "wait was interrupted");
            }
        }
    }


    /************************************ Saving Images *****************************************/

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");

                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            Toast.makeText(mActivity,"Photo saved to gallery", Toast.LENGTH_LONG).show();

            ImageSign createdSign = new ImageSign(null,Uri.fromFile(pictureFile),"test/"+pictureFile.getName());
            uploadImageToFirebase(createdSign);

            startCameraPreview();
        }
    };

    public void takePicture(){
        if(mCamera != null) mCamera.takePicture(null,null,mPicture);
    }

    /** Creates a File for saving an image **/
    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void uploadImageToFirebase(ImageSign sign){
        StorageReference signsFolder = mStorageRef.child("signs");
        signsFolder.putFile(sign.getImage());
    }

}
