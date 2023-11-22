package com.pwlsj.chat.net;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pwlsj.chat.R;
import com.pwlsj.chat.activity.AudioChatActivity;
import com.pwlsj.chat.activity.VideoChatActivity;
import com.pwlsj.chat.base.AppManager;
import com.pwlsj.chat.base.BaseActivity;
import com.pwlsj.chat.base.BaseResponse;
import com.pwlsj.chat.bean.AVChatBean;
import com.pwlsj.chat.constant.ChatApi;
import com.pwlsj.chat.dialog.VipDialog;
import com.pwlsj.chat.helper.ChargeHelper;
import com.pwlsj.chat.listener.OnCommonListener;
import com.pwlsj.chat.socket.ConnectHelper;
import com.pwlsj.chat.util.ParamUtil;
import com.pwlsj.chat.util.ToastUtil;
import com.pwlsj.chat.util.permission.PermissionUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import sakura.bottommenulibrary.bottompopfragmentmenu.BottomMenuFragment;
import sakura.bottommenulibrary.bottompopfragmentmenu.MenuItem;

public class AudioVideoRequester {

    public static final int CHAT_VIDEO = 1;

    public static final int CHAT_AUDIO = 2;

    private WeakReference<Activity> weakReference;
    private boolean isUserToActor;
    private int otherId;

    public AudioVideoRequester(Activity activity,
                               boolean isUserToActor,
                               int otherId) {

        this.weakReference = new WeakReference<>(activity);
        this.otherId = otherId;
        this.isUserToActor = isUserToActor;
    }

    public final void execute() {
        if (getUserId() == otherId) {
            return;
        }
        new BottomMenuFragment(weakReference.get())
                .addMenuItems(new MenuItem("视频通话"))
                .addMenuItems(new MenuItem("语音通话"))
                .setOnItemClickListener(new BottomMenuFragment.OnItemClickListener() {
                    @Override
                    public void onItemClick(TextView menu_item, int position) {
                        executeRequest(position + 1);
                    }
                }).show();
    }

    /**
     * 发起视频通话
     */
    public final void executeVideo() {
        executeRequest(CHAT_VIDEO);
    }

    /**
     * 发起语音通话
     */
    public final void executeAudio() {
        executeRequest(CHAT_AUDIO);
    }

