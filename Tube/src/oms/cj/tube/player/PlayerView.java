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
package oms.cj.tube.player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.Stack;
import oms.cj.tube.PlayView;
import oms.cj.tube.R;
import oms.cj.tube.TubeBasicRenderer;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;


public class PlayerView extends PlayView {
	private static final String TAG="PlayerView";	
	
	public PlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(TAG+".PlayerView", "constructor starting ...");
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TubePlayer);
		boolean bRandomized = a.getBoolean(R.styleable.TubePlayer_randomized, false);
		boolean bOverride = a.getBoolean(R.styleable.TubePlayer_override, true);
		a.recycle();
		
		PlayerRenderer _renderer = new PlayerRenderer(null, bRandomized, 
				TubeBasicRenderer.TEXTURENOID,(Activity)getContext());
		init(_renderer);
		
		if(bOverride) {
			//redefine color from attrs, which is from xml
			int[][] colors = Tube.loadCubesColor(this.getContext(), attrs);
			setCubesColor(colors);
		}
	}
	
	public PlayerView(Context context, PlayerRenderer r) {
		super(context);
	}

	public void enableFeature(int feature, boolean bSwitch){
		PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
		_renderer.enableFeature(feature, bSwitch);
	}

    @SuppressWarnings("unchecked")
	private Stack<RotateAction> _getActionsFrom(String commandFile) 
    throws StreamCorruptedException, IOException, ClassNotFoundException {
    	Stack<RotateAction> returnCommands = new Stack<RotateAction>();
    	returnCommands.clear();
		
		try {  
			// firstly open commandFile in assets
			final AssetManager mAssetManager = getContext().getResources().getAssets();
			ObjectInputStream objectIn = new ObjectInputStream(
					mAssetManager.open(commandFile, AssetManager.ACCESS_BUFFER));
			returnCommands = (Stack<RotateAction>) objectIn.readObject();
			objectIn.close();
		} catch(FileNotFoundException e) { 
			// otherwise open commandFile in /data/data/.../files
			FileInputStream fis = getContext().openFileInput(commandFile);
			ObjectInputStream objectIn = new ObjectInputStream(
					new BufferedInputStream(fis));
			returnCommands = (Stack<RotateAction>) objectIn.readObject();
			objectIn.close();
		}
		
		return returnCommands;
    }
    
	public Stack<RotateAction> getActionsFrom(String commandFile) 
	throws StreamCorruptedException, IOException, ClassNotFoundException{
		PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
		_renderer.clearBeforeSolve();
		Stack<RotateAction> commands = _getActionsFrom(commandFile);
		
		_renderer.setCommands(commands);
		
		return commands;
	}
	
	public void setCubesColor(final int[][] colors){
		queueEvent(new Runnable(){
			public void run() {
				PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
				_renderer.setCubesColor(colors);
			}
		});
	}
	public void setCubesColor(final int[] colors){
		queueEvent(new Runnable(){
			public void run() {
				PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
				_renderer.setCubesColor(colors);
			}
		});
	}
	public void setCubesColor(final Tube t){
		queueEvent(new Runnable(){
			public void run() {
				PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
				_renderer.setCubesColor(t);
			}
		});
	}
	
	public void forward(){
		queueEvent(new Runnable(){
			public void run() {
				PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
				_renderer.forward();
			}
		});
	}
	public void backward(){
		queueEvent(new Runnable(){
			public void run() {
				PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
				_renderer.backward();
			}
		});
	}
	public void play(final boolean bPlay){
		queueEvent(new Runnable(){
			public void run() {
				PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
				_renderer.play(bPlay);
			}
		});
	}
	public void reset(final int[] colors){
		queueEvent(new Runnable(){
			public void run() {
				PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
				_renderer.reset(colors);
			}
		});
	}
	public void setTubePlayer(TubePlayer tubePlayer){
		PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
		_renderer.setTubePlayer(tubePlayer);
	}
	public void setCommands(Stack<RotateAction> commands){
		PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
		_renderer.setCommands(commands);
	}
	public Tube getTube(){
		PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
		return _renderer.getTube();
	}
	public void clearBeforeSolve(){
		PlayerRenderer _renderer = (PlayerRenderer) getRenderer();
		_renderer.clearBeforeSolve();
	}
}