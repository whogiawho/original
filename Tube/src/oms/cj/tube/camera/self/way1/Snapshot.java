package oms.cj.tube.camera.self.way1;

import java.util.ArrayList;
import oms.cj.tube.R;
import oms.cj.tube.camera.ICameraPicture;
import oms.cj.tube.camera.self.ISelfCameraPicture;
import oms.cj.tube.component.Tube;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public abstract class Snapshot extends oms.cj.tube.camera.self.Snapshot implements ISelfCameraPicture {
	private final static String TAG = "self.way1.Snapshot";
      
	private boolean[] mSideFlags = {
			false, 	//FRONTSIDE
			false, 	//RIGHTSIDE
			false, 	//BACKSIDE
			false, 	//LEFTSIDE
			false, 	//TOPSIDE
			false, 	//BOTTOMSIDE
	};
	private final static ArrayList<Integer> predefinedSeqs = new ArrayList<Integer>();
	static {
		predefinedSeqs.add(FRONTSIDE);
		predefinedSeqs.add(RIGHTSIDE);
		predefinedSeqs.add(BACKSIDE);
		predefinedSeqs.add(LEFTSIDE);
		predefinedSeqs.add(TOPSIDE);
		predefinedSeqs.add(BOTTOMSIDE);
	}
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    	for(int i=0;i<mSideFlags.length;i++){
    		if(mSideFlags[i])
    			setColorsVisibleSide(i);
    	}
    }
    
	@Override 
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	Log.i(TAG+".onCreate", "being called!");
		
    }

    @Override
    public String getHintString(int side){
    	String hintString="";
    	
    	if(side>=0&&side<6){
    		hintString = mHintString[side];
    	}
    	
    	return hintString;
    }

    //this interface function is not used in this class, so there is no implementation
	@Override
	public void onPictureTaken(byte[] yuvData, byte[] rgbData, int width, int height) {
		return;
	}

	@Override
	public int getWayType() {
		return ICameraPicture.TYPE2;
	}

	@Override
	public Handler getHandler() {
		return mHandler;
	}
	
	private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case SCANMATCHFAIL:
    			break;
    		case SCANMATCHOK:
    			int[] colors = (int[]) msg.obj;		//msg.obj is int[], data interface
    			onScanComplete(colors);
    			break;
    		default:
    			Log.e(TAG+"mHandler.handleMessage", "invalide msg type = " + msg.what);
    		}
    	}
	};
	
	protected void onScanComplete(int[] colors){
		//turn off preview
		mPreview.setEnabled(false);
		//set mPreviewSwitch imagebutton visible
		mPreviewSwitch.setVisibility(View.VISIBLE);
		//set scanTV
		int scanState = SCANCOMPLETE;
		String scanString = getScanHintString(scanState);
		mScanTV.setText(scanString);
		
		int currentSide = getState();
		int tSide = conv2TubeSide[currentSide];
		for(int j=0;j<Tube.CubesEachSide;j++){
			//put color to mColors
			setTubeColor(tSide*Tube.CubesEachSide+j, colors[j]);
		}
		setFlag(mSideFlags, currentSide, true);
		// set colors of visibleSides[]
		setColor2VisibleSide(colors, currentSide);
		
		if(allFlagsSet(mSideFlags)){
			// return colors to TubeSolverActivity1
	    	Intent i = new Intent();
	    	Bundle b = new Bundle();
	    	b.putIntArray(TUBECOLORS, getTubeColors());
	    	i.putExtras(b);
	    	setResult(RESULT_OK, i);
	    	finish();
		} else {
			int next = nextSequence(predefinedSeqs, currentSide);
			while(getFlag(mSideFlags, next))
				next = nextSequence(predefinedSeqs, next);
			mHintTV.setText(getHintString(next));

			setState(next);
		}
	}
	
	@Override
	public void onClick(View v) {
		Log.i(TAG+".onClick", "being called!");
		
		int id = v.getId();
		if(id==R.id.clickcamera){
			//turn on preview
			mPreview.setEnabled(true);
			//set mPreviewSwitch imagebutton invisible
			mPreviewSwitch.setVisibility(View.INVISIBLE);
			//set scanTV
			int scanState = SCANINPROGRESS;
			String scanString = getScanHintString(scanState);
			mScanTV.setText(scanString);			
		}
	}
}
