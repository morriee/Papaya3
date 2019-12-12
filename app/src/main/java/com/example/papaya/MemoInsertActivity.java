package com.example.papaya;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MemoInsertActivity extends AppCompatActivity {

    public static final String TAG = "PostActivity";

    EditText mMemoEdit;
    ImageView mPhoto;
    TextView mGpsText;

    String mMemoMode;
    String mMemoId;

    String mMediaPhotoId;
    String mMediaPhotoUri;

    String tempPhotoUri;

    String mDateStr;
    String mMemoStr;
    String mGpsStr;
    Bitmap resultPhotoBitmap;

    boolean isPhotoCaptured;

    boolean isPhotoFileSaved;

    boolean isPhotoCanceled;

    Calendar mCalendar = Calendar.getInstance();
    Button insertDateButton;

    int mSelectdContentArray;
    int mChoicedArrayItem;
    private Toolbar mToolbar;

    Button insertSaveBtn;
    Button delete_Btn;
    Button insertCancelBtn;

    EditText insert_memoEdit;

    Animation translateLeftAnim;
    Animation translateRightAnim;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_insert);

        mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mGpsText = (TextView) findViewById(R.id.itemGps);
        mPhoto = (ImageView)findViewById(R.id.itemPhoto);
        mMemoEdit = (EditText) findViewById(R.id.insert_memoEdit);
        delete_Btn = (Button) findViewById(R.id.delete_Btn);

        insert_memoEdit = (EditText)findViewById(R.id.insert_memoEdit);

        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        mPhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(isPhotoCaptured || isPhotoFileSaved) {
                    showDialog(BasicInfo.CONTENT_PHOTO_EX);
                } else {
                    showDialog(BasicInfo.CONTENT_PHOTO);
                }
            }
        });

        delete_Btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(BasicInfo.CONFIRM_DELETE);
            }
        });

        setBottomButtons();

        setMediaLayout();

        setCalendar();

        Intent intent = getIntent();
        Data data = (Data) intent.getSerializableExtra("OBJECT");

        mMemoMode = intent.getStringExtra(BasicInfo.KEY_MEMO_MODE);
        if(mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
            processIntent(data);

            insertSaveBtn.setText("수정");
            delete_Btn.setVisibility(View.VISIBLE);
        } else {

            insertSaveBtn.setText("저장");
            delete_Btn.setVisibility(View.GONE);
        }

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_button2, menu);


        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.gps_button: {
                gpsTracker = new GpsTracker(MemoInsertActivity.this);

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();


                String address = latitude+","+longitude;
                String address1 = getCurrentAddress(latitude, longitude);

                mGpsText.setText(address1);
                mGpsStr=address;
                Toast.makeText(MemoInsertActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SlidingPageAnimationListener implements AnimationListener {

        public void onAnimationEnd(Animation animation) {

        }

        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }

    }

    public void processIntent(Data data) {

        mMemoId = data.getId();
        mMemoEdit.setText(data.getText());
        mMemoStr = data.getText();
        mDateStr = data.getDate();
        mGpsStr = data.getGps();
        String loc[] = mGpsStr.split(",");
        double lon = Double.parseDouble(loc[0]);
        double lat = Double.parseDouble(loc[1]);
        String kad = getCurrentAddress(lon, lat);
        mGpsText.setText(kad);
        //mGpsText.setText(data.getGps());

        mMediaPhotoId = data.getId_Photo();
        mMediaPhotoUri = data.getUrl_Photo();

        setMediaImage(mMediaPhotoId, mMediaPhotoUri);
    }


    public void setMediaImage(String photoId, String photoUri) {
        Log.d(TAG, "photoId : " + photoId + ", photoUri : " + photoUri);

        if(photoId.equals("") || photoId.equals("-1")) {
            mPhoto.setImageResource(R.drawable.person_add);
        } else {
            isPhotoFileSaved = true;
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
        }

    }


    public void setBottomButtons() {
        insertSaveBtn = (Button)findViewById(R.id.insert_saveBtn);
        insertCancelBtn = (Button)findViewById(R.id.insert_cancelBtn);

        insertSaveBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean isParsed = parseValues();
                if (isParsed) {
                    if(mMemoMode.equals(BasicInfo.MODE_INSERT)) {
                        saveInput();
                    } else if(mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
                        modifyInput();
                        Intent intentBack = new Intent(MemoInsertActivity.this, StoryActivity.class);
                        intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       // intentBack.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intentBack);
                        finish();



                    }
                }
            }
        });


        insertCancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void saveInput() {

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

        if (photoFilename != null) {

            SQL = "select _ID from " + MemoDatabase.TABLE_PHOTO + " where URI = '" + photoFilename + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (StoryActivity.mDatabase != null) {
                Cursor cursor = StoryActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();
            }
        }

        SQL = "insert into " + MemoDatabase.TABLE_MEMO +
                "(INPUT_DATE, CONTENT_TEXT, ID_PHOTO, GPS_TEXT) values(" +
                "DATETIME('" + mDateStr + "'), " +
                "'"+ mMemoStr + "', " +
                "'"+ photoId + "', " +
                "'"+ mGpsStr + "')";

        Log.d(TAG, "SQL : " + SQL);
        if (StoryActivity.mDatabase != null) {
            StoryActivity.mDatabase.execSQL(SQL);
        }

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();

    }


    private void modifyInput() {

        Intent intent = getIntent();

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

        if (photoFilename != null) {

            SQL = "select _ID from " + MemoDatabase.TABLE_PHOTO + " where URI = '" + photoFilename + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (StoryActivity.mDatabase != null) {
                Cursor cursor = StoryActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();

                mMediaPhotoUri = photoFilename;

                SQL = "update " + MemoDatabase.TABLE_MEMO +
                        " set " +
                        " ID_PHOTO = '" + photoId + "'" +
                        " where _id = '" + mMemoId + "'";

                if (StoryActivity.mDatabase != null) {
                    StoryActivity.mDatabase.rawQuery(SQL);
                }

                mMediaPhotoId = String.valueOf(photoId);
            }
        } else if(isPhotoCanceled && isPhotoFileSaved) {
            SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                    " where _ID = '" + mMediaPhotoId + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (StoryActivity.mDatabase != null) {
                StoryActivity.mDatabase.execSQL(SQL);
            }

            File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
            if (photoFile.exists()) {
                photoFile.delete();
            }

            SQL = "update " + MemoDatabase.TABLE_MEMO +
                    " set " +
                    " ID_PHOTO = '" + photoId + "'" +
                    " where _id = '" + mMemoId + "'";

            if (StoryActivity.mDatabase != null) {
                StoryActivity.mDatabase.rawQuery(SQL);
            }

            mMediaPhotoId = String.valueOf(photoId);
        }


        SQL = "update " + MemoDatabase.TABLE_MEMO +
                " set " +
                " INPUT_DATE = DATETIME('" + mDateStr + "'), " +
                " CONTENT_TEXT = '" + mMemoStr + "' ," +
                " GPS_TEXT = '" + mGpsStr + "'" +
                " where _id = '" + mMemoId + "'";

        Log.d(TAG, "SQL : " + SQL);
        if (StoryActivity.mDatabase != null) {
            StoryActivity.mDatabase.execSQL(SQL);
        }

        intent.putExtra(BasicInfo.KEY_MEMO_TEXT, mMemoStr);
        intent.putExtra(BasicInfo.KEY_ID_PHOTO, mMediaPhotoId);
        intent.putExtra(BasicInfo.KEY_URI_PHOTO, mMediaPhotoUri);
        intent.putExtra(BasicInfo.KEY_GPS_TEXT, mGpsStr);

        setResult(RESULT_OK, intent);
        finish();
    }



    private String insertPhoto() {
        String photoName = null;

        if (isPhotoCaptured) {
            try {
                if (mMemoMode != null && mMemoMode.equals(BasicInfo.MODE_MODIFY)) {
                    Log.d(TAG, "previous photo is newly created for modify mode.");

                    String SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                            " where _ID = '" + mMediaPhotoId + "'";
                    Log.d(TAG, "SQL : " + SQL);
                    if (StoryActivity.mDatabase != null) {
                        StoryActivity.mDatabase.execSQL(SQL);
                    }

                    File previousFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }

                File photoFolder = new File(BasicInfo.FOLDER_PHOTO);


                if(!photoFolder.isDirectory()){
                    Log.d(TAG, "creating photo folder : " + photoFolder);
                    photoFolder.mkdirs();
                }


                photoName = createFilename();

                FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultPhotoBitmap.compress(CompressFormat.PNG, 100, outstream);
                outstream.close();


                if (photoName != null) {
                    Log.d(TAG, "isCaptured: " +isPhotoCaptured);


                    String SQL = "insert into " + MemoDatabase.TABLE_PHOTO + "(URI) values(" + "'" + photoName + "')";
                    if (StoryActivity.mDatabase != null) {
                        StoryActivity.mDatabase.execSQL(SQL);
                    }
                }

            } catch (IOException ex) {
                Log.d(TAG, "Exception in copying photo : " + ex.toString());
            }


        }
        return photoName;
    }

    private void deleteMemo() {


        Log.d(TAG, "사진기록과 파일 삭제 : " + mMediaPhotoId);
        String SQL = "delete from " + MemoDatabase.TABLE_PHOTO +
                " where _ID = '" + mMediaPhotoId + "'";

        Log.d(TAG, "SQL : " + SQL);

        if (StoryActivity.mDatabase != null) {
            StoryActivity.mDatabase.execSQL(SQL);
        }

        File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
        if (photoFile.exists()) {
            photoFile.delete();
        }


        Log.d(TAG, "게시물 기록 삭제 : " + mMemoId);
        SQL = "delete from " + MemoDatabase.TABLE_MEMO +
                " where _id = '" + mMemoId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (StoryActivity.mDatabase != null) {
            StoryActivity.mDatabase.execSQL(SQL);
        }

        setResult(RESULT_OK);

        Intent intentBack = new Intent(MemoInsertActivity.this, StoryActivity.class);
        intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // intentBack.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intentBack);

        finish();
    }

    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }


    public void setMediaLayout() {
        isPhotoCaptured = false;

    }

    private void setCalendar(){
        insertDateButton = (Button) findViewById(R.id.insert_dateBtn);
        insertDateButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String mDateStr = insertDateButton.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                try {
                    date = BasicInfo.dateDayNameFormat.parse(mDateStr);
                } catch(Exception ex) {
                    Log.d(TAG, "Exception in parsing date : " + date);
                }

                calendar.setTime(date);

                new DatePickerDialog(
                        MemoInsertActivity.this,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();

            }
        });

        Date curDate = new Date();
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        insertDateButton.setText(year + "년 " + (monthOfYear+1) + "월 " + dayOfMonth + "일");

    }



    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);
            insertDateButton.setText(year + "년 " + (monthOfYear+1) + "월 " + dayOfMonth + "일");
        }
    };



    private boolean parseValues() {
        String insertDateStr = insertDateButton.getText().toString();
        try {
            Date insertDate = BasicInfo.dateDayNameFormat.parse(insertDateStr);
            mDateStr = BasicInfo.dateDayFormat.format(insertDate);
        } catch(ParseException ex) {
            Log.e(TAG, "Exception in parsing date : " + insertDateStr);
        }

        String memotxt = mMemoEdit.getText().toString();
        String gpstxt = mGpsText.getText().toString();
        mMemoStr = memotxt;

        if (mMemoStr.trim().length() < 1) {
            showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
            return false;
        }else if (gpstxt.trim().length() < 1) {
            showDialog(BasicInfo.CONFIRM_GPS_INPUT);
            return false;
        }

        return true;
    }


    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;
        switch(id) {
            case BasicInfo.CONFIRM_GPS_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Feed");
                builder.setMessage("Gps를 설정하세요.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;
            case BasicInfo.CONFIRM_TEXT_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Feed");
                builder.setMessage("내용을 입력하세요.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case BasicInfo.CONTENT_PHOTO:
                builder = new AlertDialog.Builder(this);
                mSelectdContentArray = R.array.array_photo;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
        }
    });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(mChoicedArrayItem == 0 ) {
                            showPhotoCaptureActivity();
                        }
                        /*else if(mChoicedArrayItem == 1) {
                            showPhotoSelectionActivity();
                        }

                         */
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.d(TAG, "whichButton3        ======        " + whichButton);
                    }
                });

                break;

            case BasicInfo.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo_ex;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        }
