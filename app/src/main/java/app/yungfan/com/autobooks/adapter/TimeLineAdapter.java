package app.yungfan.com.autobooks.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

import app.yungfan.com.autobooks.R;
import app.yungfan.com.autobooks.activity.AddActivity;
import app.yungfan.com.autobooks.activity.MainActivity;
import app.yungfan.com.autobooks.model.Books;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 主界面ListView的Adapter  加入AndroidSwipeLayout  继承BaseSwipeAdapter
 */
public class TimeLineAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<Books> mBooks;

    public TimeLineAdapter(Context context, List<Books> list) {
        super();
        this.mContext = context;
        this.mBooks = list;
    }

    @Override
    public int getCount() {
        if (mBooks != null) {
            return mBooks.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mBooks != null) {
            return mBooks.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 返回swipe布局的id
     *
     * @param position
     * @return
     */
    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    /**
     * item的布局
     *
     * @param position
     * @param viewGroup
     * @return
     */
    @Override
    public View generateView(final int position, ViewGroup viewGroup) {

        View convertView = LayoutInflater.from(mContext).inflate(R.layout.timeline_item, null);

        final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(position));
        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("您确定要删除该记录吗?")
                        .setConfirmText("确定")
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                swipeLayout.close();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                //修改数据源 刷新列表 但删除数据后 mList已经发生了变化 position已经变化了 按如下方法只能删除一次 后面会失败
//                                mBooks.remove(position);
//                                notifyDataSetChanged();


                                Books.delete(mBooks.get(position));

                                mBooks.clear();
                                mBooks = ((MainActivity) mContext).getAll();
                                notifyDataSetChanged();
                                sweetAlertDialog.cancel();
                                swipeLayout.close();

                            }
                        })
                        .show();


            }
        });


        //更新记录  跳转到Add界面 两个界面一样
        convertView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(mContext, AddActivity.class);

                //只传一个id过去
                // 用Serializable id会在Intent传输中丢失 用Parcelable麻烦

                Bundle bundle = new Bundle();
                bundle.putLong("book_id", mBooks.get(position).getId());
                intent.putExtras(bundle);

                mContext.startActivity(intent);


            }
        });


        // 长按显示详情对话框
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                showDetails(mBooks.get(position));
                return false;
            }
        });

        return convertView;
    }


    /**
     * 点击item查看详情
     *
     * @param mBooks
     */
    private void showDetails(Books mBooks) {


        View dialogView = LayoutInflater.from(mContext.getApplicationContext()).inflate(R.layout.activity_details, null);


        TextView details_date, details_event, details_pay, details_cost, details_gls, details_bz;
        details_date = (TextView) dialogView.findViewById(R.id.details_date);
        details_event = (TextView) dialogView.findViewById(R.id.details_event);
        details_pay = (TextView) dialogView.findViewById(R.id.details_pay);
        details_cost = (TextView) dialogView.findViewById(R.id.details_cost);
        details_gls = (TextView) dialogView.findViewById(R.id.details_gls);
        details_bz = (TextView) dialogView.findViewById(R.id.details_bz);


        details_date.setText("消费时间：" + mBooks.getYear() + "/" + mBooks.getMonth() + "/" + mBooks.getDay());
        details_event.setText("消费项目：" + mBooks.getXmmc());
        details_pay.setText("付款方式：" + mBooks.getFkfs());
        details_cost.setText("消费金额：" + mBooks.getXfje() + "元");


        String gls = TextUtils.isEmpty(mBooks.getGls()) ? "您没有填写" : mBooks.getGls();
        String bz = TextUtils.isEmpty(mBooks.getBz()) ? "您没有填写" : mBooks.getBz();
        details_gls.setText("消费公里：" + gls);
        details_bz.setText("消费备注：" + bz);


        //显示对话框
        boolean wrapInScrollView = true;
        new MaterialDialog.Builder(mContext)
                .backgroundColor(Color.parseColor("#009688"))
                .title(R.string.details)
                .titleColor(Color.parseColor("#ffffff"))
                .customView(dialogView, wrapInScrollView)
                .show();

    }

    /**
     * 填充数据
     *
     * @param position
     * @param convertView
     */
    @Override
    public void fillValues(int position, View convertView) {
        ViewHold hold;
        hold = new ViewHold();
        /**
         * 找控件
         */
        hold.event = (TextView) convertView.findViewById(R.id.event);
        hold.date = (TextView) convertView.findViewById(R.id.date);
        hold.cost = (TextView) convertView.findViewById(R.id.cost);
        hold.money = (TextView) convertView.findViewById(R.id.money);


        /**
         * 设置控件的值
         */
        hold.event.setText(mBooks.get(position).getXmmc());
        hold.date.setText(mBooks.get(position).getYear() + "/" + mBooks.get(position).getMonth() + "/" + mBooks.get(position).getDay());
        hold.cost.setText(mBooks.get(position).getFkfs());
        hold.money.setText(mBooks.get(position).getXfje() + "元");
    }


    static class ViewHold {
        public TextView event, date, cost, money;
    }

}
