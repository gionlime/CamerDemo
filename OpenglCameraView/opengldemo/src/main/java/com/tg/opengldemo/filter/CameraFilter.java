package com.tg.opengldemo.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.util.Arrays;

/**
 * @CreadBy ：DramaScript
 * @date 2017/7/10
 */
public class CameraFilter extends BaseFilter {

    //连续矩阵句柄
    private int mHCoordMatrix;
    //连续矩阵值
    private float[] mCoordMatrix= Arrays.copyOf(OM,16);

    public CameraFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        //创建opengl2
        createProgramByAssetsFile("shader/oes_base_vertex.sh","shader/oes_base_fragment.sh");
        mHCoordMatrix= GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");
    }

    public void setCoordMatrix(float[] matrix){
        this.mCoordMatrix=matrix;
    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,getTextureId());
        GLES20.glUniform1i(mHTexture,getTextureType());
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0);
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
