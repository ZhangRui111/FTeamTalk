package com.example.john.fteamtalk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by john on 2017/5/8.
 */

public class AdapterFriendList extends ArrayAdapter<DataFriendInfo> {

    int resourceId;
    Context mContext;

    public AdapterFriendList(Context context, int resource, List<DataFriendInfo> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        DataFriendInfo msg = getItem(position);
        final View view;
        ViewHolder viewHolder;
        if(convertView == null){

            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.userIdTxv = (TextView) view.findViewById(R.id.itemUserId);
            viewHolder.userDepartment = (TextView) view.findViewById(R.id.itemUserDepartment);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.userIdTxv.setText(msg.getUserNickName());
        int depart = msg.getDepartment();
        String departStr = "";
        switch (depart) {
            case 0:
                departStr = "IT部";
                break;
            case 1:
                departStr = "秘书处";
                break;
            case 2:
                departStr = "后勤部";
                break;
            case 3:
                departStr = "销售部";
                break;
            case 4:
                departStr = "经理部";
                break;
            default:
                break;
        }
        viewHolder.userDepartment.setText(departStr);

        return view;
    }

    class ViewHolder{
        TextView userIdTxv;
        TextView userDepartment;
    }
}
