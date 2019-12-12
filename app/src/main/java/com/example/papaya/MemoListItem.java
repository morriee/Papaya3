package com.example.papaya;



public class MemoListItem {

    private String[] mData;

    private String mId;

    private boolean mSelectable = true;

    public MemoListItem(String itemId, String[] obj) {
        mId = itemId;
        mData = obj;
    }

    public MemoListItem(String memoId, String memoDate, String memoText,
                        String id_photo, String uri_photo,String gpsText)
    {
        mId = memoId;
        mData = new String[5];
        mData[0] = memoDate;
        mData[1] = memoText;
        mData[2] = id_photo;
        mData[3] = uri_photo;

        mData[4] = gpsText;
    }


    public boolean isSelectable() {
        return mSelectable;
    }


    public void setSelectable(boolean selectable) {
        mSelectable = selectable;
    }

    public String getId() {
        return mId;
    }

    public void setId(String itemId) {
        mId = itemId;
    }



    public String[] getData() {
        return mData;
    }


    public String getData(int index) {
        if (mData == null || index >= mData.length) {
            return null;
        }

        return mData[index];
    }


    public void setData(String[] obj) {
        mData = obj;
    }



    public int compareTo(MemoListItem other) {
        if (mData != null) {
            Object[] otherData = other.getData();
            if (mData.length == otherData.length) {
                for (int i = 0; i < mData.length; i++) {
                    if (!mData[i].equals(otherData[i])) {
                        return -1;
                    }
                }
            } else {
                return -1;
            }
        } else {
            throw new IllegalArgumentException();
        }

        return 0;
    }



}
