package com.tg.opengldemo.manager;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

/**
 * @CreadBy ：DramaScript
 * @date 2017/7/10
 */
public interface ICameraHelper {

    //打开相机
    boolean open(int cameraId);
    //设置相机宽高比  图片和预览大小
    void setConfig(Config config);
    //是否开启预览
    boolean preview();
    // 切换前后摄像头
    boolean switchTo(int cameraId);
    // 拍照
    void takePhoto(TakePhotoCallback callback,int nRotation);
    // 相机是否关闭
    boolean close();
    //设置预览的纹理
    void setPreviewTexture(SurfaceTexture texture);
    //获得预览大小
    Point getPreviewSize();
    //获得图片的大小
    Point getPictureSize();
    //设置预览时每一帧的回调
    void setOnPreviewFrameCallback(PreviewFrameCallback callback);

    void setFlash(int flashMode);

    class Config{
        float rate; //宽高比
        int minPreviewWidth;
        int minPictureWidth;
    }

    interface TakePhotoCallback{
        void onTakePhoto(Bitmap bitmap);
    }

    interface PreviewFrameCallback{
        void onPreviewFrame(byte[] bytes, int width, int height);
    }
}
