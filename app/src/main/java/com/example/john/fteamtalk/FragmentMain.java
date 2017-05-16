package com.example.john.fteamtalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_NEW_FRIEND;

/**
 * Created by john on 2017/3/22.
 */

public class FragmentMain extends Fragment implements View.OnClickListener {

    //UIs
    private View view;
    private FloatingActionsMenu menuMultipleActions;
    private View fabNewFriendStory;
    private View fabFlashStory;
    private ListView friendList;
    private AlertDialog waitingDialog;

    //data
    private List<DataFriendInfo> dataList;
    private AdapterFriendList myAdapter;

    //Quene
    private RequestQueue mQueue;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_NEW_FRIEND:
                    waitingDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initView();
        initPermission();
        initClickEvent();
    }

    private void initData() {
        dataList = new ArrayList<>();
        DataFriendInfo tmpData;
        tmpData = new DataFriendInfo(0,"小张",0);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(1,"小李",1);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(2,"小妞",2);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(3,"小杨",3);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(4,"小朱",4);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(5,"小赵",3);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(6,"小孙",2);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(7,"小钱",1);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(8,"小周",0);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(9,"小王",0);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(10,"小朱",4);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(11,"小赵",3);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(12,"小孙",2);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(13,"小钱",1);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(14,"小周",0);
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(15,"小王",0);
        dataList.add(tmpData);
    }

    private void initView() {
        myAdapter = new AdapterFriendList(getActivity(), R.layout.item_friendlist,dataList);  //实例化适配器，注意和构造函数的参数的意义结合来看，第一个参数是上下文，第二个是自定义的布局id，第三个是要显示的数据源
        friendList = (ListView) view.findViewById(R.id.friend_list);
        friendList.setAdapter(myAdapter);  //将适配器传给实例化的listView
        /**
         * 给ListView添加点击事件，可以不看
         */
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataFriendInfo selectedEntity = dataList.get(position);//得到点击的那一项
                //以下是点击逻辑行
                Intent intent = new Intent(getActivity(),ActivityChatWindow.class);
                intent.putExtra("userId",selectedEntity.getUserId());
                intent.putExtra("userNickName",selectedEntity.getUserNickName());
                startActivity(intent);
            }
        });

        menuMultipleActions = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        fabNewFriendStory =  view.findViewById(R.id.fa_newFriend_btn);
        fabFlashStory = view.findViewById(R.id.fa_flash_btn);
    }

    private void initPermission() {

    }

    private void initClickEvent() {
        fabNewFriendStory.setOnClickListener(this);
        fabFlashStory.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fa_newFriend_btn:
                funcNewFriend();
                break;
            case R.id.fa_flash_btn:
                Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void funcNewFriend() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        builder.setTitle("Find a new friend");

        final LinearLayout newFriendLinlay= new LinearLayout(getActivity());
        newFriendLinlay.setOrientation(LinearLayout.VERTICAL);

        final EditText input = new EditText(getActivity());
        input.setSingleLine();
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setBackgroundResource(R.drawable.edittext_input_line);  //自定义EditText颜色
        input.setText("new friend ID");
        input.setTextColor(Color.GRAY);
        newFriendLinlay.addView(input);
        final EditText inputMsg = new EditText(getActivity());
        inputMsg.setSingleLine();
        inputMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        inputMsg.setBackgroundResource(R.drawable.edittext_input_line);  //自定义EditText颜色
        inputMsg.setText("some message");
        inputMsg.setTextColor(Color.GRAY);
        newFriendLinlay.addView(inputMsg);
        builder.setView(newFriendLinlay,20,20,20,20);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String friendName = input.getText().toString();
                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行网络请求
                        funcIfExist("123",friendName);  /*用户真实昵称需要更改这里*/
                    }
                }.start();

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setTitle("正在查找");
                builder.setCancelable(false);
                ProgressBar progressBar = new ProgressBar(getActivity());
                builder.setView(progressBar,20,20,20,20);
                builder.setCancelable(true);
                waitingDialog = builder.create();
                waitingDialog.show();  //记得添加dismiss

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void funcIfExist(String myNickName, String otherNickName) {
        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        String urlNewFriend = "http://211.83.107.1:8037/TeamTalk/VerifyFriend.action?username=" + myNickName + "&friendName=" + otherNickName;

        StringRequest loginRequest = new StringRequest(Request.Method.POST, urlNewFriend, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(), "OK" + s, Toast.LENGTH_SHORT).show();

                handler.sendEmptyMessage(HANDLER_NEW_FRIEND);//发送消失到handler，通知主线程修改昵称成功
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mQueue.add(loginRequest);
    }
}
