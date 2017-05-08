package com.example.john.fteamtalk;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by ZhangRui on 2016/10/31.
 */

public class DialogChooseIcon extends Dialog implements View.OnClickListener {

    private Context mContext;
    private Button takePhotoBtn, takeFromGalleryBtn, diamissBtn;
    private IDialogChooseIconEventListener mDialogChooseIconEventListener;  //自定义一个接口

    //增加一个回调函数,实现Dialog向Activity（Fragment）传值
    public interface IDialogChooseIconEventListener {
        public void listenDialogChooseIconEvent(int chooseFlag);
    }

    public DialogChooseIcon(Context context) {
        super(context, R.style.DialogChooseIcon);
        mContext = context;
    }

    /**
     * 带接口回调的构造函数
     * @param context
     * @param mListener
     */
    public DialogChooseIcon(Context context, IDialogChooseIconEventListener mListener) {
        super(context, R.style.DialogChooseIcon);
        mContext = context;
        this.mDialogChooseIconEventListener = mListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_icon);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(true);

        initView();  //初始化界面控件
        initEvent();  //初始化界面控件的事件
    }

    private void initView() {
        takePhotoBtn = (Button) findViewById(R.id.button_take_photo);
        takeFromGalleryBtn = (Button) findViewById(R.id.button_take_from_gallery);
        diamissBtn = (Button) findViewById(R.id.button_dismiss);
    }

    private void initEvent() {
        takePhotoBtn.setOnClickListener(this);
        takeFromGalleryBtn.setOnClickListener(this);
        diamissBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_take_photo:
                //调用自定义接口，传给父Fragment控制信号
                mDialogChooseIconEventListener.listenDialogChooseIconEvent(0);
                dismiss();
                break;
            case R.id.button_take_from_gallery:
                mDialogChooseIconEventListener.listenDialogChooseIconEvent(1);
                dismiss();
                break;
            case R.id.button_dismiss:
                dismiss();
                break;
            default:
                break;
        }
    }
}
