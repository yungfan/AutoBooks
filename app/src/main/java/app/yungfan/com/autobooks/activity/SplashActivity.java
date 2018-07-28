package app.yungfan.com.autobooks.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

import app.yungfan.com.autobooks.R;
import app.yungfan.com.autobooks.utils.MyUtils;

/**
 * Created by yangfan on 2016/5/17.
 */
public class SplashActivity extends Activity {
    private static final int SHOW_TIME_MIN = 3000;// 最小显示时间
    private long mStartTime;// 开始时间
    //数据库
    private SQLiteDatabase db;

    private Handler mHandler = new Handler();

//    private TextView timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mStartTime = System.currentTimeMillis();//记录开始时间，

//        timer = (TextView) findViewById(R.id.timer);
//
//
//        new CountDownTimer(3000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                timer.setText("" + millisUntilFinished / 1000 + "s");
//            }
//
//            public void onFinish() {
//                timer.setVisibility(View.GONE);
//            }
//        }.start();

        checkTable();
    }

    /**
     * 判断表是否存在
     */
    private void checkTable() {

        boolean isNew = MyUtils.isNewVersion(SplashActivity.this);

        //第一次安装新版本 需要判断表存不存在
        if (isNew) {


            // MyUtils.showPrompt(SplashActivity.this, "新版").show();

            db = openOrCreateDatabase("books.db", Context.MODE_PRIVATE, null);
            //表格不存在 注意这里区分大小写
            if (!tableIsExist("BOOKS")) {

                //创造一个表格  注意 字段区分大小写
                db.execSQL("CREATE TABLE BOOKS (ID INTEGER PRIMARY KEY AUTOINCREMENT , BZ TEXT, FKFS TEXT, GLS TEXT, XFJE TEXT, XMMC TEXT, DAY INTEGER, MONTH INTEGER, YEAR INTEGER )");

            }

        }


        // 计算一下总共花费的时间
        long loadingTime = System.currentTimeMillis() - mStartTime;
        // 如果比最小显示时间还短，就延时进入MainActivity，否则直接进入
        if (loadingTime < SHOW_TIME_MIN) {
            mHandler.postDelayed(goToMainActivity, SHOW_TIME_MIN
                    - loadingTime);
        } else {
            mHandler.post(goToMainActivity);
        }

    }

    //进入下一个Activity
    private Runnable goToMainActivity = new Runnable() {

        @Override
        public void run() {
            startActivity(new Intent(SplashActivity.this,
                    MainActivity.class));
            SplashActivity.this.finish();
        }
    };


    /**
     * 判断表存不存在
     *
     * @param tableName
     * @return
     */
    private boolean tableIsExist(String tableName) {
        boolean result = false;

        Cursor cursor = null;
        try {

            String sql = "SELECT COUNT(*) FROM sqlite_master where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

}
