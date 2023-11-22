package com.pwlsj.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.pwlsj.chat.R;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.bean.CoverUrlBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.net.AjaxCallback;
import com.pwlsj.chat.net.NetCode;
import com.pwlsj.chat.util.ParamUtil;
import com.pwlsj.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ModifyCoverDialog extends Dialog {

    private final CoverUrlBean coverUrlBean;

    public ModifyCoverDialog(@NonNull Context context, CoverUrlBean coverUrlBean) {
        super(context, R.style.DialogStyle_Dark_Background);
        this.coverUrlBean = coverUrlBean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_set_cover_layout);

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.BottomPopupAnimation);

        //删除
        TextView delete_tv = findViewById(R.id.delete_tv);
        delete_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
                dismiss();
            }
        });
        //设为封面
        TextView set_tv = findViewById(R.id.set_tv);
        set_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMainCover();
                dismiss();
            }
        });
        //取消
        TextView cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    /**
     * 删除封面
     */
    private void delete() {

        if (coverUrlBean.t_first == 0) {
            ToastUtil.showToast(R.string.can_not_delete_main);
            return;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverImgId", String.valueOf(coverUrlBean.t_id));
        OkHttpUtils.post().url(ChatApi.DEL_COVER_IMG())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    deleteCover();
                } else {
                    ToastUtil.showToast(R.string.delete_fail);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(R.string.delete_fail);
            }
        });
    }

    /**
     * 设为主封面
     */
    private void setMainCover() {

        if (coverUrlBean.t_first == 0) {
            ToastUtil.showToast(R.string.already_main);
            return;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("coverImgId", String.valueOf(coverUrlBean.t_id));
        OkHttpUtils.post()
                .url(ChatApi.SET_MAIN_COVER_IMG())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .execute(new AjaxCallback<BaseResponse>() {
                    @Override
                    public void onResponse(BaseResponse response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            setCoverFirst();
                        } else {
                            if (response != null) {
                                ToastUtil.showToast(response.m_strMessage);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        ToastUtil.showToast(R.string.system_error);
                    }
                });
    }

    /**
     * 已删除封面
     */
    protected void deleteCover() {

    }

    /**
     * 已设为主封面
     */
    protected void setCoverFirst() {

    }

}