package com.example.papaya;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.skt.Tmap.TMapView;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MemoViewActivity extends AppCompatActivity {
    TextView mMemoText;
    TextView mMemoDate;
    TextView mMemoGps;
    ImageView mPhoto;

    String mMemoMode;
    String mMemoId;
    String mMediaPhotoId;
    String mMediaPhotoUri;

    String tempPhotoUri;

    String mDateStr;
    String mMemoStr;
    String mGpsStr;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.memo_view_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mPhoto = (ImageView)findViewById(R.id.itemPhoto);
        mMemoText = (TextView) findViewById(R.id.itemText);
        mMemoDate = (TextView) findViewById(R.id.itemDate);
        mMemoGps = (TextView) findViewById(R.id.itemGps); //티맵에도 쓰임.

        Intent intent = getIntent();
        Data data = (Data) intent.getSerializableExtra("OBJECT");

        mMemoMode = intent.getStringExtra(BasicInfo.KEY_MEMO_MODE);
        if(mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
            processIntent(data);
        }

        //주소를 받아오면 맵으로 이동....?
        mMemoGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3번째 파라미터 생략 == 지도 이동 Animation 사용안함

                String[] s = mGpsStr.split(",");
                double longitude = Double.parseDouble(s[1]);
                double latitude = Double.parseDouble(s[0]);


                Intent intent = new Intent(MemoViewActivity.this, TMapActivity.class);


                intent.putExtra("mGpsStr",mGpsStr);


                startActivityForResult(intent,1);

//                tMapView.setCenterPoint(longitude,latitude);
                //tMapView.setCenterPoint(127.0100943,37.5826508);
                //tMapView.setCenterPoint(126.988205, 37.551135);

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_button, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
            case R.id.toolbar_button: {
                Intent intent = new Intent(MemoViewActivity.this,MemoInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_MEMO_MODE,   BasicInfo.MODE_VIEW);


                Data data = new Data();
                data.setId(mMemoId);
                data.setDate(mMemoStr);
                data.setText(mMemoStr);
                data.setGps(mGpsStr);
                data.setId_Photo(mMediaPhotoId);
                data.setUrl_Photo(mMediaPhotoUri);


                intent.putExtra("OBJECT", data);

                startActivityForResult(intent,BasicInfo.REQ_VIEW_ACTIVITY);

            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void processIntent(Data data) {
        mMemoId = data.getId();
        mMemoText.setText(data.getText());
        mMemoStr = data.getText();
        mMemoDate.setText(data.getDate());
        mDateStr = data.getDate();
        mMediaPhotoId = data.getId_Photo();
        mMediaPhotoUri = data.getUrl_Photo();
        mMemoGps.setText(data.getGps());
        mGpsStr = data.getGps();
        String loc[] = mGpsStr.split(",");
        double lon = Double.parseDouble(loc[0]);
        double lat = Double.parseDouble(loc[1]);
        String kad = getCurrentAddress(lon,lat);
        mMemoGps.setText(kad);


        setMediaImage(mMediaPhotoId, mMediaPhotoUri);


    }


    public void setMediaImage(String photoId, String photoUri) {

        if(photoId.equals("") || photoId.equals("-1")) {
            mPhoto.setImageResource(R.drawable.person_add);
        } else {
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
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
}