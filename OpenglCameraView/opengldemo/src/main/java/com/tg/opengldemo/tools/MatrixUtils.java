package com.tg.opengldemo.tools;

/**
 * @CreadBy ：DramaScript
 * @date 2017/7/10
 */
public class MatrixUtils {

    MatrixUtils(){

    }

    /**
     * 获取最初始的矩阵值
     * @return
     */
    public static float[] getOriginalMatrix(){
        return new float[]{
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
    }
}
