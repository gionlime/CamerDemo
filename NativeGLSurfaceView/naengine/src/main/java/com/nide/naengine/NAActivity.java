package com.nide.naengine;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class NAActivity extends Activity {
    private GLSurfaceView gLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = new NAGLSurfaceView(this);
        setContentView(gLView);
    }
}