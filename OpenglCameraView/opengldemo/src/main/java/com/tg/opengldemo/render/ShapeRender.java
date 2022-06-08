package com.tg.opengldemo.render;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

/**
 * 绘制图形的render抽象类
 * @CreadBy ：DramaScript
 * @date 2017/7/7
 */
public abstract class ShapeRender implements GLSurfaceView.Renderer {

    protected View mView;

    public ShapeRender(View mView) {
        this.mView = mView;
    }

    public int loadShader(int type,String shaderCode){
        //根据type创建顶点着色器或者片元着色器
        int shader = GLES20.glCreateShader(type);
        //将资源加载到着色器中，并编译
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
