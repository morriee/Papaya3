package com.example.papaya;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class TMapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);

        //skt map 여기서부터
        setContentView(R.layout.activity_tmap);
        LinearLayout linearLayoutT_map = (LinearLayout)findViewById(R.id.linearLayoutT_map);
        final TMapView tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("8398fcc7-82cd-40e9-825f-28383d253bbd");
        linearLayoutT_map.addView(tMapView);
        //여까지

        String mGpsStr = getIntent().getStringExtra("mGpsStr");

        String s[] = mGpsStr.split(",");

        Double latitude = Double.parseDouble(s[0]);
        Double longitude = Double.parseDouble(s[1]);


        // 마커 아이콘
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poi_dot);
        final TMapPoint tMapPoint = new TMapPoint(latitude, longitude);
        final TMapMarkerItem markerItem1 = new TMapMarkerItem();
        markerItem1.setIcon(bitmap); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint(tMapPoint); // 마커의 좌표 지정
        tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
        tMapView.setCenterPoint(longitude,latitude);

    }
}
