package app.yungfan.com.autobooks.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.yungfan.com.autobooks.R;


public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> data;
    private int lastPosition = -1; // 记录上一次选中的图片位置，-1表示未选中
    private ArrayList<Boolean> list = new ArrayList<Boolean>(); // 定义一个数组链表记录选中的状态
    private LayoutInflater mInflater;

    public MyAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.data = data;

        //刚开始默认都是未选中
        for (int i = 0; i < data.size(); i++) {
            list.add(false);
        }

        mInflater = LayoutInflater.from(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.choice_item, null);

            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_choice);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (!"".equals(data.get(position))) {
            viewHolder.title.setText(data.get(position));
        }
        if (list.get(position) == true) {
            viewHolder.title.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.choice_item_bg_selected));
        } else {
            viewHolder.title.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.choice_item_bg_default));
        }


        return convertView;
    }


    private class ViewHolder {
        private TextView title;
    }

    /**
     * 修改选中时的状态
     *
     * @param position
     */
    public void changeState(int position) {
        if (lastPosition != -1) {
            list.set(lastPosition, false);// 取消上一次的选中状态
        }
        list.set(position, !list.get(position));// 设置这一次的选中状态
        lastPosition = position; // 记录本次选中的位置
        notifyDataSetChanged(); // 通知适配器进行更新
    }
}
