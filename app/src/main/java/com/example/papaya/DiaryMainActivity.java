package com.example.papaya;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
//import android.support.v7.widget.Toolbar;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
public class DiaryMainActivity extends AppCompatActivity {

    //메인 엑티비티 전역변수
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static ArrayList<Diary> data;
    public static DiaryDBHelper dbHelper;
    public static DiaryPassDBHelper pdbHelper;
    public static SQLiteDatabase db;


    //Tab_Write 전역변수
    public static EditText edit_title, edit_contents;
    public static Button save_btn;

    //Tab_List 전역변수
    public static Button refresh_btn;
    public static ListView list_diary;
    public static DiaryListAdapter listAdapter;

    //Tab_Setting 전역변수
    public static Switch secretSwitch;
    public static EditText secretEditText;
    public static Button secretSaveBtn;

    public static EditText checkPass;
    public static int flag = 0;
    public static int flag2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_main);

        //디비 핼퍼 객체 생성
        dbHelper = new DiaryDBHelper(this);
        pdbHelper = new DiaryPassDBHelper(this);
        data = new ArrayList<>();

        //커스텀 툴바의 리소스 연결
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        //액션바를 커스텀 툴바로 설정
        setSupportActionBar(toolbar);

        //커스텀 탭 레이아웃 리소스 연결
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //탭 레이아웃에 탭 추가
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_launcher_white_settings));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_launcher_white_create));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_launcher_white_visibility));


        //탭 배치 설정
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //커스텀 뷰페이지 리소스 연결
        viewPager = (ViewPager) findViewById(R.id.pager);

        //페이지 어답터 객체 생성
        Tab_Pager_Adapter pagerAdapter = new Tab_Pager_Adapter(getSupportFragmentManager(), tabLayout.getTabCount());

        //뷰 페이지 어답터 설정
        viewPager.setAdapter(pagerAdapter);

        //페이지에 변화가 생기면 ?
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //viewPager.addOnPageChangeListener();

        //탭 선택 이벤트
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }//end of onCreate -------------------------------------------------------

    //Diary_Write inner class
    public static class Diary_Write extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab_write, container, false);
            edit_title = (EditText) view.findViewById(R.id.edit_title_write);
            edit_contents = (EditText) view.findViewById(R.id.edit_contents_write);
            save_btn = (Button) view.findViewById(R.id.save_btn_write);
            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InsertDB();
                }
            });
            return view;
        }
    }

    //Diary_List inner class
    public static class Diary_List extends Fragment{

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.tab_list, container, false);
            flag = 1;
            refresh_btn = (Button) view.findViewById(R.id.refresh_btn_list);
            refresh_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(secretSwitch.isChecked()){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setMessage("비밀번호를 입력하세요");
                        View dialogview = inflater.inflate(R.layout.passwd, container, false);
                        alertDialog.setView(dialogview);
                        checkPass = (EditText)dialogview.findViewById(R.id.check_pass);

                        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cp = checkPass();
                                if(checkPass.getText().toString().equals(cp)){
                                    list_diary = (ListView) view.findViewById(R.id.list_diary);
                                    data = showDB();
                                    listAdapter = new DiaryListAdapter(getContext(), R.layout.list_layout, data);
                                    list_diary.setAdapter(listAdapter);
                                    list_diary.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            Intent intent = new Intent(getContext(), DiaryUpdate.class);
                                            int code = data.get(position).getCode();
                                            intent.putExtra("code", code);
                                            startActivity(intent);
                                        }
                                    });
                                    list_diary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                        @Override
                                        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                            alertDialog.setMessage(data.get(position).getTitle()+"을(를) 삭제하시겠습니까?");
                                            alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    int code = data.get(position).getCode();
                                                    deleteDB(code);
                                                    showDB();
                                                    listAdapter.notifyDataSetChanged();
                                                }
                                            });
                                            alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                            alertDialog.show();
                                            return false;
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    list_diary = (ListView) view.findViewById(R.id.list_diary);
                                    data = showDB();
                                    listAdapter = null;
                                    list_diary.setAdapter(listAdapter);
                                }
                            }
                        });
                        alertDialog.show();
                    }else{
                        list_diary = (ListView) view.findViewById(R.id.list_diary);
                        data = showDB();
                        listAdapter = new DiaryListAdapter(getContext(), R.layout.list_layout, data);
                        list_diary.setAdapter(listAdapter);
                        list_diary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getContext(), DiaryUpdate.class);
                                int code = data.get(position).getCode();
                                intent.putExtra("code", code);
                                startActivity(intent);
                            }
                        });
                        list_diary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                alertDialog.setMessage(data.get(position).getTitle()+"을(를) 삭제하시겠습니까?");
                                alertDialog.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int code = data.get(position).getCode();
                                        deleteDB(code);
                                        showDB();
                                        listAdapter.notifyDataSetChanged();
                                    }
                                });
                                alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertDialog.show();
                                return false;
                            }
                        });
                    }


                }
            });
            if(flag == 1){
                return view;
            }else{
                return null;
            }


        }
    }

    //환경설정 inner class
    public static class Diary_Setting extends Fragment{
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab_setting, container, false);
            secretSwitch = (Switch)view.findViewById(R.id.switch1);
            secretEditText = (EditText)view.findViewById(R.id.secret_editText);
            secretSaveBtn = (Button)view.findViewById(R.id.secret_save_btn);
            secretSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        //On
                        secretEditText.setVisibility(View.VISIBLE);

                    }else{
                        //Off
                        secretEditText.setVisibility(View.INVISIBLE);


                    }
                }
            });
            secretSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = secretEditText.getText().toString();
                    passDB(str);
                }
            });
            return view;
        }
    }


    //일기 쓰기 메소드
    public static void InsertDB(){
        db = dbHelper.getWritableDatabase();
        String sql = "insert into diary ('title', 'date', 'contents') values(?,?,?)";
        SQLiteStatement st = db.compileStatement(sql);
        st.bindString(1,edit_title.getText().toString());
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분 ss초");
        String writeTime = sdf.format(date);
        st.bindString(2,writeTime);
        st.bindString(3,edit_contents.getText().toString());
        st.execute();
        db.close();
        edit_title.setText("");
        edit_contents.setText("");
    }

    //일기 목록 보기 메소드
    public static ArrayList<Diary> showDB(){
        data.clear();
        db = dbHelper.getReadableDatabase();
        String sql = "select * from diary";
        Cursor cursor = db.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Diary diary = new Diary();
            diary.setCode(cursor.getInt(0));
            diary.setTitle(cursor.getString(1));
            diary.setDate(cursor.getString(2));
            diary.setContents(cursor.getString(3));
            data.add(diary);
        }
        cursor.close();
        db.close();
        return data;
    }

    //일기 삭제 메소드
    public static void deleteDB(int code){
        db = dbHelper.getReadableDatabase();
        String sql = "delete from diary where code="+code;
        db.execSQL(sql);
        db.close();
    }

    //비밀번호 입력 메소드
    public static void passDB(String str){
        db = pdbHelper.getWritableDatabase();
        String sql = "insert into passwd values('"+str+"')";
        db.execSQL(sql);
        db.close();
        secretEditText.setText("");
    }
    //비밀번호 확인 메소드
    public static String checkPass(){
        db = pdbHelper.getReadableDatabase();
        String sql = "select * from passwd";
        Cursor cursor = db.rawQuery(sql, null);
        String pass="";
        while(cursor.moveToNext()){
            pass = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pass;
    }

}


