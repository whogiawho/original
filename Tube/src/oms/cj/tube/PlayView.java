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
package oms.cj.tube;

import javax.microedition.khronos.opengles.GL;

import oms.cj.matrixhelper.MatrixTrackingGL;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class PlayView extends GLSurfaceView {
	private static final String TAG="PlayView";
	
	private float mHeightPercent = 1.0f;
	private PlayRenderer _renderer;
	
	public PlayView(Context context) {
		super(context);
	}
    
	public PlayRenderer getRenderer(){
		return _renderer;
	}
	
	protected void init(PlayRenderer r){
	    _renderer = r;
		setRenderer(_renderer);
		requestFocus();
		setFocusableInTouchMode(true);
		
        setGLWrapper(new GLSurfaceView.GLWrapper() {
            public GL wrap(GL gl) {
                return new MatrixTrackingGL(gl);
            }});
	}
	
	public PlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PlayView(Context context, PlayRenderer r) {
		super(context); 
	        
	    init(r);
	}
	
	public void setHeightPercent(float heightPercent){
		mHeightPercent = heightPercent;
	}
	
    @Override
    protected void onMeasure(int width, int height){
    	//Log.i(TAG+".onMeasure", "width = " + View.MeasureSpec.toString(width));
    	//Log.i(TAG+".onMeasure", "height = " + View.MeasureSpec.toString(height));
    	setMeasuredDimension(View.MeasureSpec.getSize(width), 
    			(int) (View.MeasureSpec.getSize(height)*mHeightPercent));
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		Log.i(TAG, "onKeyDown(...): " + "keyCode" + keyCode);
		return super.onKeyDown(keyCode, event);
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