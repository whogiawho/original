/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oms.cj.tube.camera;

import javax.microedition.khronos.opengles.GL;

import oms.cj.matrixhelper.MatrixTrackingGL;


import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class TubeCamView extends GLSurfaceView {
	public TubeCamView(Context context) {
		super(context);
	}

	private TubeCamRenderer _renderer;
    
	public TubeCamView(Context context, TubeCamRenderer r) {
		super(context);
 
        setGLWrapper(new GLSurfaceView.GLWrapper() {
            public GL wrap(GL gl) {
                return new MatrixTrackingGL(gl);
            }});
        
        _renderer = r;
        
        if(android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.FROYO)
        	this.setEGLConfigChooser(5, 6, 5, 8, 16, 0);
        else
        	this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        
		setRenderer(_renderer);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		
		this.requestFocus();
		this.setFocusableInTouchMode(true);
	}
	
    public boolean onTouchEvent(final MotionEvent event) {
    	final int action = event.getAction();
    	
        final int _x = (int) event.getX();
        final int _y = (int) event.getY();
        queueEvent(new Runnable() {
            public void run() {
                _renderer.trackCoords(new Point(_x,_y), action);
            }
        }); 
        return true;
    }
}