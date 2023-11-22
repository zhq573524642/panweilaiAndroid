package com.pwlsj.chat.helper;

import android.text.TextUtils;

import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.bean.ErWeiBean;
import com.pwlsj.chat.bean.PosterBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.listener.OnCommonListener;
import com.pwlsj.chat.net.AjaxCallback;
import com.pwlsj.chat.net.NetCode;
import com.pwlsj.chat.util.ParamUtil;
import com.pwlsj.chat.util.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取分享链接
 */
public class ShareUrlHelper {

    private static String shareUrl;

    public static String getShareUrl(boolean toastNull) {
        if (TextUtils.isEmpty(shareUrl)) {
            if (toastNull) {
                ToastUtil.showToast("分享失败，请重试");
            }
            getShareUrl(null);
        }
        return shareUrl;
    }

    public static void getShareUrl(final OnCommonListener<ErWeiBean<PosterBean>> onCommonListener) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        OkHttpUtils
                .post()
                .url(ChatApi.GET_SPREAD_URL())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build()
                .connTimeOut(30 * 1000)
                .readTimeOut(30 * 1000)
                .writeTimeOut(30 * 1000)
                .execute(new AjaxCallback<BaseResponse<ErWeiBean<PosterBean>>>() {
                    @Override
                    public void onResponse(BaseResponse<ErWeiBean<PosterBean>> response, int id) {
                        if (response != null && response.m_istatus == NetCode.SUCCESS) {
                            ErWeiBean<PosterBean> bean = response.m_object;
                            if (bean != null) {
                                SharedPreferenceHelper.saveShareUrl(AppManager.getInstance(), bean.shareUrl);
                                shareUrl = bean.shareUrl;
                                if (onCommonListener != null) {
                                    onCommonListener.execute(bean);
                                }
                            }
                        }
                    }
                });
    }
}