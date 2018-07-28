package app.yungfan.com.autobooks.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.novachevskyi.datepicker.CalendarDatePickerDialog;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import app.yungfan.com.autobooks.R;
import app.yungfan.com.autobooks.adapter.MyAdapter;
import app.yungfan.com.autobooks.model.Books;
import app.yungfan.com.autobooks.utils.MyUtils;

/**
 * Created by yangfan on 2016/4/27.
 */
public class AddActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button add, add_date, add_event, add_pay;
    private EditText add_cost, add_gls, add_bz;
    private int mYear, mMonth, mDay;

    //上个界面传过来的id和通过id查询的Books
    private long book_id;
    private Books mBooks;
    private boolean isUpdate = false;

    //资源转数组
    private List eventArray, payArray;

    //用户选择的单选对话框的位置
    private int eventSelected, paySelected;

    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        iSUpdate();
        initToolBar();
        initViews();
    }

    /**
     * 判断是不是更新操作
     */
    private void iSUpdate() {

        Bundle bundle = this.getIntent().getExtras();

        if (null != bundle) {
            isUpdate = true;
            book_id = getIntent().getLongExtra("book_id", 0);

            //通过id获取Books
            mBooks = Books.findById(Books.class, book_id);
        }


    }

    private void initViews() {

        eventArray = Arrays.asList(getResources().getStringArray(R.array.event));
        payArray = Arrays.asList(getResources().getStringArray(R.array.pay));
        eventSelected = isUpdate ? eventArray.indexOf(mBooks.getXmmc()) : eventArray.indexOf(eventArray.get(0));
        paySelected = isUpdate ? payArray.indexOf(mBooks.getFkfs()) : payArray.indexOf(payArray.get(0));


        add_date = (Button) findViewById(R.id.add_date);
        add_event = (Button) findViewById(R.id.add_event);
        add_pay = (Button) findViewById(R.id.add_pay);
        add_cost = (EditText) findViewById(R.id.add_cost);
        add_gls = (EditText) findViewById(R.id.add_gls);
        add_bz = (EditText) findViewById(R.id.add_bz);
        add = (Button) findViewById(R.id.add);


        //项目类型设置默认值后设置监听器
        add_event.setText(R.string.event_default);

        add_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new MaterialDialog.Builder(AddActivity.this)
//                        .title(R.string.event_title)
//                        .titleColor(Color.parseColor("#009688"))
//                        .items(R.array.event)
//                        .itemsCallbackSingleChoice(eventSelected, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                                add_event.setText(text);
//                                eventSelected = which;
//                                return true;
//                            }
//                        })
//                        .show();

                alertDialog = new AlertDialog.Builder(AddActivity.this)
                        .setView(getChoiceView(1))
                        .create();

                alertDialog.show();


            }
        });

        //消费方式设置默认值后设置监听器
        add_pay.setText(R.string.pay_default);
        add_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new MaterialDialog.Builder(AddActivity.this)
