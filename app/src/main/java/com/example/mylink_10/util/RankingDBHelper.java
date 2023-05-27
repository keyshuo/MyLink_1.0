package com.example.mylink_10.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RankingDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ranking.db";
    private static final String TABLE_NAME = "tb_ranking";
    private static final int DB_VERSION = 1;
    private static RankingDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    /**
     * 获取数据库帮助器单例
     * @param context   活动上下文
     * @return          数据库帮助器单例
     */
    public static RankingDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new RankingDBHelper(context);
        }
        return mHelper;
    }

    /**
     * 构造方法
     * @param context   活动上下文
     */
    private RankingDBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    /**
     * 打开数据库读链接
     * @return
     */
    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    /**
     * 获取数据库写链接
     * @return
     */
    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getReadableDatabase();
        }
        return mWDB;
    }

    /**
     * 关闭数据库链接
     */
    public void closeLink() {
        //关闭读链接
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }
        //关闭写链接
        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
