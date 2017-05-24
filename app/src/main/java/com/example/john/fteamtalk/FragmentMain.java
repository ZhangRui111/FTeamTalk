package com.example.john.fteamtalk;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract;
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
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.john.fteamtalk.UtilsFinalArguments.HANDLER_NEW_FRIEND;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_READ_CONTACT;
import static com.example.john.fteamtalk.UtilsFinalArguments.REQUEST_USUAL;
import static com.example.john.fteamtalk.UtilsFinalArguments.contactList;
import static com.example.john.fteamtalk.UtilsFinalArguments.dataList;
import static com.example.john.fteamtalk.UtilsFinalArguments.userInfoStatic;

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
    private AdapterFriendList myAdapter;
    private Boolean flag = true;

    //Quene
    private RequestQueue mQueue;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 233:

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
        int i = 0;

        //初始化一个网络请求队列
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(getActivity());
        }

        String ulrFriendList = "http://211.83.103.247:8037/TeamTalk/sendFriendList.action?username=" + userInfoStatic.getUsername();

       //Log.i("TTTT",ulrFriendList);
        StringRequest friendListRequest = new StringRequest(Request.Method.POST, ulrFriendList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //Log.i("TTTT",s);
                Gson gson = new Gson();
                DataFriendList dat = gson.fromJson(s,DataFriendList.class);

                int n = 0;
                for(;n<dat.getData().size();n++){
                    String Str = dat.getDataItem(n).getDepart();
                    String departStr = "";
                    switch (Str) {
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
                            departStr = "经理部";
                            break;
                        case "4":
                            departStr = "销售部";
                            break;
                        default:
                            break;
                    }
                    DataFriendInfo Data = new DataFriendInfo(n,dat.getDataItem(n).getUsername(),departStr);
                    dataList.add(Data);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.i("TTTT","error");
            }
        });
        mQueue.add(friendListRequest);

        tmpData = new DataFriendInfo(i++,"zzz","后勤部");
        dataList.add(tmpData);
        tmpData = new DataFriendInfo(i++,"gyh","IT部");
        dataList.add(tmpData);

        contactList = new ArrayList<>();  //通讯录
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

        friendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataFriendInfo selectedEntity = dataList.get(i);//得到点击的那一项

                final int num = i;
                //Toast.makeText(getActivity(), selectedEntity.getUserNickName(), Toast.LENGTH_SHORT).show();
                //handler.sendEmptyMessage(233);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setTitle("delete friend");
                builder.setMessage("Are you sure to delete" + " " + selectedEntity.getUserNickName());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dataList.remove(num);
                        myAdapter.notifyDataSetChanged(); //当有新消息的时候，刷新ListView中的显示
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        waitingDialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });

        menuMultipleActions = (FloatingActionsMenu) view.findViewById(R.id.multiple_actions);
        fabNewFriendStory =  view.findViewById(R.id.fa_newFriend_btn);
        fabFlashStory = view.findViewById(R.id.fa_flash_btn);
    }

    private void initPermission() {
        if (!AndPermission.hasPermission(getActivity(), Manifest.permission.READ_CONTACTS)) {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(REQUEST_READ_CONTACT)
                    .permission(Manifest.permission.READ_CONTACTS)
                    .send();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if(requestCode == REQUEST_READ_CONTACT) {
                // TODO 相应代码。
                //do nothing
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(getActivity(), deniedPermissions)) {

                // 第二种：用自定义的提示语。
                AndPermission.defaultSettingDialog(getActivity(), REQUEST_USUAL)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };

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
                //Toast.makeText(getActivity(), "刷新", Toast.LENGTH_SHORT).show();
                /*ActivityContact.actionStart(getActivity());
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)*/;
                myAdapter.notifyDataSetChanged();  //更新列表
                break;
            default:
                break;
        }
    }

    private void funcNewFriend() {

        //提示正在登陆
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        builder.setTitle("准备通讯录数据");
        builder.setCancelable(false);
        ProgressBar progressBar = new ProgressBar(getActivity());
        builder.setView(progressBar, 20, 20, 20, 20);
        waitingDialog = builder.create();
        waitingDialog.show();
        new ContactTask().execute();
    }

    class ContactTask extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            if (flag == true) {
                DataFriendInfo tmpData;
                int i = 0;

                ContentResolver cr = getActivity().getContentResolver();
                //取得电话本中开始一项的光标
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                //向下移动光标
                while (cursor.moveToNext()) {
                    //取得联系人名字
                    int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    String contact = cursor.getString(nameFieldColumnIndex);

                    //取得电话号码
                    String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

                    if (phone != null) {
                        while (phone.moveToNext()) {
                            String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //格式化手机号
                            PhoneNumber = PhoneNumber.replace("-", "");
                            PhoneNumber = PhoneNumber.replace(" ", "");

                            tmpData = new DataFriendInfo(i++, contact, PhoneNumber);
                            contactList.add(tmpData);
                        }
                    }
                }
                flag = false;
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            waitingDialog.dismiss();
            //Toast.makeText(getActivity(), "OK", Toast.LENGTH_SHORT).show();
            ActivityContact.actionStart(getActivity());
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }
}
