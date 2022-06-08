package com.nide.nativeandroid;

public class NativeRenderer {
    public NativeRenderer() {
        System.loadLibrary("native-lib");
    }

    public int bla() {return 3;}


    public String getName() {
        return getNameJNI();
    }

    private native String getNameJNI();
}
