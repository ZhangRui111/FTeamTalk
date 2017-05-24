package com.example.john.fteamtalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_MOMENTS_NEW_MESSAGE;
import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_NEW_FRIEND;

/**
 * Created by john on 2017/5/8.
 */

public class FragmentMoments extends Fragment {

    private View view;
    private ListView msgListView;
    private AdapterNewMessage myAdapter;

    private RequestQueue mQueue;

    //data
    private List<DataItemNewMessage> msgList = new ArrayList<>();
    private DataItemNewMessage Mydata;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 经鉴定,可以通过getSupportFragmentManager().findFragmentByTag();
                 * & handler从Activity修改Fragment的UI
                 */
                case HANDLER_MOMENTS_NEW_MESSAGE:
                    //Log.i("TTT","fragment");
                    msgList.add(Mydata);
                    myAdapter.notifyDataSetChanged(); //当有新消息的时候，刷新ListView中的显示
                    msgListView.setSelection(msgList.size());  //将ListView定位到最后一行
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moments,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initPermission();
        initClickEvent();
        initData();
    }

    private void initView() {

        DataItemNewMessage data = new DataItemNewMessage("gyh","Where?",1);
        msgList.add(data);
        msgListView = (ListView) view.findViewById(R.id.newMessage_listView);
        //实例化适配器，注意和构造函数的参数的意义结合来看，第一个参数是上下文，第二个是自定义的布局id，第三个是要显示的数据源
        myAdapter = new AdapterNewMessage(getActivity(), R.layout.item_new_message,msgList);
        msgListView.setAdapter(myAdapter);  //将适配器传给实例化的listView


        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataItemNewMessage selectedEntity = msgList.get(position);//得到点击的那一项
                //以下是点击逻辑行
                Intent intent = new Intent(getActivity(),ActivityChatWindow.class);
                intent.putExtra("userId",1);
                intent.putExtra("userNickName",selectedEntity.getUserName());
                intent.putExtra("newMessage","1");
                intent.putExtra("message",selectedEntity.getMsg());
                startActivity(intent);
            }
        });
    }

    public void funcTest(int i) {

        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        String urlNewMsg = "http://115.28.66.165:8080/SendMessage.action?username=" + "gyh" + "&friendName=" + "123" + "&message" +
                "hello" + i +"&time" + "201705121123";


        StringRequest loginRequest = new StringRequest(Request.Method.POST, urlNewMsg, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(), "OK" + s, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mQueue.add(loginRequest);
    }

    private void initPermission() {

    }

    private void initClickEvent() {

    }

    private void initData() {

    }


    private void funcInitInfo() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        String urlChangeNickname = "http://115.28.66.165:8080/updateInfo.action?username=" + "123" +
                "&type=" + "0" + "&nickname=" + "1234";

        StringRequest loginRequest = new StringRequest(Request.Method.POST, urlChangeNickname, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(getActivity(), "OK" + s, Toast.LENGTH_SHORT).show();

                //handler.sendEmptyMessage(HANDLER_NEW_FRIEND);//发送消失到handler，通知主线程修改昵称成功
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        mQueue.add(loginRequest);
    }

    public void funcGetNewMessage(DataItemNewMessage data){
        Mydata = data;
        handler.sendEmptyMessage(HANDLER_MOMENTS_NEW_MESSAGE);
    }

}
