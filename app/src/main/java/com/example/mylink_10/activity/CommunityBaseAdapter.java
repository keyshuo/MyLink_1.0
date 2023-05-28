package com.example.mylink_10.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.mylink_10.R;

import java.util.List;

public class CommunityBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<CommunityMSG> mMSGList;

    public CommunityBaseAdapter(Context mContext, List<CommunityMSG> mMSGList) {
        this.mContext = mContext;
        this.mMSGList = mMSGList;
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
        ViewHolder holder;
        if(convertView == null)
        {
            convertView =  LayoutInflater.from(mContext).inflate(R.layout.item_community,null);
            holder = new ViewHolder();
            holder.tv_name = convertView.findViewById(R.id.cm_name);
            holder.tv_msg = convertView.findViewById(R.id.cm_content);
            holder.tv_time = convertView.findViewById(R.id.cm_time);
            holder.btn_comments = convertView.findViewById(R.id.cm_bottom);
            //将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        //给控件设置好数据
        CommunityMSG cmmsg = mMSGList.get(position);
        holder.tv_msg.setText(cmmsg.getMsg());
        holder.tv_name.setText(cmmsg.getName());
        holder.tv_time.setText(cmmsg.getTime());
        holder.btn_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToastUtil.show(mContext,"按钮被点击"+cmmsg.getName());
                Intent intent = new Intent(mContext, CommunityRemarksActivity.class);
                //intent.putExtra("item", (CharSequence) cmmsg);
                intent.putExtra("CommunityMSG", mMSGList.get(position));
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }
    public final class ViewHolder{
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_msg;
        public Button btn_comments;
    }
}