/*
                        else if(mChoicedArrayItem == 1) {
                            showPhotoSelectionActivity();
                        }

 */
                        else if(mChoicedArrayItem == 2) {

                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            mPhoto.setImageResource(R.drawable.person_add);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case BasicInfo.CONFIRM_DELETE:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Feed");
                builder.setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteMemo();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dismissDialog(BasicInfo.CONFIRM_DELETE);
                    }
                });

                break;
            default:
                break;
        }

        return builder.create();
    }

    public void showPhotoCaptureActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoCaptureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);
    }
/*
    public void showPhotoSelectionActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoSelectionActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);
    }
*/

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode) {
            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
            case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY:  // 사진 찍는 경우
                Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE_ACTIVITY.");

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode : " + resultCode);

                    boolean isPhotoExists = checkCapturedPhotoFile();
                    if (isPhotoExists) {
                        Log.d(TAG, "image file exists : " + BasicInfo.FOLDER_PHOTO + "captured");

                        resultPhotoBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + "captured");

                        tempPhotoUri = "captured";

                        mPhoto.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;

                        mPhoto.invalidate();
                    } else {
                        Log.d(TAG, "image file doesn't exists : " + BasicInfo.FOLDER_PHOTO + "captured");
                    }
                }

                break;
/*
            case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY:  // 사진을 앨범에서 선택하는 경우
                Log.d(TAG, "onActivityResult() for REQ_PHOTO_LOADING_ACTIVITY.");

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode : " + resultCode);

                    Uri getPhotoUri = intent.getParcelableExtra(BasicInfo.KEY_URI_PHOTO);
                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        resultPhotoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoUri), null, options);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    mPhoto.setImageBitmap(resultPhotoBitmap);
                    isPhotoCaptured = true;

                    mPhoto.invalidate();
                }

                break;
*/
        }
    }



    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");
        if(file.exists()) {
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           String[] permissions,
                                           int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MemoInsertActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MemoInsertActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MemoInsertActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MemoInsertActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MemoInsertActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MemoInsertActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MemoInsertActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MemoInsertActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MemoInsertActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
