package app.yungfan.com.autobooks.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.yungfan.com.autobooks.R;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by yangfan on 2016/4/28.
 */
public class StaticsActivity extends AppCompatActivity {

    //柱状图
    private ColumnChartView chart;
    //柱状图数据
    private ColumnChartData columnData;
    //默认查询的年份
    private String selYear = Calendar.getInstance().get(Calendar.YEAR) + "";
    //一年的金额  展示给用户
    private float totalYearMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statics);


        initViews();
    }

    private void initViews() {

        //柱状图
        chart = (ColumnChartView) findViewById(R.id.staticschart);

        //浮动按钮
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.yearsel_fab);

        actionButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                //去下个界面获取用户选择的年份
                startActivityForResult(new Intent(StaticsActivity.this, SelYearActivity.class), 0);
            }


        });


        //产生数据
        generateColumnData();
    }


    /**
     * 构造数据并填充表格显示
     */

    private void generateColumnData() {


        //横坐标的值
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        //柱形图的值
        List<SubcolumnValue> values;

        //柱形图对象 包装图形值
        List<Column> columns = new ArrayList<Column>();


        //原生数据库进行打开数据库操作
        SQLiteDatabase database = openOrCreateDatabase("books.db", Context.MODE_PRIVATE, null);

        //获取选择某一年以后的具体月份
        List<String> monthes = new ArrayList<>();

        //查询出有多少个月
        Cursor cursor = database.rawQuery("select distinct month as totalMonth from books where year = ? order by month ", new String[]{selYear});

        //获取有多少行数据
        int totalMonth = cursor.getCount();


        if (totalMonth != 0) {

            while (cursor.moveToNext()) {
                //将月份保存到数组
                monthes.add(cursor.getString(cursor.getColumnIndex("totalMonth")));

            }


            //统计查询每个月总共消费
            Cursor money = null;
            //每次初始化为0  因为每次选中year都会重新刷新图表
            totalYearMoney = 0;


            for (int i = 0; i < totalMonth; ++i) {

                //统计查询
                money = database.rawQuery("select sum(xfje) as totalMoney from books where year = ? and month = ?", new String[]{selYear, monthes.get(i) + ""});

                //游标移到数据行
                money.moveToNext();

                values = new ArrayList<SubcolumnValue>();

                //每个月的花费
                float monthMoney = Float.parseFloat(money.getString(money.getColumnIndex("totalMoney")));

                //统计一年所有的花费
                totalYearMoney += monthMoney;

                // 添加值
                values.add(new SubcolumnValue(monthMoney, ChartUtils.pickColor()));

                SubcolumnValue subcolumnValue = new SubcolumnValue(monthMoney, ChartUtils.pickColor());

                // 添加横坐标的值
                axisValues.add(new AxisValue(i).setLabel(monthes.get(i) + "月"));

                //点击柱形图 会提示数字
                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));

            }


            //关闭游标和数据库
            if (cursor != null) {
                cursor.close();
            }

            if (money != null) {
                money.close();
            }
            if (database != null) {
                database.close();
            }
        }

        columnData = new ColumnChartData(columns);


        //根据月数来动态调整柱体的大小

        /**
         * Set fill ration for columns, value from 0 to 1, 1 means that there will be almost no free space between columns,
         * 0 means that columns will have minimum width(2px).
         *
         * @param fillRatio
         * @return
         */
        if (totalMonth <= 2) {
            columnData.setFillRatio(0.1f);
        } else if (totalMonth <= 8) {
            columnData.setFillRatio(0.5f);
        } else {
            columnData.setFillRatio(0.8f);
        }

        //设置横纵坐标的值
        if (monthes.size() != 0) {
            //X
            columnData.setAxisXBottom(new Axis(axisValues).setName(selYear + "年 共消费 " + totalYearMoney + " 元" + "         月份").setTextColor(ChartUtils.pickColor()));
        } else {
            columnData.setAxisXBottom(new Axis().setName("月份").setTextColor(ChartUtils.pickColor()));
        }
        //Y
        columnData.setAxisYLeft(new Axis().setName("金额").setTextColor(ChartUtils.pickColor()));


        // 设置表格的数据
        chart.setColumnChartData(columnData);

        // 点击放大效果
        chart.setValueSelectionEnabled(true);

        //设置无缩放
        chart.setZoomEnabled(false);


    }


    /**
     * 获取用户选择的年份后重新构造图表数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {

            //获取年份以后刷新图表
            selYear = data.getStringExtra("selYear");

            generateColumnData();
        }
    }


    //当指定了android:configChanges="orientation"后,方向改变时onConfigurationChanged被调用
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }

}


