package com.example.papaya;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class Tab_Pager_Adapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public Tab_Pager_Adapter(FragmentManager fm, int tabCount) {
        super(fm, tabCount);
        this.tabCount = tabCount;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                DiaryMainActivity.Diary_Write diaryWrite = new DiaryMainActivity.Diary_Write();
                return diaryWrite;
            case 2:

                DiaryMainActivity.Diary_List diaryList = new DiaryMainActivity.Diary_List();
                return diaryList;
            case 0:
                DiaryMainActivity.Diary_Setting diarySetting = new DiaryMainActivity.Diary_Setting();
                return diarySetting;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}