    private void executeRequest(final int chatType) {

        if (getUserId() == otherId) {
            return;
        }

        PermissionUtil.requestPermissions(weakReference.get(), new PermissionUtil.OnPermissionListener() {

                    @Override
                    public void onPermissionGranted() {
                        if (isUserToActor && AppManager.getInstance().getUserInfo().isVip()) {
                            sVipSwitch(new OnCommonListener<Boolean>() {
                                @Override
                                public void execute(Boolean aBoolean) {
                                    if (aBoolean) {
                                        getSign(chatType, null);
                                    }
                                }
                            });
                        } else {
                            getSign(chatType, null);
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        ToastUtil.showToast("无麦克风或者摄像头权限，无法使用该功能");
                    }

                },
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO);

    }

    //-------------------权限---------------------
    protected void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakReference.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakReference.get(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakReference.get(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakReference.get(), Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakReference.get(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(weakReference.get(),
                        permissions.toArray(new String[0]),
                        100);
            }
        }
    }

    /**
     * Vip用户提示
     * code == 2 提示用户：每个女主播每天免费接听5个Vip视频，超过的话，按照正常收费
     */
    private void sVipSwitch(final OnCommonListener<Boolean> listener) {
        showLoadingDialog();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", getUserId());
        paramMap.put("coverLinkUserId", otherId);
        paramMap.put("launchUserId", getUserId());

        OkHttpUtils.post().url(ChatApi.svipSwitch())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {

            @Override
            public void onResponse(BaseResponse response, int id) {
                if (weakReference.get().isFinishing())
                    return;
                dismissLoadingDialog();
                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {
                        listener.execute(true);
                    } else if (response.m_istatus == 2) {
                        new AlertDialog.Builder(weakReference.get())
                                .setMessage(response.m_strMessage)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        listener.execute(true);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .create().show();
                    } else {
                        ToastUtil.showToast(response.m_strMessage);
                    }
                } else {
                    ToastUtil.showToast(R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                if (weakReference.get().isFinishing())
                    return;
                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(R.string.system_error);
            }

        });
    }


    //------------------------------发起视频部分--------------------------------------

    /**
     * 获取签名,并登陆 然后创建房间,并加入
     */
    private void getSign(final int chatType, final AVChatBean avChatBean) {
        if (avChatBean != null) {
            showLoadingDialog();
            getAgoraSign(avChatBean.roomId, new OnCommonListener<String>() {
                @Override
                public void execute(String s) {
                    if (weakReference.get().isFinishing())
                        return;
                    if (!TextUtils.isEmpty(s)) {
                        avChatBean.sign = s;
                        if (avChatBean.coverRole == 0 && AppManager.getInstance().getUserInfo().t_role == 0) {
                            ToastUtil.showToast(R.string.sex_can_not_communicate);
                        } else {
                            requestChat(avChatBean);
                        }
                    } else {
                        ToastUtil.showToast("连接失败");
                    }
                    dismissLoadingDialog();
                }
            });
        } else {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("userId", getUserId());
            paramMap.put("anthorId", otherId);
            OkHttpUtils.post().url(ChatApi.GET_VIDEO_CHAT_SIGN())
                    .addParams("param", ParamUtil.getParam(paramMap))
                    .build().execute(new AjaxCallback<BaseResponse<AVChatBean>>() {

                @Override
                public void onResponse(BaseResponse<AVChatBean> response, int id) {
                    if (weakReference.get().isFinishing())
                        return;
                    if (response != null) {
                        if (response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                            AVChatBean avChatBean = response.m_object;
                            avChatBean.chatType = chatType;
                            avChatBean.isRequest = true;
                            avChatBean.countdown = !avChatBean.isActor();
                            avChatBean.otherId = otherId;
                            getSign(chatType, avChatBean);
                        } else if (response.m_istatus == -7) {
                            new VipDialog(weakReference.get()).show();
                        } else {
                            ToastUtil.showToast(response.m_strMessage);
                        }
                    }
                    dismissLoadingDialog();
                }

                @Override
                public void onBefore(Request request, int id) {
                    super.onBefore(request, id);
                    showLoadingDialog();
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    if (weakReference.get().isFinishing())
                        return;
                    super.onError(call, e, id);
                    dismissLoadingDialog();
                    ToastUtil.showToast(R.string.system_error);
                }
            });
        }
    }

    /**
     * 发起通信
     */
    private void requestChat(final AVChatBean chatBean) {

        String method;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("roomId", chatBean.roomId);
        paramMap.put("chatType", chatBean.chatType);
        if (isUserToActor) {
            method = ChatApi.LAUNCH_VIDEO_CHAT();
            paramMap.put("userId", getUserId());
            paramMap.put("coverLinkUserId", otherId);
        } else {
            method = ChatApi.ACTOR_LAUNCH_VIDEO_CHAT();
            paramMap.put("anchorUserId", getUserId());
            paramMap.put("userId", otherId);
        }
        OkHttpUtils.post()
                .url(method)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse response, int id) {

                if (weakReference.get().isFinishing())
                    return;

                dismissLoadingDialog();

                if (response != null) {
                    if (response.m_istatus == NetCode.SUCCESS) {

                        //视频跳转
                        if (chatBean.chatType == 1) {
                            VideoChatActivity.start(weakReference.get(), chatBean);
                        }
                        //语音跳转
                        else {
                            AudioChatActivity.startCall(weakReference.get(), chatBean);
                        }

                    } else if (response.m_istatus == -2) {
                        //你拨打的用户正忙,请稍候再拨
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(message);
                        } else {
                            ToastUtil.showToast(R.string.busy_actor);
                        }
                    } else if (response.m_istatus == -1) {
                        //对方不在线
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(message);
                        } else {
                            ToastUtil.showToast(R.string.not_online);
                        }
                    } else if (response.m_istatus == -3) {
                        //对方设置了勿扰
                        String message = response.m_strMessage;
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtil.showToast(message);
                        } else {
                            ToastUtil.showToast(R.string.not_bother);
                        }
                    } else if (response.m_istatus == -4) {
                        //余额不足
                        ChargeHelper.showSetCoverDialog(weakReference.get());
                    } else if (response.m_istatus == -7) {
                        //非vip用户提示充vip
                        new VipDialog(weakReference.get(), response.m_strMessage).show();
                    } else {
                        //-10 socket未登录
                        if (response.m_istatus == -10) {
                            ConnectHelper.get().checkLogin();
                        }
                        ToastUtil.showToast(response.m_strMessage);
                    }
                } else {
                    ToastUtil.showToast(R.string.system_error);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {

                if (weakReference.get().isFinishing())
                    return;

                super.onError(call, e, id);
                dismissLoadingDialog();
                ToastUtil.showToast(R.string.system_error);
            }

        });
    }

    private int getUserId() {
        return AppManager.getInstance().getUserInfo().t_id;
    }

    private void dismissLoadingDialog() {
        if (weakReference.get() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) weakReference.get();
            baseActivity.dismissLoadingDialog();
        }
    }

    private void showLoadingDialog() {
        if (weakReference.get() instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) weakReference.get();
            baseActivity.showLoadingDialog();
        }
    }

    /**
     * 获取签名
     */
    public static void getAgoraSign(int roomId, OnCommonListener<String> commonListener) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", AppManager.getInstance().getUserInfo().t_id);
        paramMap.put("roomId", roomId);
        OkHttpUtils.post().url(ChatApi.getAgoraRoomSign())
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse<String>>() {
            @Override
            public void onResponse(BaseResponse<String> response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS && response.m_object != null) {
                    JSONObject jsonObject = JSON.parseObject(response.m_object);
                    commonListener.execute(jsonObject.getString("rtcToken"));
                } else {
                    onError(null, null, 0);
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showToast(R.string.system_error);
            }
        });
    }

}