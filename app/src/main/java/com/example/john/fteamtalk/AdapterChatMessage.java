package com.example.john.fteamtalk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.john.fteamtalk.UtilsFinalArguments.imagePicPath;
import static com.example.john.fteamtalk.UtilsLibrary.decodeFileUtils;

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
            viewHolder.leftPic = (ImageView) view.findViewById(R.id.left_sendPic);
            viewHolder.rightPic = (ImageView) view.findViewById(R.id.right_sendPic);
            viewHolder.leftPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    funcDialog();
                }
            });
            viewHolder.rightPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    funcDialog();
                }
            });
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if(msg.getType() == DataChatMessage.TYPE_SEND){
            if(msg.getIfPic() == DataChatMessage.IF_PIC_NO) {
                viewHolder.leftLayout.setVisibility(View.INVISIBLE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightPic.setVisibility(View.GONE);
                viewHolder.rightMsg.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setText(msg.getChatMessage());
            } else {
                viewHolder.leftLayout.setVisibility(View.INVISIBLE);
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.rightMsg.setVisibility(View.GONE);
                viewHolder.rightPic.setVisibility(View.VISIBLE);
                viewHolder.rightPic.setImageBitmap(decodeFileUtils(msg.getPath()));
            }
        }else if(msg.getType() == DataChatMessage.TYPE_RECEIVED){
            if(msg.getIfPic() == DataChatMessage.IF_PIC_NO) {
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.INVISIBLE);
                viewHolder.leftPic.setVisibility(View.GONE);
                viewHolder.leftMsg.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setText(msg.getChatMessage());
            } else {
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.INVISIBLE);
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.leftPic.setVisibility(View.VISIBLE);
                viewHolder.leftPic.setImageBitmap(decodeFileUtils(msg.getPath()));
            }
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
        ImageView leftPic;
        ImageView rightPic;
    }

    private void funcDialog() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                getContext(), R.style.MyAlertDialogStyle);
        builder.setCancelable(true);
        final ImageView picImg = new ImageView(getContext());
        picImg.setImageBitmap(decodeFileUtils(imagePicPath));
        builder.setView(picImg,20,20,20,20);
        builder.create().show();
    }
}