//                        .title(R.string.pay_title)
//                        .titleColor(Color.parseColor("#009688"))
//                        .items(R.array.pay)
//                        .itemsCallbackSingleChoice(paySelected, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                                add_pay.setText(text);
//                                paySelected = which;
//                                return true;
//                            }
//                        })
//                        .show();

                alertDialog = new AlertDialog.Builder(AddActivity.this)
                        .setView(getChoiceView(2))
                        .create();

                alertDialog.show();
            }
        });


        //添加按钮设置监听器
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isUpdate) {

                    updateItem();

                } else {
                    addItem();
                }
            }
        });


        //设置时间
        if (isUpdate) {

            mYear = mBooks.getYear();
            mMonth = mBooks.getMonth();

            //显示的时间月数要少1 单独拿出来 不然容易出错
            final int showMonth = mBooks.getMonth() - 1;
            mDay = mBooks.getDay();

            add_date.setText(mYear + "/" + mMonth + "/" + mDay);

            //选择时间按钮设置监听器
            add_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final CalendarDatePickerDialog dialog =
                            CalendarDatePickerDialog.newInstance(dateSetListener, mYear, showMonth, mDay);

                    //设置时间选择区间
                    dialog.setYearRange(1970, mYear);

                    dialog.show(getSupportFragmentManager(), "DATE_PICKER_TAG");


                }
            });
        } else {
            Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH) + 1;
            mDay = c.get(Calendar.DATE);

            add_date.setText(mYear + "/" + mMonth + "/" + mDay);
            add_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final CalendarDatePickerDialog dialog =
                            CalendarDatePickerDialog.newInstance(dateSetListener, mYear, mMonth - 1, mDay);

                    //设置时间选择区间
                    dialog.setYearRange(1970, mYear);

                    dialog.show(getSupportFragmentManager(), "DATE_PICKER_TAG");


                }
            });
        }


        //填充数据
        if (isUpdate) {
            initData();

        }

    }


    private View getChoiceView(final int type) {

        View view = LayoutInflater.from(AddActivity.this).inflate(R.layout.dialog_choice, null);
        GridView gv = (GridView) view.findViewById(R.id.gv_reply);
        List<String> data;
        final MyAdapter adapter;
        if (type == 1) {
            data = Arrays.asList(getResources().getStringArray(R.array.event));
            adapter = new MyAdapter(this, data);
            gv.setAdapter(adapter);
            adapter.changeState(eventSelected);
        } else {
            data = Arrays.asList(getResources().getStringArray(R.array.pay));
            adapter = new MyAdapter(this, data);
            gv.setAdapter(adapter);
            adapter.changeState(paySelected);
        }


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (type == 1) {
                    eventSelected = position;
                    add_event.setText(eventArray.get(position).toString());


                } else {
                    paySelected = position;

                    add_pay.setText(payArray.get(position).toString());
                }
                alertDialog.dismiss();
                adapter.changeState(position);

            }
        });


        return view;
    }

    /**
     * 更新时调用
     */
    private void initData() {


        add_event.setText(mBooks.getXmmc());
        add_pay.setText(mBooks.getFkfs());
        add_cost.setText(mBooks.getXfje());
        add_gls.setText(mBooks.getGls());
        add_bz.setText(mBooks.getBz());

        add.setText("更新");


    }


    /**
     * 日期监听器
     */
    CalendarDatePickerDialog.OnDateSetListener dateSetListener =
            new CalendarDatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear,
                                      int dayOfMonth) {

                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;

                    add_date.setText(mYear + "/" + mMonth + "/" + mDay);
                }
            };

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {

        mToolbar = (Toolbar) findViewById(R.id.add_toolbar);
        setSupportActionBar(mToolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (isUpdate) {
            mToolbar.setTitle(R.string.update);
        } else {
            mToolbar.setTitle(R.string.add);
        }
        mToolbar.setTitleTextColor(Color.WHITE);

    }


    /**
     * 添加一条消费信息
     */
    private void addItem() {


        if (TextUtils.isEmpty(add_cost.getText())) {

            MyUtils.showTip(AddActivity.this, "请输入消费金额").show();
            return;
        }


        //创建Book对象
        Books books = new Books(add_event.getText().toString(), add_pay.getText().toString(), add_cost.getText().toString(), add_gls.getText().toString(), add_bz.getText().toString(), mYear, mMonth, mDay);
        long resultId = books.save();
        if (resultId > 0) {

            MyUtils.showPrompt(AddActivity.this, "添加成功").show();
            AddActivity.this.finish();
        }


    }


    /**
     * 更新一条消费信息
     */
    private void updateItem() {


        if (TextUtils.isEmpty(add_cost.getText())) {

            MyUtils.showTip(AddActivity.this, "请输入消费金额").show();
            return;
        }


        //更新Book对象
        mBooks.setXmmc(add_event.getText().toString());
        mBooks.setFkfs(add_pay.getText().toString());
        mBooks.setXfje(add_cost.getText().toString());
        mBooks.setBz(add_bz.getText().toString());
        mBooks.setGls(add_gls.getText().toString());
        mBooks.setYear(mYear);
        mBooks.setMonth(mMonth);
        mBooks.setDay(mDay);

        long resultId = mBooks.save();

        if (resultId > 0) {

            MyUtils.showPrompt(AddActivity.this, "更新成功").show();
            AddActivity.this.finish();
        }


    }


}