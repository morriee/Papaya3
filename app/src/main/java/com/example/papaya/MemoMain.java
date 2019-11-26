package com.example.papaya;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class MemoMain extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView textView;
    private TextView textView2;
    private Realm realm;
    private RecyclerView rcv;
    private RcvAdapter rcvAdapter;
    private Memo memo_Main;
    public List<Memo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_memo);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView = findViewById(R.id.mTitleTextView);
        textView2 = findViewById(R.id.mContentTextView);
        rcv = findViewById(R.id.rcvMain);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        rcv.setLayoutManager(linearLayoutManager);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        RealmResults<Memo> realmResults = realm.where(Memo.class)
                .findAllAsync();

        for(Memo memo : realmResults) {
            list.add(new Memo(memo.getTitle(),memo.getContent()));
            rcvAdapter = new RcvAdapter(MemoMain.this,list);
            rcv.setAdapter(rcvAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_button, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { // 뒤로가기 버튼 눌렀을 때
                finish();
                return true;
            }
            case R.id.toolbar_button: { // 오른쪽 상단 버튼 눌렀을 때
                Intent addActivity = new Intent(MemoMain.this,AddActivity.class);
                startActivityForResult(addActivity,1);

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK) {

            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");

            realm.beginTransaction();
            memo_Main = realm.createObject(Memo.class);
            memo_Main.setTitle(title);
            memo_Main.setContent(content);

            realm.commitTransaction();

            RealmResults<Memo> realmResults1 = realm.where(Memo.class)
                    .equalTo("title",title)
                    .findAllAsync();
            RealmResults<Memo> realmResults2 = realm.where(Memo.class)
                    .equalTo("content",content)
                    .findAllAsync();

            list.add(new Memo(title,content));
            rcvAdapter = new RcvAdapter(MemoMain.this,list);
            rcv.setAdapter(rcvAdapter);

        }
    }
}