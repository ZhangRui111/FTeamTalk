package com.example.john.fteamtalk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by john on 2017/5/23.
 */

public class AdapterContactList extends ArrayAdapter<DataFriendInfo> {

    int resourceId;
    Context mContext;

    public AdapterContactList(Context context, int resource, List<DataFriendInfo> objects) {
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
        String departStr = msg.getDepartment();

        viewHolder.userDepartment.setText(departStr);
        return view;
    }

    class ViewHolder{
        TextView userIdTxv;
        TextView userDepartment;
    }
}
