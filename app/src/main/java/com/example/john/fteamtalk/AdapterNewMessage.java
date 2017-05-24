package com.example.john.fteamtalk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2017/5/24.
 */

public class AdapterNewMessage extends ArrayAdapter<DataItemNewMessage> {

    int resourceId;
    Context mContext;

    public AdapterNewMessage(Context context, int resource, List<DataItemNewMessage> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        DataItemNewMessage msg = getItem(position);
        final View view;
        ViewHolder viewHolder;
        if(convertView == null){

            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.userIdTxv = (TextView) view.findViewById(R.id.itemUserID);
            viewHolder.userDepartment = (TextView) view.findViewById(R.id.itemUserDepart);
            viewHolder.message = (TextView) view.findViewById(R.id.newMessageTxv);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.userIdTxv.setText(msg.getUserName());
        /*String depart = msg.getData().get;
        String departStr = "";
        switch (depart) {
            case "0":
                departStr = "IT部";
                break;
            case "1":
                departStr = "秘书处";
                break;
            case "2":
                departStr = "后勤部";
                break;
            case "3":
                departStr = "销售部";
                break;
            case "4":
                departStr = "经理部";
                break;
            default:
                break;
        }*/
        String num = msg.getNum() + "";
        viewHolder.userDepartment.setText(num);
        viewHolder.message.setText(msg.getMsg());

        return view;
    }

    class ViewHolder{
        TextView userIdTxv;
        TextView userDepartment;
        TextView message;
    }
}
