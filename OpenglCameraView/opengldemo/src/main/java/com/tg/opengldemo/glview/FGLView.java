package com.tg.opengldemo.glview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.tg.opengldemo.render.FGLRender;
import com.tg.opengldemo.render.ShapeRender;
/**
 * @CreadBy ：DramaScript
 * @date 2017/8/17
 */
public class FGLView extends GLSurfaceView {

    private FGLRender renderer;

    public FGLView(Context context) {
        super(context);
    }

    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        // 当使用OpenGLES 2.0时，你必须在GLSurfaceView构造器中调用另外一个函数，它说明了你将要使用2.0版的AP
        setEGLContextClientVersion(2);
        setRenderer(renderer=new FGLRender(this));
        // 设置只有在数据发生改变时才绘制
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setShape(Class<? extends ShapeRender> clazz){
        try {
            renderer.setShape(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}