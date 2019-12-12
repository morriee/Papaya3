package com.example.papaya;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PhotoCaptureActivity extends Activity {

    public static final String TAG = "PhotoCaptureActivity";

    CameraSurfaceView mCameraView;

    FrameLayout mFrameLayout;


    boolean processing = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setContentView(R.layout.activity_photo_capture);

        mCameraView = new CameraSurfaceView(getApplicationContext());
        mFrameLayout = (FrameLayout) findViewById(R.id.frame);
        mFrameLayout.addView(mCameraView);

        setCaptureBtn();

    }


    public void setCaptureBtn() {
        ImageButton takeBtn = (ImageButton) findViewById(R.id.capture_takeBtn);
        takeBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!processing) {
                    processing = true;
                    mCameraView.capture(new CameraPictureCallback());
                }
            }
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            mCameraView.capture(new CameraPictureCallback());

            return true;
        } else if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();

            return true;
        }

        return false;
    }


    class CameraPictureCallback implements Camera.PictureCallback {

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.v(TAG, "onPictureTaken() called.");

            int bitmapWidth = 480;
            int bitmapHeight = 360;

            Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(capturedBitmap, bitmapWidth, bitmapHeight, false);

            Bitmap resultBitmap = null;

            Matrix matrix = new Matrix();
            matrix.postRotate(0);

            resultBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);

            try {
                File photoFolder = new File(BasicInfo.FOLDER_PHOTO);


                if(!photoFolder.isDirectory()){
                    Log.d(TAG, "creating photo folder : " + photoFolder);
                    photoFolder.mkdirs();
                }

                String photoName = "captured";


                File file = new File(BasicInfo.FOLDER_PHOTO + photoName);
                if(file.exists()) {
                    file.delete();
                }

                FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultBitmap.compress(CompressFormat.PNG, 100, outstream);


                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(BasicInfo.FOLDER_PHOTO + photoName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap bmRotated = rotateBitmap(resultBitmap, orientation);
                bmRotated.compress(CompressFormat.PNG,100,outstream);
                outstream.close();

            } catch (Exception ex) {
                Log.e(TAG, "Error in writing captured image.", ex);
                showDialog(BasicInfo.IMAGE_CANNOT_BE_STORED);
            }

            showParentActivity();
        }
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showParentActivity() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);

        finish();
    }


    protected Dialog onCreateDialog(int id) {
        Log.d(TAG, "onCreateDialog() called");

        switch (id) {
            case BasicInfo.IMAGE_CANNOT_BE_STORED:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("사진을 저장할 수 없습니다. SD카드 상태를 확인하세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                return builder.create();
        }

        return null;
    }

}