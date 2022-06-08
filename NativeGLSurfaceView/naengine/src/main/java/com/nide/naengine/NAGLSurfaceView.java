package com.nide.naengine;

import android.opengl.GLSurfaceView;
import android.content.Context;

public class NAGLSurfaceView extends GLSurfaceView {
    private final NARender render;

    public NAGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        render = new NARender();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(render);
    }

}
