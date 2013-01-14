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
package oms.cj.tube.colorsetter;

import java.io.IOException;
import java.io.OptionalDataException;
import java.util.Stack;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Quaternion;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.PlayView;
import oms.cj.tube.R;
import oms.cj.tube.TubeBasicRenderer;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

public class ColorSetterView extends PlayView {
	private static final String TAG="ColorSetterView";
	private final static boolean bTest = false;
	
	public ColorSetterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG+".ColorSetterView", "constructor starting ... ");
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
		//set heightPercent
		float heightPercent = a.getFloat(R.styleable.TubePlayer_heightpercent, 1.0f);
		setHeightPercent(heightPercent);
		boolean bRandomized = a.getBoolean(R.styleable.TubePlayer_randomized, false);
		a.recycle();
		
		ColorSetterRenderer _renderer = new ColorSetterRenderer(null, bRandomized, 
				ColorSetterRenderer.TEXTURECENTER, (Activity)getContext());
		_renderer.enableFeature(TubeBasicRenderer.FEATURE_ROTATEFACE, false);
		init(_renderer);
		
		//redefine color from attrs, which is from xml
		int[][] colors = Tube.loadCubesColor(this.getContext(), attrs);
		//redefine color by loadColors() when bTest=true
		if(bTest){
			loadColors(colors);
		}
		setCubesColor(colors);
	}
	
	public ColorSetterView(Context context, ColorSetterRenderer r) {
		super(context, r);
	}
	
	private void loadColors(int[][] colors){
		int[] colorsDefined = {
			Color.green.toInt(), 
			Color.blue.toInt(), 
			Color.white.toInt(), 
			Color.yellow.toInt(), 
			Color.orange.toInt(), 
			Color.red.toInt()	
		};
		for(int i=0;i<colors.length;i++){
			for(int j=0;j<colors[i].length;j++){
				if(Tube.isVisibleFace(i, j))
					colors[i][j] = colorsDefined[j];
			}
		}
	}
	
	public void setExternalInterface(IExternal iC){
		ColorSetterRenderer _renderer = (ColorSetterRenderer) getRenderer();
		_renderer.setExternalInterface(iC);
	}

	public void setCubesColor(final int[][] colors){
		queueEvent(new Runnable(){
			public void run() {
				ColorSetterRenderer _renderer = (ColorSetterRenderer) getRenderer();
				_renderer.setCubesColor(colors);
			}
		});
	}
	public void setCubesColor(final Tube t){
		queueEvent(new Runnable(){
			public void run() {
				ColorSetterRenderer _renderer = (ColorSetterRenderer) getRenderer();
				_renderer.setCubesColor(t);
			}
		});
	}
    public void restoreFromFile(final Activity act, final String strFileName){
		queueEvent(new Runnable(){
			public void run() {
				try {
					ColorSetterRenderer _renderer = (ColorSetterRenderer) getRenderer();
					_renderer.restoreFromFile(act, strFileName);
				} catch (OptionalDataException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});    	
    }
    public void saveToFile(final Activity act, final String strFileName, 
    		final Stack<RotateAction> commands, final Quaternion q, 
    		final Color[] faceColor){
		queueEvent(new Runnable(){
			public void run() {
				try {
					TubeBasicRenderer.saveToFile(act, strFileName, commands, 
							q, faceColor, 0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
    }
	public Tube getTube() {
		ColorSetterRenderer _renderer = (ColorSetterRenderer) getRenderer();
		return _renderer.getTube();
	}
	
	public Quaternion getCurrentQuaternion(){
		ColorSetterRenderer _renderer = (ColorSetterRenderer) getRenderer();
		return _renderer.currentRotation();
	}
}