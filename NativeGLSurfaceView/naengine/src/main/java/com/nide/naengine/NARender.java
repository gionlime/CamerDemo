package com.nide.naengine;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class NARender implements GLSurfaceView.Renderer  {
    private long engine_ptr_;

    public NARender() {
        System.loadLibrary("naenginelib");
        engine_ptr_ = createNAEngine(4);
    }

    public void destroy() {
        destroyNAEngine(engine_ptr_);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroyNAEngine(engine_ptr_);
    }


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.6f, 0.0f, 1.0f);
    }

    public void onDrawFrame(GL10 unused) {
        draw();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
//        nativeResizeRendering(width, height);
    }


    public void onPause() {
//        nativeDeinitRendering();
    }

    public int bla() {return 3;}

    public String getName() {
        return getNameJNI();
    }
    public Integer compute() {
        return compute(engine_ptr_,3);
    }

    private native String getNameJNI();
    private native int compute(long ptr, int value);
    private native long createNAEngine(long value);
    private native void destroyNAEngine(long ptr);
    private native void draw();
}
