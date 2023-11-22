package com.pwlsj.chat.util.share;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.pwlsj.chat.R;
import com.pwlsj.chat.helper.ShareUrlHelper;
import com.pwlsj.chat.util.ToastUtil;

/**
 * 复制链接
 */
public class ShareCopyUrl implements IShare {

    @Override
    public void share(Activity activity) {
        String mShareUrl = ShareUrlHelper.getShareUrl(true);
        if (TextUtils.isEmpty(mShareUrl)) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", mShareUrl);
        if (cm != null) {
            cm.setPrimaryClip(mClipData);
            ToastUtil.showToast(R.string.copy_success);
        }
    }
}