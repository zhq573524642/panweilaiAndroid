package com.pwlsj.chat.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.pwlsj.chat.dialog.ImageVerifyCodeDialog;
import com.pwlsj.chat.listener.OnCommonListener;
import com.pwlsj.chat.util.VerifyUtils;
import com.pwlsj.chat.util.upload.FileUploadManager;
import com.pwlsj.chat.util.upload.UploadTask;
import com.pwlsj.chat.view.CodeTextView;
import com.pwlsj.chat.view.recycle.OnItemClickListener;
import com.pwlsj.chat.R;
import com.pwlsj.chat.base.BaseActivity;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.constant.Constant;
import com.pwlsj.chat.helper.ImageHelper;
import com.pwlsj.chat.net.AjaxCallback;
import com.pwlsj.chat.net.NetCode;
import com.pwlsj.chat.util.ParamUtil;
import com.pwlsj.chat.util.ToastUtil;
import com.pwlsj.chat.view.recycle.AbsRecycleAdapter;
import com.pwlsj.chat.view.recycle.ViewHolder;
import com.zhihu.matisse.Matisse;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：投诉举报页面
 * 作者：
 * 创建时间：2018/6/20
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ReportActivity extends BaseActivity {

    @BindView(R.id.etContent)
    EditText etContent;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.phone_et)
    EditText phoneEt;

    @BindView(R.id.verify_code_et)
    EditText verifyCodeEt;

    @BindView(R.id.get_code_tv)
    CodeTextView getCodeTv;

    private AbsRecycleAdapter adapter;

    private int mActorId;

    private final int CHOOSE_IMAGE = 1001;

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_report);
    }

    @Override
    protected void onContentAdded() {

        mActorId = getIntent().getIntExtra(Constant.ACTOR_ID, 0);
        setTitle(R.string.report_des);

        adapter = new AbsRecycleAdapter(new AbsRecycleAdapter.Type(R.layout.item_img_report, UploadTask.class)) {

            @Override
            public void convert(ViewHolder holder, Object t) {
                UploadTask task = (UploadTask) t;
                if (task.filePath == null) {
                    return;
                }
                Glide.with(mContext)
                        .load(task.filePath)
                        .transform(new CenterCrop())
                        .into(holder.<ImageView>getView(R.id.content_iv));
            }

        };
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object obj, int position) {
                ImageHelper.openPictureChoosePage(mContext, CHOOSE_IMAGE, 3);
            }
        });
        adapter.setDatas(Collections.singletonList(new UploadTask()));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
    }

    @OnClick({R.id.get_code_tv, R.id.save_tv})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.get_code_tv:
                String phone = phoneEt.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showToast(getApplicationContext(), R.string.phone_number_null);
                    return;
                }
                if (!VerifyUtils.isPhoneNum(phone)) {
                    ToastUtil.showToast(getApplicationContext(), R.string.wrong_phone_number);
                    return;
                }
                ImageVerifyCodeDialog dialog = ImageVerifyCodeDialog.showDialog(this, phone, 3);
                dialog.setOnOptionSuccessListener(new OnCommonListener() {
                    @Override
                    public void execute(Object o) {
                        if (isFinishing()) {
                            return;
                        }
                        ToastUtil.showToast(getApplicationContext(), R.string.send_success_des);
                        getCodeTv.start(60, "重新获取%ss");
                    }
                });
                break;

            case R.id.save_tv:
                if (getEditString(etContent).length() < 10) {
                    ToastUtil.showToast(this, etContent.getHint().toString());
                    return;
                }
                UploadTask task = (UploadTask) adapter.getData().get(0);
                if (task.filePath == null) {
                    ToastUtil.showToast(this, "请选择上传图片");
                    return;
                }
                if (TextUtils.isEmpty(getEditString(phoneEt))) {
                    ToastUtil.showToast(this, R.string.phone_number_null);
                    return;
                }
                if (TextUtils.isEmpty(getEditString(verifyCodeEt))) {
                    ToastUtil.showToast(this, "请输入验证码");
                    return;
                }
                post();
                break;
        }
    }

    /**
     * 上传
     */
    private void post() {
        showLoadingDialog();
        List<UploadTask> tasks = adapter.getData();
        FileUploadManager.permissionUpload(tasks, new OnCommonListener<Boolean>() {
            @Override
            public void execute(Boolean aBoolean) {
                if (aBoolean) {
                    String paths = "";
                    for (UploadTask task : tasks) {
                        paths += !TextUtils.isEmpty(paths) ? "," + task.url : task.url;
                    }
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("userId", getUserId());
                    paramMap.put("coverUserId", mActorId);
                    paramMap.put("t_phone", getEditString(phoneEt));
                    paramMap.put("comment", getEditString(etContent));
                    paramMap.put("t_code", getEditString(verifyCodeEt));
                    paramMap.put("img", paths);
                    OkHttpUtils.post().url(ChatApi.SAVE_COMPLAINT())
                            .addParams("param", ParamUtil.getParam(paramMap))
                            .build().execute(new AjaxCallback<BaseResponse<String>>() {

                        @Override
                        public void onResponse(BaseResponse response, int id) {
                            if (response != null) {
                                if (response.m_istatus == NetCode.SUCCESS) {
                                    finish();
                                }
                                ToastUtil.showToast(response.m_strMessage);
                            }
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtil.showToast(R.string.complain_fail);
                        }

                        @Override
                        public void onAfter(int id) {
                            dismissLoadingDialog();
                        }

                    });
                } else {
                    dismissLoadingDialog();
                }
            }
        });
    }

    private String getEditString(EditText text) {
        return text.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CHOOSE_IMAGE) {
                List<String> pathResult = Matisse.obtainPathResult(data);
                if (pathResult != null && pathResult.size() > 0) {
                    List<UploadTask> tasks = new ArrayList<>(pathResult.size());
                    for (String s : pathResult) {
                        UploadTask task = new UploadTask();
                        task.filePath = s;
                        tasks.add(task);
                    }
                    adapter.setDatas(tasks);
                }
            }
        }
    }

}