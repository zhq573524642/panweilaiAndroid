package com.pwlsj.chat.activity;

import android.view.View;

import com.pwlsj.chat.R;
import com.pwlsj.chat.base.BaseActivity;

public class MyInviteActivity extends BaseActivity {

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_my_invite);
    }

    @Override
    protected void onContentAdded() {
        setTitle("我的邀请");
    }

}