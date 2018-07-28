package app.yungfan.com.autobooks.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import app.yungfan.com.autobooks.R;
import app.yungfan.com.autobooks.adapter.TimeLineAdapter;
import app.yungfan.com.autobooks.model.Books;
import app.yungfan.com.autobooks.utils.MyUtils;
import app.yungfan.com.autobooks.utils.SDCardUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    //ToolBar
    private Toolbar mToolbar;
    //展示数据的列表
    private ListView mListView;
    //ListView填充Model
    private List<Books> mBooks = new ArrayList<>();
    //时间轴Adapter
    private TimeLineAdapter mAdapter;
    //刷新控件
    private PtrClassicFrameLayout ptrClassicFrameLayout;

    /**
     * 两次返回键退出
     */
    // 弹出提示框
    private Toast toast;
    // 记录第一次按下的时间
    private long firstPressTime = -1;
    // 记录第二次按下的时间
    private long lastPressTime;
    // 两次按下的时间间隔
    private final long INTERVAL = 2000;

    //分页查询的是第几组
    private int index = 0;

    //导出文件
    private String path = SDCardUtils.getSDCardPath() + "爱车助手_导出.json";

    //加载更多时需要
    static Handler handler = new Handler();

    /**
     * 读写文件
     */
    private InputStreamReader ifr;
    private OutputStreamWriter ofw;
    private int len = 0;
    //导入时的JSON字符串
    private String mJson;


    //导入MenuItem的按钮能不能用
    public boolean canImport = false;
    //选项菜单
    private Menu optionsMenu;

    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0x11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNewVersion();
        initToolBar();
        initViews();


    }


    /**
     * 判断是不是新版本 此方法只运行一次
     * 第一次查看数据库 为了判断是否有数据
     */
    private void checkNewVersion() {


        //查询数据总数
        long dataCount = Books.count(Books.class);

        /**
         * 表格有但无数据
         */
        if (dataCount == 0) {

            showSweetAlertDialog();

        } else {
            adjustMenu();
        }


    }


    /**
     * 显示提醒SweetAlertDialog
     */
    private void showSweetAlertDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("无数据，如果您备份过数据，请务必在添加前先导入，否则可能会恢复失败。")
                .setTitleText("温馨提醒")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        sweetAlertDialog.cancel();
                        canImport = true;
                        adjustMenu();
                    }
                })
                .show();
    }

    /**
     * 因为从上个界面回来的时候 必须要刷新数据 所以放到这里执行
     */
    @Override
    protected void onResume() {
        super.onResume();


        //只要是重新进入这个界面重新刷新
        index = 0;

        //第二次查询所有 填充数据
        mBooks = getAll();


        //一旦有数据 就不可以导入了
        if (mBooks.size() > 0) {

            //大于14条就有加载更多
            if (mBooks.size() > 14) {
                ptrClassicFrameLayout.setLoadMoreEnable(true);
            }

        }

        mAdapter = new TimeLineAdapter(MainActivity.this, mBooks);

        mListView.setAdapter(mAdapter);

        //一次只能出现一个侧滑
        mAdapter.setMode(Attributes.Mode.Single);
    }

    /**
     * 初始化控件
     */

    private void initViews() {


        toast = MyUtils.showTip(MainActivity.this, "再按一次退出程序");

        mListView = (ListView) findViewById(R.id.listbooks);

        //下拉刷新的组件
        ptrClassicFrameLayout = (PtrClassicFrameLayout) this.findViewById(R.id.main_ptr);


        ptrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            //禁止下拉刷新
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {


            }
        });

        //下拉加载更多
        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //索引++，继续上次加载的数据后面加载数据
                        index++;

                        //第四次查询
                        mBooks.addAll(getAll());

                        mAdapter.notifyDataSetChanged();


                        ptrClassicFrameLayout.loadMoreComplete(true);
                    }
                }, 1500);
            }
        });

    }


    /**
     * 初始化ToolBar
     */
    private void initToolBar() {

        mToolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(mToolbar);
        //通过toolbar.setTitle(“主标题”);设置Toolbar的标题，你必须在调用它之前调用如下代码
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(R.string.main);
        mToolbar.setTitleTextColor(Color.WHITE);


    }


    /**
     * 创建菜单  ToolBar上面的菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    /**
     * ToolBar菜单监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemId = item.getItemId();
        //添加
        if (menuItemId == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, AddActivity.class));

        } else if (menuItemId == R.id.action_total) {
            //统计
            startActivity(new Intent(MainActivity.this, StaticsActivity.class));

        } else if (menuItemId == R.id.action_export) {

            //导出至SD卡

            //Android 6.0 写入之前检查是否有WRITE_EXTERNAL_STORAGE权限，没有则申请权限

            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            } else {

                exportToSDCard();
            }

        } else if (menuItemId == R.id.action_import) {

            //从SD卡导入
            importFromSDCard();

        }
//        else if (menuItemId == R.id.action_oil) {
//
//            //查询油价
//            startActivity(new Intent(MainActivity.this, OilPriceActivity.class));
//
//        }

        else if (menuItemId == R.id.action_about) {
            //关于
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 6.0以后申请权限 用户选择允许或拒绝后，会回调onRequestPermissionsResult方法, 该方法类似于onActivityResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户允许
                exportToSDCard();
            } else {
                // 用户拒绝
                MyUtils.showTip(MainActivity.this, " 您没有权限读写SD卡 ").show();

            }
        }
    }


    /**
     * onPrepareOptionsMenu是每次在display Menu之前，都会去调用，只要按一次Menu按鍵，就会调用一次。所以可以在这里动态的改变menu。
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);

        optionsMenu = menu;

        MenuItem item = menu.findItem(R.id.action_import);
        //导入按钮默认不能点 只有没有数据的时候可以点(没有表格、没有数据、滑动删除至没有数据)
        //注意设置按钮的颜色变化方式 非常巧妙
        if (!canImport) {
            //     MyUtils.showTip(MainActivity.this, " 移除 ").show();
            item.setEnabled(false);
            item.setTitle(Html.fromHtml("<font color='#C0C0C0'>从SD卡导入</font>"));
        } else {
            //    MyUtils.showTip(MainActivity.this, " 添加 ").show();
            item.setEnabled(true);
            item.setTitle(Html.fromHtml("<font color='#009688'>从SD卡导入</font>"));
        }
        return true;
    }

    /**
     * 从sd卡导入
     */

    private void importFromSDCard() {

        //导入数据
        if (SDCardUtils.isSDCardEnable()) {

            //先看有没有数据
            final File myFile = new File(path);
            if (!myFile.exists()) {
                MyUtils.showTip(MainActivity.this, "您没有备份数据").show();

            } else {

                final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);

                pDialog.getProgressHelper().setBarColor(R.color.material_green);
                pDialog.setTitleText("正在导入");
                pDialog.setCancelable(false);
                pDialog.show();

                final char[] data = new char[1024];
                try {
                    ifr = new InputStreamReader(new FileInputStream(path), "UTF-8");
                    //读取数据
                    len = ifr.read(data);
                    String json = new String(data, 0, len);

                    //将数据传出去
                    mJson = json;

                } catch (IOException e) {

                    e.printStackTrace();
                    MyUtils.showTip(MainActivity.this, "导入失败").show();

                } finally {

                    try {
                        if (ifr != null) {
                            ifr.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


                //对话框显示时长
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        pDialog.cancel();
                        //提醒导入成功
                        mAdapter.notifyDataSetChanged();
                        MyUtils.showPrompt(MainActivity.this, "导入成功").show();

                    }
                }, 800);


                //这个导入 只能是刚进来无数据的时候导入 以后就不能再次导入 因为如果先添加 再次导入的话 会出现id重复 就会认为是同一条记录 要么覆盖 要么导不进来
                if (!TextUtils.isEmpty(mJson)) {

                    //先清空所有数据
                    //Books.deleteAll(Books.class);

                    //将json解析出来 转换成List
                    Gson gson = new Gson();

                    List<Books> books = gson.fromJson(mJson, new TypeToken<List<Books>>() {
                    }.getType());


                    //刷新listView 第三次查询
//                    mBooks.clear();
//                    mBooks.addAll(getAll());
                    mBooks.clear();
                    mBooks.addAll(books);


                    //导完以后菜单不可用
                    canImport = false;
                    adjustMenu();

                    //保存到数据库
                    for (Books book : books) {
                        Books.save(book);
                    }


                }

            }

        } else {
            MyUtils.showTip(MainActivity.this, "内存卡不可用").show();
        }
    }

    /**
     * 导出至sd卡
     */

    private void exportToSDCard() {

        //导出数据至JSON
        List<Books> books = Books.findWithQuery(Books.class, "select * from books");

        if (books.size() == 0) {
            MyUtils.showTip(MainActivity.this, " 您没有数据可以备份 ").show();
        } else {

            Gson gson = new Gson();
            final String data = gson.toJson(books);

            if (SDCardUtils.isSDCardEnable()) {
//                        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

                //对话框提示
                final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);


                pDialog.getProgressHelper().setBarColor(R.color.material_green);
                pDialog.setTitleText("正在导出");
                pDialog.setCancelable(false);
                pDialog.show();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        try {

                            ofw = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
                            ofw.write(data);

                        } catch (IOException e) {

                            e.printStackTrace();
                            MyUtils.showTip(MainActivity.this, "导出失败").show();

                        } finally {

                            pDialog.cancel();
                            //提醒导出成功
                            MyUtils.showPrompt(MainActivity.this, "导出成功").show();
                            try {

                                if (ofw != null) {
                                    ofw.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, 800);


            } else {
                MyUtils.showTip(MainActivity.this, "内存卡不可用").show();
            }
        }
    }

    /**
     * 获取所有信息  Adapter要用 所以设为public
     *
     * @return
     */
    public List getAll() {


        //offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
        //执行完词句 没有表格也会帮我们创建一个表 名字为BOOKS
        List<Books> books = Books.findWithQuery(Books.class, "select * from BOOKS order by YEAR desc, MONTH desc, DAY desc limit 15 offset ?", index * 15 + "");
        return books;
    }


    /**
     * 调整菜单
     */
    public void adjustMenu()

    {
        if (optionsMenu != null) {
            onPrepareOptionsMenu(optionsMenu);
        }
    }


    /**
     * 以下是按下两次返回键退出程序
     * 按下返回键的时候调用
     */
    public void onBackPressed() {

        showQuitTips();
    }


    /**
     * 显示退出提示框
     */
    private void showQuitTips() {

        // 如果是第一次按下 直接提示
        if (firstPressTime == -1) {
            firstPressTime = System.currentTimeMillis();
            toast.show();

        }

        // 如果是第二次按下，需要判断与上一次按下的时间间隔，这里设置2秒
        else {

            lastPressTime = System.currentTimeMillis();
            if (lastPressTime - firstPressTime <= INTERVAL) {
                System.exit(0);
            } else {
                firstPressTime = lastPressTime;
                toast.show();
            }
        }
    }


}
