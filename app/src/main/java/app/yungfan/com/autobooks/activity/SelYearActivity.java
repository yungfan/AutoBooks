package app.yungfan.com.autobooks.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.yungfan.com.autobooks.R;

/**
 * Created by yangfan on 2016/4/29.
 * 构造一个弹出式的Activity 选择年份
 */
public class SelYearActivity extends Activity {

    private List<String> years = null;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selyear);
        initData();
        initViews();

    }

    private void initViews() {

        mListView = (ListView) findViewById(R.id.years);
        //设置空视图
        mListView.setEmptyView(findViewById(R.id.empty));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelYearActivity.this, R.layout.selyear_item, R.id.year_item, years);

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //将选择的年份回传至上个界面  并让本界面消失
                Intent intent = new Intent();

                intent.putExtra("selYear", years.get(i));

                setResult(RESULT_OK, intent);

                onBackPressed();

            }
        });

    }


    private void initData() {

        //原生数据库进行打开数据库操作
        SQLiteDatabase database = openOrCreateDatabase("books.db", Context.MODE_PRIVATE, null);

        //获取年份做下拉列表
        years = new ArrayList<>();
        Cursor cursor = database.rawQuery("select distinct year as totalYear from books order by year desc", null);


        while (cursor.moveToNext())

        {
            //将月份保存金属组
            years.add(cursor.getString(cursor.getColumnIndex("totalYear")));

        }

        //关闭游标和数据库

        if (cursor != null) {
            cursor.close();
        }
        if (database != null) {
            database.close();
        }


    }
}