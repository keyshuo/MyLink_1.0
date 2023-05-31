package com.example.mylink_10.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.mylink_10.R;
import com.example.mylink_10.util.ToastUtil;

import java.util.List;

public class MyCommunityAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityMSG> mMSGList;
    private RefreshListView refreshListView;

    public MyCommunityAdapter(Context mContext, List<CommunityMSG> mMSGList,RefreshListView callback) {
        this.mContext = mContext;
        this.mMSGList = mMSGList;
        this.refreshListView = callback;
    }

    @Override
    public int getCount() {
        return mMSGList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMSGList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyCommunityAdapter.ViewHolder holder;
        if(convertView == null)
        {
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.item_mycomments,null);
            holder = new MyCommunityAdapter.ViewHolder();
            holder.tv_name = convertView.findViewById(R.id.mycm_name);
            holder.tv_msg = convertView.findViewById(R.id.mycm_content);
            holder.tv_time = convertView.findViewById(R.id.mycm_time);
            holder.btn_comments = convertView.findViewById(R.id.mycm_btn_remarks);
            holder.btn_del = convertView.findViewById(R.id.mycm_btn_del);
            //将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        }
        else
        {
            holder = (MyCommunityAdapter.ViewHolder) convertView.getTag();
        }
        //给控件设置好数据
        CommunityMSG cmmsg = mMSGList.get(position);
        holder.tv_msg.setText(cmmsg.getMsg());
        holder.tv_name.setText(cmmsg.getName());
        holder.tv_time.setText(cmmsg.getTime());
        holder.btn_comments.setOnClickListener(v -> {
            //ToastUtil.show(mContext,"按钮被点击"+cmmsg.getName());
            Intent intent = new Intent(mContext, CommunityRemarksActivity.class);
            //intent.putExtra("item", (CharSequence) cmmsg);
            intent.putExtra("CommunityMSG", mMSGList.get(position));
            mContext.startActivity(intent);
        });
        holder.btn_del.setOnClickListener(v -> {
            //ToastUtil.show(mContext,"删除按钮被点击"+cmmsg.getName());
            // 创建一个SpannableString对象，用于设置标题的样式
            SpannableString spannableString = new SpannableString("删除动态");
            // 创建一个AlignmentSpan对象，用于设置文本对齐方式为居中
            AlignmentSpan alignmentSpan = new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER);
            // 设置标题文本样式为加粗
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // 将AlignmentSpan对象应用到标题文本
            spannableString.setSpan(alignmentSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(spannableString);
            builder.setMessage("确定要删除该动态吗？");
            builder.setPositiveButton("确定", (dialog, which) -> {
                // 调用activity接口实现删除数据并刷新页面
                Log.d("MyCommunityAdapter","开始删除信息");
                if(refreshListView !=null)
                {
                    refreshListView.onItemClicked(position);
                }
                Log.d("MyCommunityAdapter","删除成功");
                dialog.dismiss();
            });
            builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return convertView;
    }
    public interface RefreshListView {
        void onItemClicked(int position);
    }

    public final class ViewHolder{
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_msg;
        public Button btn_comments;
        public Button btn_del;
    }
}
