package com.tg.opengldemo.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @CreadBy ：DramaScript
 * @date 2017/7/10
 */
public class CameraHelper implements ICameraHelper {

    private Config mConfig;
    private Camera mCamera;
    private CameraSizeComparator sizeComparator;

    private Camera.Size picSize;
    private Camera.Size preSize;

    private Point mPicSize;
    private Point mPreSize;
    private boolean isFrontCamera = true;
    private int picWide, picHeight;

    private int cameraId;


    /**
     * 当前缩放
     */
    private int mZoom;

    public Camera getmCamera() {
        return mCamera;
    }

    public void setPicWide(int picWide) {
        this.picWide = picWide;
    }

    public void setPicHeight(int picHeight) {
        this.picHeight = picHeight;
    }

    public int getZoom() {
        return mZoom;
    }

    public void setZoom(int zoom) {
        if (mCamera == null) return;
        Camera.Parameters parameters;
        //注意此处为录像模式下的setZoom方式。在Camera.unlock之后，调用getParameters方法会引起android框架底层的异常
        //stackoverflow上看到的解释是由于多线程同时访问Camera导致的冲突，所以在此使用录像前保存的mParameters。
        parameters = mCamera.getParameters();

        if (!parameters.isZoomSupported()) return;
        parameters.setZoom(zoom);
        mCamera.setParameters(parameters);
        mZoom = zoom;
    }

    public int getMaxZoom() {
        if (mCamera == null) return -1;
        Camera.Parameters parameters = mCamera.getParameters();
        if (!parameters.isZoomSupported()) return -1;
        return parameters.getMaxZoom() > 40 ? 40 : parameters.getMaxZoom();
    }

    public CameraHelper() {
        this.mConfig = new Config();
        mConfig.minPreviewWidth = 720;
        mConfig.minPictureWidth = 720;
        mConfig.rate = 1.778f;
        sizeComparator = new CameraSizeComparator();
    }

    @Override
    public boolean open(int cameraId) {
        mCamera = Camera.open(cameraId);
        this.cameraId = cameraId;
        if (mCamera != null) {
            Camera.Parameters param = mCamera.getParameters();
            picSize = getPropPictureSize(param.getSupportedPictureSizes(), mConfig.rate,
                    mConfig.minPictureWidth);
            preSize = getPropPreviewSize(param.getSupportedPreviewSizes(), mConfig.rate, mConfig
                    .minPreviewWidth);
            //设置照片生成的大小
            picWide = picSize.width;
            picHeight = picSize.height;
            param.setPictureSize(picWide, picHeight);
            //生成照片预览大小
            param.setPreviewSize(preSize.width, preSize.height);
            mCamera.setParameters(param);
            Camera.Size pre = param.getPreviewSize();
            Camera.Size pic = param.getPictureSize();
            mPicSize = new Point(pic.height, pic.width);
            mPreSize = new Point(pre.height, pre.width);
            Log.e("wuwang", "camera previewSize:" + mPreSize.x + "/" + mPreSize.y);

            return true;
        }
        return false;
    }

    @Override
    public void setConfig(Config config) {
        this.mConfig = config;
    }

    @Override
    public boolean preview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
        return false;
    }

    @Override
    public boolean switchTo(int cameraId) {
        close();
        open(cameraId);
        if (cameraId == 1) {
            isFrontCamera = true;
        } else {
            isFrontCamera = false;
        }
        return false;
    }

    @Override
    public void takePhoto(final TakePhotoCallback callback, final int nRotation) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.e("tag", "拍照的角度：" + nRotation);
                Bitmap bm = null;
                if (nRotation==0){
                    bm = setTakePicktrueOrientation(cameraId, BitmapFactory.decodeByteArray(data, 0, data.length));
                }else {
                    bm  = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (isFrontCamera) {
                        bm = rotateBitmapByDegree(bm, 270 - nRotation, false);
                    } else {
                        bm = rotateBitmapByDegree(bm, nRotation + 90, false);
                    }
                }
                callback.onTakePhoto(bm);
            }
        });
    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Point getPreviewSize() {
        return mPreSize;
    }

    @Override
    public Point getPictureSize() {
        return mPicSize;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
                }
            });
        }
    }

    @Override
    public void setFlash(int flashMode) {
        if (flashMode == 1) {
            turnLightOn();
        } else {
            turnLightOff();
        }
    }

    /**
     * 添加buffer在Callback中加了3个Buffer，一般2个应该够用，保险起见加3个，增强相机的处理能力
     *
     * @param buffer
     */
    public void addBuffer(byte[] buffer) {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(buffer);
        }
    }

    /**
     * 在调用Camera.startPreview()接口前，我们需要setPreviewCallbackWithBuffer，而setPreviewCallbackWithBuffer
     * 之前我们需要重新addCallbackBuffer，因为setPreviewCallbackWithBuffer 使用时需要指定一个字节数组作为缓冲区，
     * 用于预览图像数据 即addCallbackBuffer，然后你在onPerviewFrame中的data才会有值
     *
     * @param callback
     */
    public void setOnPreviewFrameCallbackWithBuffer(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            Log.e("wuwang", "Camera set CallbackWithBuffer");
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
                }
            });
        }
    }

    /**
     * 获得预览大小
     *
     * @param list
     * @param th
     * @param minWidth
     * @return
     */
    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    /**
     * 获得生成照片的大小
     *
     * @param list
     * @param th
     * @param minWidth
     * @return
     */
    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    /**
     * 比较比例值
     *
     * @param s
     * @param rate
     * @return
     */
    private boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static Bitmap setTakePicktrueOrientation(int id, Bitmap bitmap) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
        bitmap = rotaingImageView(id, info.orientation, bitmap);
        return bitmap;
    }


    /**
     * 把相机拍照返回照片转正
     *
     * @param angle 旋转角度
     * @return bitmap 图片
     */
    public static Bitmap rotaingImageView(int id, int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //加入翻转 把相机拍照返回照片转正
        if (id == 1) {
            matrix.postScale(-1, 1);
        }
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree, boolean bFlip) {

        if (degree >= 360)
            degree -= 360;
        if (degree < 0)
            degree += 360;
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        android.graphics.Matrix matrix = new android.graphics.Matrix();

        matrix.postRotate(degree);

        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 开启闪光灯
     */
    public void turnLightOn() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                mCamera.setParameters(parameters);
            } else {
            }
        }
    }

    //关闭闪光灯
    public void turnLightOff() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            return;
        }
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } else {
            }
        }
    }


}
