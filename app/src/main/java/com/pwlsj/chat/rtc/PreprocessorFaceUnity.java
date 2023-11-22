package com.pwlsj.chat.rtc;

import android.content.Context;
import android.opengl.GLES20;

import com.faceunity.FURenderer;
import com.pwlsj.chat.ttt.QiNiuChecker;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.bean.TiRotation;
import io.agora.capture.framework.modules.channels.VideoChannel;
import io.agora.capture.framework.modules.processors.IPreprocessor;
import io.agora.capture.video.camera.VideoCaptureFrame;

public class PreprocessorFaceUnity implements IPreprocessor {

    private final static String TAG = "FaceUnity";

    private FURenderer mFURenderer;
    private Context mContext;
    private boolean mEnabled;

    public boolean isFrontCamera = true;

    public PreprocessorFaceUnity(Context context) {
        mContext = context;
        mEnabled = true;
    }

    @Override
    public VideoCaptureFrame onPreProcessFrame(VideoCaptureFrame outFrame, VideoChannel.ChannelContext context) {


        QiNiuChecker.get().checkTakeShot(outFrame.image, outFrame.format.getWidth(), outFrame.format.getHeight());

        if(!mEnabled){
            return outFrame;
        }
        outFrame.textureId = TiSDKManager.getInstance().renderOESTexture(outFrame.textureId,
                outFrame.format.getWidth(),
                outFrame.format.getHeight(),
                TiRotation.fromValue(outFrame.rotation),
                isFrontCamera);

        outFrame.format.setTexFormat(GLES20.GL_TEXTURE_2D);
        return outFrame;
    }

    @Override
    public void initPreprocessor() {
//        mFURenderer = new FURenderer.Builder(mContext)
//                .inputTextureType(1)
//                .build();
//        mFURenderer.config(RenderConfig.get(AppManager.getInstance()));
    }

    @Override
    public void enablePreProcess(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public void releasePreprocessor(VideoChannel.ChannelContext context) {
    }

    public FURenderer getFURenderer() {
        return mFURenderer;
    }
}