package app.yungfan.com.autobooks.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.yungfan.com.autobooks.R;

/**
 * Created by yangfan on 2016/4/30.
 */
public class MyUtils {

    public static Toast showTip(Context Context, String tip) {
        Toast toast = new Toast(Context);
        //toast_tips.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);


        View view = LayoutInflater.from(Context).inflate(R.layout.toast_tips, null);
        TextView tv = (TextView) view.findViewById(R.id.tips);
        tv.setText(tip);
        toast.setView(view);

        return toast;
    }


    public static Toast showPrompt(Context Context, String prompt) {
        Toast toast = new Toast(Context);
        //toast_tips.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);


        View view = LayoutInflater.from(Context).inflate(R.layout.toast_prompt, null);
        TextView tv = (TextView) view.findViewById(R.id.tips);
        tv.setText(prompt);
        toast.setView(view);

        return toast;
    }

    /**
     * 判断是不是第一次装
     *
     * @param context
     * @return
     */
    public static boolean isNewVersion(Context context) {

        float newVersionName = Float.parseFloat(AppUtils.getVersionName(context));
        float oldVersionName = (float) SPUtils.get(context, "versionName", new Float(1.0));

        if (newVersionName > oldVersionName) {


            SPUtils.put(context, "versionName", newVersionName);

            return true;
        }

        return false;

    }


}
