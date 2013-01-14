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

import javax.microedition.khronos.opengles.GL10; 

import oms.cj.tube.PlayRenderer;
import oms.cj.tube.Ray;
import oms.cj.tube.TubeBasicRenderer;
import oms.cj.tube.component.CenterTexture;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.ITubeTexture;
import oms.cj.tube.component.Tube;
import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

public class ColorSetterRenderer extends PlayRenderer {
	private final static String TAG = "ColorSetterRenderer";
	public final static int TEXTUREID_OFFSET = TubeBasicRenderer.TEXTUREID_OFFSET + 2;
    public final static int TEXTURECENTER = TEXTUREID_OFFSET;
    
    private ITubeTexture[] iTextures = new ITubeTexture[1];

    public ColorSetterRenderer( String fileName, boolean randomized, 
    		int IDSwitch, Activity act){
    	super(fileName, randomized, IDSwitch, act);
    }
    
    @Override
    protected void initTextureIDs(GL10 gl){
    	super.initTextureIDs(gl);
    	
    	iTextures[0] = new CenterTexture(Tube.CubesEachTube, Cube.FacesEachCube, 
    			getContext(), gl);
    }
    
    @Override
    protected ITubeTexture getTextureIDs(int idSwitch){
    	ITubeTexture iTexture;
    	
    	switch(idSwitch){
    	case TEXTURENOID:
    	case TEXTURECUBEID:
    	default:
    		iTexture = super.getTextureIDs(idSwitch);
    		break;

    	case TEXTURECENTER:
    		Log.i(TAG, "CenterTexture is selected!");
    		iTexture = iTextures[0];
    		break;
    	}
    	
    	return iTexture;
    }
	
    IExternal mIExternal;
	public void setExternalInterface(IExternal iC){
		mIExternal = iC;
	}
	
	private final static int MAXMOVETHRESHOLD = 10;
	private final static int SETCOLOR = 0;
	private final static int MOVECUBE = 1;
	private int mState = SETCOLOR;
	private int moveCount = 0;
	@Override
	public void trackCoords(Point pt, int motionState){
		switch(motionState){
		case MotionEvent.ACTION_UP:
			if(getState() == SETCOLOR){
	            Point downPoint = translate(pt);
	            int[] cubeCoord = {0,0,0};

	            Ray ray = getPickRay(downPoint, mMatrixGrabber);
	            if(intersect(ray, cubeCoord, null)){
	            	Log.i(TAG+".trackCoords", "startCoord[0]=" + cubeCoord[0]);
	                Log.i(TAG+".trackCoords", "startCoord[1]=" + cubeCoord[1]);
	                Log.i(TAG+".trackCoords", "startCoord[2]=" + cubeCoord[2]);
	                setCubeColor(cubeCoord[0], cubeCoord[1], mIExternal.getCurrentColor());
	            }  
			} else {
				resetState();
			}
			moveCount=0;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG+".trackCoords", "motionState=ACTION_MOVE");
			moveCount++;
			if(moveCount>=MAXMOVETHRESHOLD)
				setState(MOVECUBE);
			break;
		}

		super.trackCoords(pt, motionState);
	}
	
	private void resetState(){
		mState = SETCOLOR;
	}
	private void setState(int state){
		mState = state;
	}
	private int getState(){
		return mState;
	}
}
