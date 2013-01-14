package oms.cj.tube.camera.ex.way2;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Matrix4f;

import oms.cj.tube.R;
import oms.cj.tube.TubeBasicRenderer;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.camera.ICameraPicture;
import oms.cj.tube.camera.TubeCamRenderer;
import oms.cj.tube.camera.TubeCamView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class Snapshot extends oms.cj.tube.camera.ex.Snapshot implements ICameraPicture{
	private final static String TAG = "way2.Snapshot";
	public final static int PHASE0 = 0;
	public final static int PHASE1 = 1;
	private final static int[][] phase2tSides = {
		{Tube.top, Tube.front, Tube.right},
		{Tube.bottom, Tube.left, Tube.back},
	};
	private final static int[][] phase2sSides = {
		{TOPSIDE, FRONTSIDE, RIGHTSIDE},
		{BOTTOMSIDE, LEFTSIDE, BACKSIDE},
	};
	private final static ArrayList<Integer> predefinedSeqs = new ArrayList<Integer>();
	static {
		predefinedSeqs.add(PHASE0);
		predefinedSeqs.add(PHASE1);
	}
	
	private boolean[] mPhaseFlags = {
			false, 	false,
	};
	
    @Override
    public void onResume(){
    	super.onResume();
    	
    	mHintTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
    	for(int i=0;i<mPhaseFlags.length;i++){
    		if(mPhaseFlags[i]){
    			int[] sSides = phase2sSides[i];
    			for(int j=0;j<sSides.length;j++)
    				this.setColorsVisibleSide(sSides[j]);
    		}
    	}
    }
    
    @Override
    public String getHintString(int phase){
    	String hintString="";
    	
    	if(phase>=0&&phase<phase2sSides.length){
    		int[] sSides = phase2sSides[phase];
    		for(int i=0;i<sSides.length;i++){
    			hintString = hintString + mHintString[sSides[i]] + "\n";
    		}
    	}
    	
    	return hintString;
    }
	
    @Override
    public void initTube(String fileName, boolean randomized, Activity act, int phase){
        int idSwitch = TubeCamRenderer.TEXTURETRANSPARENT;

        if(phase<0||phase>=2){
        	Log.e(TAG+".initTube", "exception! invalid phase ="+phase);
        	Log.i(TAG+".initTube", "using default phase 0");
        	phase = 0;
        }
        
        // do something per requestCode
        _renderer = new TubeCamRenderer(fileName, randomized, idSwitch, act);
        _renderer.enableFeature(TubeBasicRenderer.FEATURE_ROTATEFACE, true);
        
		String key = "" + PHASE0 + phase;
		Log.i(TAG+".initTube", "key="+key);
		ArrayList<RotateAction> r = routineMap.get(key);
		for(RotateAction action: r){
			//maybe bugs here, _renderer.rotate can only be called from GLThread
			_renderer.rotate(action);
		}
        
        _tubeview=new TubeCamView(this, _renderer);
        _renderer.setColor(Color.transparent);
        
        setContentView(_tubeview);
    }
    
    //comment 1 - 2011/06/25
    //I do not understand why function getRotateMatrix v1.0 does not work for PHASE1
    //so another way to getProjectPoint is used instead for way2.Snapshot, that is convertCenter()
    //comment 2 - 2011/06/25
    //the key to answer this problem lies in the point to know the matrix transformation order
    /*    v1.0 
    private Matrix4f getRotateMatrix(int phase){
    	Matrix4f m = new Matrix4f();
    	m.setIdentity();
    	
    	switch(phase){
    	case PHASE1:
    		Matrix4f m1=new Matrix4f();
    		m.rotX((float) Math.PI);
    		m1.rotY((float) (Math.PI/2));
    		m.mul(m1);
    		break;
    	case PHASE0:	//nothing is done
    		break;
    	}
    	return m;
    }*/
    private void convertCenter(int[] orig, int[] target){
    	target[0] = orig[0];
    	target[1] = orig[1];
    	
    	if(orig[0]==Tube.front||orig[0]==Tube.right||orig[0]==Tube.top)
    		return;
    	else {
    		int[][] mapping = {
    				{8, 5, 2, 7, 4, 1, 6, 3, 0},    //left
    				{8, 5, 2, 7, 4, 1, 6, 3, 0},	//back
    				{8, 5, 2, 7, 4, 1, 6, 3, 0},	//bottom
    		};
    		switch(orig[0]){
    		case Tube.left:
    			target[0]=Tube.front;
    			break;
    		case Tube.bottom:
    			target[0]=Tube.top;
    			break;
    		case Tube.back:
    			target[0]=Tube.right;
    			break;
    		}
    		target[1]=mapping[0][orig[1]];
    	}
    }
	@Override
	public void onPictureTaken(byte[] yuvData, byte[] rgbData, int width, int height){
		int currentPhase = getState();
		int[] tSides = phase2tSides[currentPhase];
		int[][] colors = new int[3][Tube.CubesEachSide];
		
		for(int i=0;i<tSides.length;i++){
			int tSide = tSides[i];
			for(int j=0;j<Tube.CubesEachSide;j++){
				int[] orig = {tSide, j};
				int[] target=new int[2];
				convertCenter(orig, target);
				float[] center = getTubeCenters(target[0], target[1]);
				//map center of 3D to a point in projected view
				Matrix4f m = new Matrix4f(); m.setIdentity();
				float[] projectCenter = _renderer.getProjectPoint(center, m);
				
				int iColor1 = getRGBColor(rgbData, projectCenter, width, height);
				int dstIdx = tSide * Tube.CubesEachSide + j;
				
				//translate color to its closet color
				Color c1 = new Color(iColor1);
				Color c2 = Color.closestColor(c1); 
				Log.i(TAG+".onPictureTaken", Tube.LAYERSTR_F[tSide]+":color="+c2.toString());
				
				//put color to mColors
				setTubeColor(dstIdx, c2);
				colors[i][j] = c2.toInt();
			}
		}
		setFlag(mPhaseFlags, currentPhase, true);
		// set colors of visibleSides[]
		for(int i=0;i<3;i++){
			int[] sSides = phase2sSides[currentPhase];
			setColor2VisibleSide(colors[i], sSides[i]);
		}
		
		Log.i(TAG+".onPictureTaken", "phase 0");
		if(allFlagsSet(mPhaseFlags)){
			// return colors to TubeSolverActivity1
	    	Intent i = new Intent();
	    	Bundle b = new Bundle();
	    	b.putIntArray(TUBECOLORS, getTubeColors());
	    	i.putExtras(b);
	    	setResult(RESULT_OK, i);
	    	finish();
		} else {
			int next = nextSequence(predefinedSeqs, currentPhase);
			while(getFlag(mPhaseFlags, next))
				next = nextSequence(predefinedSeqs, next);
			mHintTV.setText(getHintString(next));
			String key = ""+currentPhase+next;
			ArrayList<RotateAction> r = routineMap.get(key);
			//maybe bugs here, _renderer.rotate can only be called from GLThread
			for(RotateAction action: r){
				_renderer.rotate(action);
			}
			setState(next);
			restartPreview();
		}
    	
    	bTakingPicture = false;
	}
	
	@Override
	public void onClick(View v) {		
		Log.i(TAG+".onClick", "being called!");
		
		int id = v.getId();
		if(id == R.id.clickcamera){
			takePicture();
		} else {	// user clicked the visibleside button
			int currentPhase = getState();
			Log.i(TAG+".onClick", "currestPhase = " + currentPhase);

			int sSide = 0;
			for(int i=0;i<6;i++){
				if(v==getVisibleSide(i)){
					sSide=i;
					break;
				}
			}
			Log.i(TAG+".onClick", "sSide="+sSide);

			if(sSide<6){
				int next = nextPhase(currentPhase, sSide);
				mHintTV.setText(getHintString(next));
				String key = "" + currentPhase + next;
				Log.i(TAG+".onClick", "key="+key);
				ArrayList<RotateAction> r = routineMap.get(key);
				for(RotateAction action: r){
					//maybe bugs here, _renderer.rotate can only be called from GLThread
					_renderer.rotate(action);
				}
				setState(next);
			}			
		}
	}
	
	private final static int nextPhase(int currentPhase, int sSide){
		int next = PHASE0;
		
		if(currentPhase==PHASE0 && 
				(sSide==TOPSIDE||sSide==FRONTSIDE||sSide==RIGHTSIDE)){
			next = currentPhase;
		} else if(currentPhase==PHASE1 &&
					(sSide==BOTTOMSIDE||sSide==BACKSIDE||sSide==LEFTSIDE)){
			next = currentPhase;
		} else if(currentPhase==PHASE0 && 
				(sSide==BOTTOMSIDE||sSide==BACKSIDE||sSide==LEFTSIDE)) {
			next = PHASE1;
		} else if(currentPhase==PHASE1 && 
				(sSide==TOPSIDE||sSide==FRONTSIDE||sSide==RIGHTSIDE)){
			next = PHASE0;
		}
		
		return next;
	}
	
	private final static HashMap<String, ArrayList<RotateAction>> routineMap = new HashMap<String, ArrayList<RotateAction>>();
	private final static int[] sides1 = {
			Tube.top, Tube.equator, Tube.bottom,
	};
	private final static int[] sides2 = {
			Tube.front, Tube.standing, Tube.back,
	};
	private final static RotateAction r0 = new RotateAction(sides1, Tube.CW);
	private final static RotateAction r0R = new RotateAction(sides1, Tube.CCW);
	private final static RotateAction r1 = new RotateAction(sides2, Tube.CCW);
	private final static RotateAction r1R = new RotateAction(sides2, Tube.CW);
	
	private final static void init0(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		routineMap.put("00", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1R);
		list.add(r1R);
		list.add(r0);
		routineMap.put("01", list);
	}
	
	private final static void init1(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r1);
		list.add(r1);
		routineMap.put("10", list);
		
		list = new ArrayList<RotateAction>();
		routineMap.put("11", list);
	}
	
	static {
		init0();
		init1();
	}

	@Override
	public int getWayType() {
		return ICameraPicture.TYPE1;
	}
}
