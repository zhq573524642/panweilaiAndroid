package com.pwlsj.chat.util.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.pwlsj.chat.R;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.constant.Constant;
import com.pwlsj.chat.helper.ShareUrlHelper;
import com.pwlsj.chat.util.BitmapUtil;
import com.pwlsj.chat.util.ToastUtil;

/**
 * 微信朋友圈分享
 */
public class ShareWechatCircle implements IShare {

    @Override
    public void share(Activity activity) {

        IWXAPI mWxApi = WXAPIFactory.createWXAPI(activity, Constant.WE_CHAT_APPID, true);
        mWxApi.registerApp(Constant.WE_CHAT_APPID);

        if (!mWxApi.isWXAppInstalled()) {
            ToastUtil.showToast(R.string.not_install_we_chat);
            return;
        }

        String mShareUrl = ShareUrlHelper.getShareUrl(true);
        if (TextUtils.isEmpty(mShareUrl)) {
            return;
        }

        String title = activity.getString(R.string.chat_title);
        String des = activity.getString(R.string.chat_des);

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mShareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = des;
        Bitmap thumb = BitmapUtil.setBackGroundColor(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher));
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        req.message = msg;
        req.transaction = String.valueOf(System.currentTimeMillis());
        mWxApi.sendReq(req);
        boolean res = mWxApi.sendReq(req);
        if (res) {
            AppManager.getInstance().setIsMainPageShareQun(false);
        }
    }
}