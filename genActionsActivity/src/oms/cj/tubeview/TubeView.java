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
package oms.cj.tubeview;

import java.io.IOException;
import java.io.StreamCorruptedException;
import javax.microedition.khronos.opengles.GL;
import oms.cj.genActions.R;
import com.example.android.apis.graphics.spritetext.MatrixTrackingGL;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class TubeView extends GLSurfaceView {
	private static final String TAG="TubeView";
	private TubeRenderer _renderer;
	private float mHeightPercent = 1.0f;
    
	private void init(TubeRenderer _renderer){
		//_renderer.enableFeature(TubeRenderer.FEATURE_ROTATECUBE, false);
		//_renderer.enableFeature(TubeRenderer.FEATURE_ROTATEFACE, false);
		setRenderer(_renderer);
		requestFocus();
		setFocusableInTouchMode(true);
		
        setGLWrapper(new GLSurfaceView.GLWrapper() {
            public GL wrap(GL gl) {
                return new MatrixTrackingGL(gl);
            }});
	}
	
	public TubeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubeView);
		boolean bRandomized = a.getBoolean(R.styleable.TubeView_randomized, false);
		a.recycle();
		
		_renderer = new TubeRenderer(null, bRandomized, TubeRenderer.TEXTURENOID,(Activity)getContext());
		init(_renderer);
	}
	
	public TubeView(Context context, TubeRenderer r) {
		super(context);
		
        _renderer = r;
        init(_renderer);
	}
	
	public void enableFeature(int feature, boolean bSwitch){
		_renderer.enableFeature(feature, bSwitch);
	}
	public void setHeightPercent(float heightPercent){
		mHeightPercent = heightPercent;
	}
	public void getActionsFrom(String commandFile) throws StreamCorruptedException, IOException, ClassNotFoundException{
		_renderer.getActionsFrom(commandFile);
	}
	public void setCubesColor(final int[][] colors){
		queueEvent(new Runnable(){
			public void run() {
				_renderer.setCubesColor(colors);
			}
		});
	}
	public void forward(){
		queueEvent(new Runnable(){
			public void run() {
				_renderer.forwardRotate();
			}
		});
	}
	public void backward(){
		queueEvent(new Runnable(){
			public void run() {
				_renderer.backwardRotate();
			}
		});
	}
	public void play(){
		queueEvent(new Runnable(){
			public void run() {
				_renderer.play();
			}
		});
	}
	public void reset(){
		queueEvent(new Runnable(){
			public void run() {
				_renderer.reset();
			}
		});
	}
	
    @Override
    protected void onMeasure(int width, int height){
    	Log.i(TAG+".onMeasure", "width = " + View.MeasureSpec.toString(width));
    	Log.i(TAG+".onMeasure", "height = " + View.MeasureSpec.toString(height));
    	setMeasuredDimension(View.MeasureSpec.getSize(width), (int) (View.MeasureSpec.getSize(height)*mHeightPercent));
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