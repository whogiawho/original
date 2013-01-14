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
package oms.cj.tube.solver;

import oms.cj.tube.player.PlayerRenderer;
import android.app.Activity;
import android.util.Log;

public class SolverRenderer extends PlayerRenderer {
	private final static String TAG = "SolverRenderer";

    public SolverRenderer(String fileName, boolean randomized, int IDSwitch, 
    		Activity act){
    	super(fileName, randomized, IDSwitch, act);
    	Log.i(TAG+".SolverRenderer", "constructor starting ...");
    }
}
