package app.yungfan.com.autobooks.app;

import android.app.Application;

import com.orm.SugarContext;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by yangfan on 2016/4/27.
 */
public class AutoBooksApp extends Application {


    private static AutoBooksApp instance;


    public AutoBooksApp() {
    }

    // 单例模式获取唯一的AutoBooksApp实例
    public static AutoBooksApp getInstance() {
        if (null == instance) {
            instance = new AutoBooksApp();
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);
        LeakCanary.install(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        SugarContext.terminate();


    }


}
