package com.pwlsj.chat.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.pwlsj.chat.R;
import com.pwlsj.chat.base.BaseActivity;
import com.pwlsj.chat.rtc.RtcEngineEventHandler;
import com.pwlsj.chat.rtc.RtcManager;
import com.pwlsj.chat.rtc.RtcVideoConsumer;
import com.pwlsj.chat.util.permission.PermissionUtil;

import butterknife.BindView;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.TiPanelLayout;
import io.agora.rtc.Constants;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class SetBeautyActivity extends BaseActivity {

    //视频聊天相关
    protected RtcManager rtcManager;

    @BindView(R.id.content_fl)
    TextureView localVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtil.requestPermissions(this, new PermissionUtil.OnPermissionListener() {

            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied() {
            }

        },  Manifest.permission.CAMERA);

    }

    @Override
    protected View getContentView() {
        return inflate(R.layout.activity_set_beauty_layout);
    }

    @Override
    protected void onContentAdded() {

        addContentView(new TiPanelLayout(this).init(TiSDKManager.getInstance()),
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initVideo();
    }

    /**
     * 初始化video
     */
    protected void initVideo() {

        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT);

        rtcManager = RtcManager.get();
        rtcManager.addRtcHandler(rtcEngineEventHandler);
        rtcManager.rtcEngine().setVideoEncoderConfiguration(configuration);
        rtcManager.rtcEngine().setVideoSource(new RtcVideoConsumer());
        rtcManager.rtcEngine().enableVideo();
        rtcManager.getCameraManager().setLocalPreview(localVideoView);
        rtcManager.startCamera();
        rtcManager.rtcEngine().enableLocalAudio(true);
        rtcManager.rtcEngine().enableLocalVideo(true);
        rtcManager.rtcEngine().muteLocalVideoStream(false);
        rtcManager.rtcEngine().muteLocalAudioStream(false);
        rtcManager.rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        rtcManager.rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
    }



    RtcEngineEventHandler rtcEngineEventHandler = new RtcEngineEventHandler() {

        private void onError() {
            new AlertDialog.Builder(mContext)
                    .setMessage("进入房间失败")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create().show();
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Log.d("EngineEvent", "onJoinChannelSuccess: " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {

        }

        @Override
        public void onUserJoined(int uid, int elapsed) {

        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            Log.d("EngineEvent", state + "onRemoteVideoStateChanged: " + reason);
        }

        @Override
        public void onUserMuteVideo(int uid, boolean muted) {

        }

    };
}