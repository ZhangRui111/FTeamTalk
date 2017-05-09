package com.example.john.fteamtalk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by john on 2017/5/8.
 */
public class AdapterChatMessage extends ArrayAdapter<DataChatMessage> {
    private int resourceId;

    public AdapterChatMessage(Context context, int resource, List<DataChatMessage> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataChatMessage msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){

            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(msg.getType() == DataChatMessage.TYPE_SEND){
            viewHolder.leftLayout.setVisibility(View.INVISIBLE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getChatMessage());
        }else if(msg.getType() == DataChatMessage.TYPE_RECEIVED){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.INVISIBLE);
            viewHolder.leftMsg.setText(msg.getChatMessage());
        }
        return view;
    }

    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        ImageView leftIcon;
        ImageView rightIcon;
        TextView leftMsg;
        TextView rightMsg;
    }
}


