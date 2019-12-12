package com.example.papaya;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;




import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;


import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    Bitmap bitmap;
    TextView feed_btn;
    TextView cal_btn;
    TextView memo_btn;
    TextView diary_btn;
    LinearLayout linear_main;
    private static Handler mHandler ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 건들이지 말자
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // <--

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TextView times = (TextView) findViewById(R.id.Time);


                Calendar cal = Calendar.getInstance();

                // 메인하단 버튼
                feed_btn = (TextView) findViewById(R.id.feed_btn);
                cal_btn = (TextView) findViewById(R.id.cal_btn);
                memo_btn = (TextView) findViewById(R.id.memo_btn);
                diary_btn = (TextView) findViewById(R.id.diary_btn);

                linear_main = (LinearLayout) findViewById(R.id.linear_main);


                int month = cal.get(Calendar.MONTH) + 1;
                int date = cal.get(Calendar.DATE);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);

                SimpleDateFormat AmPm = new SimpleDateFormat("a", Locale.US);

                Date today = new Date();
                String AmPmString = AmPm.format(today);

                String korDayOfWeek = null;
                switch (dayOfWeek) {
                    case 1:
                        korDayOfWeek = "SUN";
                        break;
                    case 2:
                        korDayOfWeek = "MON";
                        break;
                    case 3:
                        korDayOfWeek = "TUE";
                        break;
                    case 4:
                        korDayOfWeek = "WED";
                        break;
                    case 5:
                        korDayOfWeek = "THU";
                        break;
                    case 6:
                        korDayOfWeek = "FRI";
                        break;
                    case 7:
                        korDayOfWeek = "SAT";
                        break;
                    default:
                        break;
                }
                times.setText(month + "/" + date + "  " + korDayOfWeek + "  " + hour + ":" + minute + "  " + AmPmString);


                TextView textView = (TextView) findViewById(R.id.Temperature);
                TextView textView1 = (TextView) findViewById(R.id.Finedust);
                TextView textView2 = (TextView) findViewById(R.id.Ozone);
                ImageView imageView = (ImageView) findViewById(R.id.WeatherImage);

                Bitmap bitmap1 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.sunrise);
                Bitmap bitmap2 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.sunny_cloud);
                Bitmap bitmap3 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.cloud);
                Bitmap bitmap4 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.cloud);
                Bitmap bitmap5 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.rain);
                Bitmap bitmap7 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.snow);
                Bitmap bitmap9 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.shower);
                Bitmap bitmap10 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.fog);
                Bitmap bitmap21 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.storm_rain);
                Bitmap bitmap22 = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.dust_stom);


                WeatherConnection weatherConnection = new WeatherConnection();
                FinedustConnection finedustConnection = new FinedustConnection();
                OzoneConnection ozoneConnection = new OzoneConnection();


                AsyncTask<String, String, String> result1 = weatherConnection.execute("", "");
                AsyncTask<String, String, String> result2 = finedustConnection.execute("", "");
                AsyncTask<String, String, String> result3 = ozoneConnection.execute("", "");

                try {
                    String msg1 = result1.get();
                    String msg2 = result2.get();
                    String msg3 = result3.get();


                    if (msg1.matches(".*맑음.*")) {
                        linear_main.setBackgroundResource(R.drawable.bg_sunrise);
                        imageView.setImageBitmap(bitmap1);
                    } else if (msg1.matches(".*구름조금*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_cloudy);
                        imageView.setImageBitmap(bitmap3);
                    } else if (msg1.matches(".*구름많음.*")) {
                        linear_main.setBackgroundResource(R.drawable.bg_cloudy);
                        imageView.setImageBitmap(bitmap21);
                    } else if (msg1.matches(".*흐림*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_cloudy);
                        imageView.setImageBitmap(bitmap2);
                    } else if (msg1.matches(".*비*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_rain);
                        imageView.setImageBitmap(bitmap4);

                    } else if (msg1.matches(".*눈*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_snow);
                        imageView.setImageBitmap(bitmap5);

                    } else if (msg1.matches(".*소나기*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_rain);
                        imageView.setImageBitmap(bitmap7);

                    } else if (msg1.matches(".*안개*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_cloudy);
                        imageView.setImageBitmap(bitmap9);

                    } else if (msg1.matches(".*뇌우*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_rain);
                        imageView.setImageBitmap(bitmap10);
                    } else if (msg1.matches(".*황사*.")) {
                        linear_main.setBackgroundResource(R.drawable.bg_dust_storm);
                        imageView.setImageBitmap(bitmap22);

                    } else
                        System.out.println("error");

                    textView.setText(msg1.toString());
                    textView1.setText("미세먼지   " + msg2.toString());
                    textView2.setText("오존          " + msg3.toString());


                } catch (Exception e) {

                }



        }
    };
    class NewRunnable implements Runnable{
        @Override
        public void run() {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace() ;
                }

                mHandler.sendEmptyMessage(0) ;
            }
        }
    }

    NewRunnable nr = new NewRunnable() ;
    Thread t = new Thread(nr) ;
        t.start() ;
    }

    // 네트워크 작업은 AsyncTask 를 사용해야 한다
    public class WeatherConnection extends AsyncTask<String, String, String>{

        // 백그라운드에서 작업하게 한다
        @Override
        protected String doInBackground(String... params) {


            // Jsoup을 이용한 날씨데이터 Pasing하기.
            try{

                String path = "https://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09290555";

                Document document = Jsoup.connect(path).get();

                Elements elements = document.select("em");

                System.out.println(elements);

                Element targetElement = elements.get(2);

                String text = targetElement.text();

                System.out.println(text);

                return text;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class FinedustConnection extends AsyncTask<String, String, String>{

        // 백그라운드에서 작업하게 한다
        @Override
        protected String doInBackground(String... params) {


            // Jsoup을 이용한 날씨데이터 Pasing하기.
            try{

                String path = "https://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09290555";

                Document document = Jsoup.connect(path).get();

                Elements elements = document.select("em");

                System.out.println(elements);

                Element targetElement = elements.get(3);

                String text = targetElement.text();

                System.out.println(text);

                return text;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public class OzoneConnection extends AsyncTask<String, String, String>{

        // 백그라운드에서 작업하게 한다
        @Override
        protected String doInBackground(String... params) {


            // Jsoup을 이용한 날씨데이터 Pasing하기.
            try{

                String path = "https://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09290555";

                Document document = Jsoup.connect(path).get();

                Elements elements = document.select("em");

                System.out.println(elements);

                Element targetElement = elements.get(4);

                String text = targetElement.text();

                System.out.println(text);

                return text;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void onClick(View view) {
;

        switch (view.getId()) {
            case R.id.feed_btn: { // 피드 버튼 눌렀을때
                Intent intent = new Intent(MainActivity.this, StoryActivity.class);

                startActivityForResult(intent,1);
                break;
            }
            case R.id.cal_btn: { // 캘린더 버튼 눌렀을때
                Intent intent = new Intent(MainActivity.this, PapayaCalendar.class);
                startActivityForResult(intent,1);
                break;
            }
            case R.id.memo_btn: { // 메모 버튼 눌렀을때
                Intent intent = new Intent(MainActivity.this, MemoMain.class);

                startActivityForResult(intent,1);
                break;
            }
            case R.id.diary_btn: { // 다이어리 버튼 눌렀을때
               // MapsActivity
                Intent intent = new Intent(MainActivity.this, DiaryMainActivity.class);

                startActivityForResult(intent,1);
                break;
            }
        }
    }

}