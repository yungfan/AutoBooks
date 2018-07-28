package app.yungfan.com.autobooks.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import app.yungfan.com.autobooks.R;
import app.yungfan.com.autobooks.utils.AppUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 关于界面
 */
public class AboutActivity extends AppCompatActivity {

    //关于选项
    private String[] aboutItem = null;
    private Toolbar mToolbar;
    //上面是版本信息 下面是列表信息
    private TextView mVersionIntro;
    private ListView mAboutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolBar();
        initViews();
    }

    private void initViews() {

        aboutItem = getResources().getStringArray(R.array.aboutItem);

        mVersionIntro = (TextView) findViewById(R.id.versionintro);
        mVersionIntro.setText("版本 V" + AppUtils.getVersionName(AboutActivity.this));
        mAboutList = (ListView) findViewById(R.id.aboutlist);


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(AboutActivity.this, R.layout.about_item, R.id.abouttext, aboutItem);
        mAboutList.setAdapter(adapter);

        mAboutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (0 == i) {
                    new SweetAlertDialog(AboutActivity.this)
                            .setTitleText("作者简介")
                            .setContentText(getResources().getString(R.string.author))
                            .setConfirmText(getResources().getString(R.string.choose))
                            .show();
                } else if (1 == i) {
                    //打开新浪微博
                    Uri url = Uri.parse(getResources().getString(R.string.weibo));
                    Intent it = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(it);

                } else if (2 == i) {
                    //打开简书首页
                    Uri url = Uri.parse(getResources().getString(R.string.jianshu));
                    Intent it = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(it);
                }
            }


        });
    }


    /**
     * 初始化ToolBar
     */
    private void initToolBar() {

        mToolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(mToolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle(R.string.about);
        mToolbar.setTitleTextColor(Color.WHITE);

    }


}