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

import javax.microedition.khronos.opengles.GL10; 
import javax.vecmath.Vector3f;


import oms.cj.tube.Config;
import oms.cj.tube.TubeBasicRenderer;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.ITubeTexture;
import oms.cj.tube.component.Quaternion;
import oms.cj.tube.component.TransparentTexture;
import oms.cj.tube.component.Tube;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;


public class TubeCamRenderer extends TubeBasicRenderer{
    //const variables
	private final static String TAG = "TubeInCameraRenderer";
	public final static int TEXTUREID_OFFSET = TubeBasicRenderer.TEXTUREID_OFFSET + 2;
    public final static int TEXTURETRANSPARENT = TEXTUREID_OFFSET;
    //define kinds of textureIDs here
    private ITubeTexture[] iTextures = new ITubeTexture[1];
    private final static Vector3f delta = new Vector3f(-1, 0, 0);

    public TubeCamRenderer(String fileName, boolean randomized, int IDSwitch, Activity act){
    	super(fileName, randomized, IDSwitch, act);
    	setBackground(new Color(0,0,0,0.0f));
    	
    	SharedPreferences settings = act.getSharedPreferences(Config.PREFS_NAME, 0);
    	int size = settings.getInt(Config.ref[2], Tube.DefaultSize);
    	getTube().setSize(size+2);
    	super.setTranslate(delta);
    }
	public TubeCamRenderer(String fileName, boolean randomized, int IDSwitch, Activity act, Quaternion q){
		this(fileName, randomized, IDSwitch, act);
		setCurrentQuaternion(q);
	}
    public void setColor(Color c){
    	getTube().setColor(c);
    }

    @Override
    protected void initTextureIDs(GL10 gl){
    	super.initTextureIDs(gl);
    	iTextures[0] = new TransparentTexture(Tube.CubesEachTube, Cube.FacesEachCube, getContext(), gl);
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

    	case TEXTURETRANSPARENT:
    		Log.i(TAG+".getTextureIDs", "TransparentTexture is selected!");
    		iTexture = iTextures[0];
    		break;
    	}
    	
    	return iTexture;
    }
    
    public float[][] getTubeCenters(int side){
    	return getTube().getCenters(side);
    }
}
