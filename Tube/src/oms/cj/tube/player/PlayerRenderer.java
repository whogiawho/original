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

import java.util.ArrayList;
import java.util.Stack;

import oms.cj.tube.ITubeRenderCallbacks;
import oms.cj.tube.PlayRenderer;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.flysymbol.FlySymbol;
import android.app.Activity;
import android.util.Log;
import android.widget.TextSwitcher;

public class PlayerRenderer extends PlayRenderer implements ITubeRenderCallbacks{
	private final static String TAG = "PlayerRenderer";

    private final static int FORWARD = 0;
    private final static int BACKWARD = 1;
    private final static int PLAY = 2;
    private final static int PAUSE = 3;
    private ArrayList<Integer> listRequest = new ArrayList<Integer>();
    private Stack<RotateAction> mCommands = new Stack<RotateAction>();
    private int mCurrentPosition = 0;
    private TubePlayer mTubePlayer;
    
    public void setTubePlayer(TubePlayer tubePlayer){
    	mTubePlayer = tubePlayer;
    }
    public void setCommands(Stack<RotateAction> commands){
    	mCommands = commands;
    }
    public void clearBeforeSolve(){
		mCurrentPosition = 0;
    	listRequest.clear();
    	getTube().clearRequestQueue();
    	//send a message to update PLAY button
    	updatePlayButton(false);
    	
      	mCommands.clear();
    }
    
    public PlayerRenderer( String fileName, boolean randomized, 
    		int IDSwitch, Activity act){
    	super(fileName, randomized, IDSwitch, act);
    	
    	getTube().setCallbacks(this);
    }
    
    private int getLargestIndex(int type){
    	int idx = -1;
    	
    	for(int i=0;i<listRequest.size();i++){
    		if(listRequest.get(i)==type)
    			idx = i;
    	}
    	
    	return idx;
    }
    
    private void startFlySymbolAnimation(final FlySymbol flysym, final int dir){
    	mTubePlayer.post(new Runnable(){

			@Override
			public void run() {
				switch(dir){
				case FORWARD:
					flysym.forward();
					break;
				case BACKWARD:
					flysym.backward();
					break;
				}
			}
    		
    	});
    }
    private void updateRemark(final TextSwitcher switcher, final String remark){
    	mTubePlayer.post(new Runnable(){

			@Override
			public void run() {
				switcher.setText(remark);
			}
    		
    	});
    }
    public void onRotateStart(RotateAction r){
    	if(mTubePlayer!=null){
    		FlySymbol flysym = mTubePlayer.getFlySymbol();
    		if(flysym!=null){
    			int dir = -1;
    	    	int type = listRequest.get(0);
    			if(type==FORWARD||type==PLAY)
    				dir = FORWARD;
    			else if(type==BACKWARD)
    				dir = BACKWARD;
    			else {
    				Log.e(TAG+".onRotateStart", "invalid type = " + type);
    				return;
    			}
    						
    	    	startFlySymbolAnimation(flysym, dir);
    		}
    		
    		TextSwitcher switcher = mTubePlayer.getSwitcher();
    		if(switcher!=null){
    			updateRemark(switcher, r.getRemark());
    		}    		
    	}
    }
    
    public void onRotateFinish(RotateAction r){
    	//remove the 1st request if it is not PLAY; 
    	//Info: the 1st request is the request which is just finished 
    	if(listRequest.size()!=0 && listRequest.get(0)!=PLAY){
    		listRequest.remove(0);
    	}
    	
    	//check if there is a PAUSE in queue; get the largest PAUSE's index
    	int idx = getLargestIndex(PAUSE);
    	if(idx!=-1){
    		//yes, clear all requests before PAUSE in queue
    		for(int i=0;i<=idx;i++)
    			listRequest.remove(0);
    	}
    	
    	//play subsequent request
    	boolean bContinue = false;
    	while(!bContinue && listRequest.size()!=0){
            int currentRequest = listRequest.get(0);
        	switch(currentRequest){
        	case PLAY:
        	case FORWARD:
        		if(mCurrentPosition<mCommands.size()){
        			forwardRotate();
        			bContinue = true;
        		}else {
        			listRequest.remove(0);
        			if(currentRequest==PLAY) {
        				//send a message to update PLAY button
        				updatePlayButton(false);
        			}
        		}
        		break;
        	case BACKWARD:
        		if(mCurrentPosition>0){
        			backwardRotate();
        			bContinue = true;
        		}else
        			listRequest.remove(0);
        		break;
        	default:
        		Log.e(TAG+".onRotateFinish", "exception! currentRequest =" + currentRequest);
        		break;
        	}  			
    	}
    }
    
    private void updatePlayButton(final boolean bPlay){
    	mTubePlayer.post(new Runnable(){
			@Override
			public void run() {
				Log.i(TAG+".run", "being called!");
				Log.i(TAG+".run", "bPlay = " + bPlay);
				mTubePlayer.togglePlayButton(bPlay);
			}		
    	});
    }
    private void forwardRotate(){	
    	RotateAction r = mCommands.get(mCurrentPosition);
    	rotate(r);
    	mCurrentPosition++;
    }
    private void backwardRotate(){
    	mCurrentPosition--;
    	RotateAction r = mCommands.get(mCurrentPosition);
    	rotate(r.reverse());
    }
    public void forward(){
    	listRequest.add(FORWARD);
    	if(listRequest.size()==1){
    		if(mCurrentPosition<mCommands.size())
    			forwardRotate();
    		else
    			listRequest.remove(0);
    	}
    }
    public void backward(){
    	listRequest.add(BACKWARD);
    	if(listRequest.size()==1){
    		if(mCurrentPosition>0)
    			backwardRotate();
    		else
    			listRequest.remove(0);
    	}
    }
    public void play(boolean bPlay){
    	if(bPlay) {
    		listRequest.add(PLAY);
    		if(listRequest.size()==1) {
    			if(mCurrentPosition<mCommands.size())
    				forwardRotate();
    			else {
    				listRequest.remove(0);
    				updatePlayButton(false);
    			}
    		}
    	} else {
    		if(listRequest.size()!=0)
    			listRequest.add(PAUSE);
    	}
    }
    public void reset(int[] colors){
    	mCurrentPosition = 0;
    	listRequest.clear();
    	getTube().clearRequestQueue();
    	//send a message to update PLAY button
    	updatePlayButton(false);
    	
    	setCubesColor(colors);
    }
}
