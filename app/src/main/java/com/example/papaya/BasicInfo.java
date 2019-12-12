package com.example.papaya;

import java.text.SimpleDateFormat;

public class BasicInfo {


        public static String ExternalPath = "/sdcard/";


        public static boolean ExternalChecked = false;


        public static String FOLDER_PHOTO 	= "Memo_Storage/photo/";


        public static String DATABASE_NAME = "Memo_Storage/memo.db";



        public static final String KEY_MEMO_MODE = "MEMO_MODE";
        public static final String KEY_MEMO_TEXT = "MEMO_TEXT";
        public static final String KEY_MEMO_ID = "MEMO_ID";
        public static final String KEY_MEMO_DATE = "MEMO_DATE";
        public static final String KEY_ID_PHOTO = "ID_PHOTO";
        public static final String KEY_URI_PHOTO = "URI_PHOTO";
        public static final String KEY_GPS_TEXT = "GPS_TEXT";


        public static final String MODE_INSERT = "MODE_INSERT";
        public static final String MODE_MODIFY = "MODE_MODIFY";
        public static final String MODE_VIEW = "MODE_VIEW";



        public static final int REQ_VIEW_ACTIVITY = 1001;
        public static final int REQ_INSERT_ACTIVITY = 1002;
        public static final int REQ_PHOTO_CAPTURE_ACTIVITY = 1501;
        //public static final int REQ_PHOTO_SELECTION_ACTIVITY = 1502;




        public static SimpleDateFormat dateDayNameFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        public static SimpleDateFormat dateDayFormat = new SimpleDateFormat("yyyy-MM-dd");



        public static final int WARNING_INSERT_SDCARD = 1001;
        public static final int IMAGE_CANNOT_BE_STORED = 1002;

        public static final int CONTENT_PHOTO = 2001;
        public static final int CONTENT_PHOTO_EX = 2005;

        public static final int CONFIRM_DELETE = 3001;

        public static final int CONFIRM_TEXT_INPUT = 3002;

        public static final int CONFIRM_GPS_INPUT = 3003;


}


