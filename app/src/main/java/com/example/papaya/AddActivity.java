package com.example.papaya;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.app.Activity.RESULT_OK;

public class AddActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText editText;
    private EditText editText2;
    private String title;
    private String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = findViewById(R.id.title_text);
        editText2 = findViewById(R.id.content_text);
        Button btn = findViewById(R.id.addFinish);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = editText.getText().toString();
                content = editText2.getText().toString();

                Intent add = new Intent();
                add.putExtra("title",title);
                add.putExtra("content",content);
                setResult(RESULT_OK,add);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // 뒤로가기 버튼 눌렀을 때
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}