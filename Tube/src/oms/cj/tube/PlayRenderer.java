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

import oms.cj.tube.R;
import oms.cj.tube.component.ITubeTexture;
import android.app.Activity;
import android.util.Log;

public class PlayRenderer extends TubeBasicRenderer{
	private final static String TAG = "PlayRenderer";
	
    public PlayRenderer( String fileName, boolean randomized, 
    		int IDSwitch, Activity act){
    	super(fileName, randomized, IDSwitch, act);
    }

    @Override
    protected ITubeTexture getTextureIDs(int idSwitch){
    	int basicIDSwitch=0;
    	
    	switch(idSwitch){
    	case R.id.noid:
    		basicIDSwitch = TEXTURENOID;
    		break;
    	case R.id.cubeid:
    		Log.i(TAG+".getTextureIDs", "CubeIDTexture is selected!");
    		basicIDSwitch = TEXTURECUBEID;
    		break;
    	default:
    		basicIDSwitch = idSwitch;
    		break;
    	}
    	
    	return super.getTextureIDs(basicIDSwitch);
    }
}
