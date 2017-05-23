package com.example.john.fteamtalk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_NEW_FRIEND;

/**
 * Created by john on 2017/5/8.
 */

public class FragmentMoments extends Fragment {

    private View view;
    private Button getMsgBtn;

    private RequestQueue mQueue;

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
        final int[] i = {0};
        getMsgBtn = (Button) view.findViewById(R.id.button);
        getMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //funcInitInfo();
                funcTest(i[0]);
                i[0]++;
            }
        });
    }

    private void funcTest(int i) {

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

}
