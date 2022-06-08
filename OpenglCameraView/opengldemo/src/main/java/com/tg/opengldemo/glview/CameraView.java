package com.tg.opengldemo.glview;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;

import com.tg.opengldemo.manager.CameraHelper;
import com.tg.opengldemo.manager.ICameraHelper;
import com.tg.opengldemo.render.CameraRender;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @CreadBy ：DramaScript
 * @date 2017/7/10
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private CameraHelper manager;
    private CameraRender cameraRender;
    private int cameraId=1;

    private Runnable mRunnable;
    private Camera mCamera;

    private int mOrientation = 0;

    private Context context;

    public CameraHelper getManager() {
        return manager;
    }

    public int getCameraId() {
        return cameraId;
    }

    public CameraView(Context context) {
        super(context);
        this.context = context;
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        manager = new CameraHelper();
        AlbumOrientationEventListener mAlbumOrientationEventListener = new AlbumOrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }else {
            Log.d("tag", "Can't Detect Orientation");
        }
        cameraRender = new CameraRender(getResources());
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        cameraRender.onSurfaceCreated(gl,config);
        if(mRunnable!=null){
            mRunnable.run();
            mRunnable=null;
        }
        manager.open(cameraId);
        cameraRender.setCameraId(cameraId);
        Point point=manager.getPreviewSize();
        cameraRender.setDataSize(point.x,point.y);
        manager.setPreviewTexture(cameraRender.getSurfaceTexture());
        cameraRender.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        manager.preview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        cameraRender.setViewSize(width,height);
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        cameraRender.onDrawFrame(gl);

    }

    @Override
    public void onPause() {
        super.onPause();
        manager.close();
    }

    public void switchCamera(){
        mRunnable=new Runnable() {
            @Override
            public void run() {
                manager.close();
                cameraId=cameraId==1?0:1;
            }
        };
        onPause();
        onResume();
    }

    public void takePic(ICameraHelper.TakePhotoCallback callback){
        manager.takePhoto(callback,mOrientation);
    }

    public void setFlash(int flashMode){
        manager.setFlash(flashMode);
    }

    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            //保证只返回四个方向
            int newOrientation = ((orientation + 45) / 90 * 90) % 360;
            if (newOrientation != mOrientation) {
                mOrientation = newOrientation;
                Log.e("MJHTEST", "mOrientation = " + mOrientation);
                //返回的mOrientation就是手机方向，为0°、90°、180°和270°中的一个
            }
        }
    }

    /**
     * 手动聚焦
     *
     * @param point 触屏坐标
     */
    protected boolean onFocus(Point point, Camera.AutoFocusCallback callback) {
        mCamera = manager.getmCamera();
        if (mCamera == null) {
            Log.e("tag","------------------------1");
            return false;
        }

        Camera.Parameters parameters = null;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tag","------------------------2");
            return false;
        }
        //不支持设置自定义聚焦，则使用自动聚焦，返回

        if(Build.VERSION.SDK_INT >= 14) {

            if (parameters.getMaxNumFocusAreas() <= 0) {
                return focus(callback);
            }

            Log.i("tag", "onCameraFocus:" + point.x + "," + point.y);

            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            int left = point.x - 300;
            int top = point.y - 300;
            int right = point.x + 300;
            int bottom = point.y + 300;
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
            parameters.setFocusAreas(areas);
            try {
                //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
                //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                Log.e("tag","------------------------3");
                return false;
            }
        }


        return focus(callback);
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        try {
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("tag","------------------------4");
            return false;
        }
        return true;
    }
}
