package oms.cj.tube.camera.ex.way1;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Matrix4f;

import oms.cj.tube.R;
import oms.cj.tube.TubeBasicRenderer;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Quaternion;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;
import oms.cj.tube.camera.ICameraPicture;
import oms.cj.tube.camera.TubeCamRenderer;
import oms.cj.tube.camera.TubeCamView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Snapshot extends oms.cj.tube.camera.ex.Snapshot implements ICameraPicture {
	private final static String TAG = "way1.Snapshot";
	private final static Quaternion[] qList = {
		Quaternion.c0,
		Quaternion.c6,
		Quaternion.c7,
		Quaternion.c8,
		Quaternion.c9,
		Quaternion.c10,
	};
	
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
    public String getHintString(int side){
    	String hintString="";
    	
    	if(side>=0&&side<mHintString.length){
    		hintString = mHintString[side];
    	}
    	
    	return hintString;
    }
	
    @Override
    public void initTube(String fileName, boolean randomized, Activity act, int sSide){
        int idSwitch = TubeCamRenderer.TEXTURETRANSPARENT;

        if(sSide<0||sSide>=6){
        	Log.e(TAG+".initTube", "exception! invalid sSide ="+sSide);
        	Log.i(TAG+".initTube", "using default side 0");
        	sSide = 0;
        }
        
        // do something per requestCode
    	Quaternion q1 = qList[FRONTSIDE].clone();
    	_renderer = new TubeCamRenderer(fileName, randomized, idSwitch, act, q1);
    	_renderer.enableFeature(TubeBasicRenderer.FEATURE_ROTATEFACE, false);
    	_renderer.enableFeature(TubeBasicRenderer.FEATURE_ROTATECUBE, false);
    	
		String key = ""+FRONTSIDE+sSide;
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
    
    private Matrix4f getRotateMatrix(int side){
    	Matrix4f m = new Matrix4f();
    	m.setIdentity();
    	
    	switch(side){
    	case Tube.left:
    		m.rotY((float) (Math.PI/2*1));
    		break;
    	case Tube.right:
    		m.rotY((float) (-Math.PI/2*1));
    		break;
    	case Tube.top:
    		m.rotX((float) (Math.PI/2*1));
    		break;
    	case Tube.bottom:
    		m.rotX((float) (-Math.PI/2*1));
    		break;
    	case Tube.back:
    		m.rotY((float) (Math.PI*1));
    		break;
    	case Tube.front:	//nothing is done
    	default:
    		break;
    	}
    	
    	return m;
    }
    
	@Override
	public void onPictureTaken(byte[] yuvData, byte[] rgbData, int width, int height){
		int currentSide = getState();
		int tSide = conv2TubeSide[currentSide];
		int[] colors = new int[Tube.CubesEachSide];
		for(int j=0;j<Tube.CubesEachSide;j++){
			float[] center = getTubeCenters(tSide, j);
			//map center of 3D to a point in projected view
			float[] projectCenter = _renderer.getProjectPoint(center, getRotateMatrix(tSide));
			
			int iColor1 = getRGBColor(rgbData, projectCenter.clone(), width, height);
			int colorIdx = j;
			Color c1 = new Color(iColor1);
			//translate color to its closet color
			Color c2 = Color.closestColor(c1); 
			
			String out = String.format("%s: c2=%s", Tube.LAYERSTR_F[tSide], c2.toString());
			Log.i(TAG+".onPictureTaken", out);
			
			//put color to mColors
			setTubeColor(tSide*Tube.CubesEachSide+colorIdx, c2);
			colors[colorIdx] = c2.toInt();
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
			String key = ""+currentSide+next;
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
		if(id==R.id.clickcamera){
			takePicture();
		} else {	// user clicked the visibleside button
			int sSide = 0;
			for(int i=0;i<6;i++){
				if(v==getVisibleSide(i)){
					sSide=i;
					break;
				}
			}
			Log.i(TAG+".onClick", "sSide="+sSide);
			
			if(sSide<6){
				int currentSide = getState();
				mHintTV.setText(getHintString(sSide));
				String key = ""+currentSide+sSide;
				Log.i(TAG+".onClick", "key="+key);
				ArrayList<RotateAction> r = routineMap.get(key);
				for(RotateAction action: r){
					//maybe bugs here, _renderer.rotate can only be called from GLThread
					_renderer.rotate(action);
				}
				setState(sSide);
			}			
		}
	}
	
	private final static HashMap<String, ArrayList<RotateAction>> routineMap = new HashMap<String, ArrayList<RotateAction>>();
	private final static int[] sides1 = {
			Tube.top, Tube.equator, Tube.bottom,
	};
	private final static int[] sides2 = {
			Tube.left, Tube.middle, Tube.right,
	};
	private final static RotateAction r0 = new RotateAction(sides1, Tube.CW);
	private final static RotateAction r0R = new RotateAction(sides1, Tube.CCW);
	private final static RotateAction r1 = new RotateAction(sides2, Tube.CCW);
	private final static RotateAction r1R = new RotateAction(sides2, Tube.CW);
	
	private final static void init0(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		routineMap.put("00", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0);
		routineMap.put("01", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0);
		list.add(r0);
		routineMap.put("02", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		routineMap.put("03", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1);
		routineMap.put("04", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1R);
		routineMap.put("05", list);
	}
	private final static void init1(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		list.add(r0R);
		routineMap.put("10", list);
		
		list = new ArrayList<RotateAction>();
		routineMap.put("11", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0);
		routineMap.put("12", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0);
		list.add(r0);
		routineMap.put("13", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r1);
		routineMap.put("14", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r1R);
		routineMap.put("15", list);
	}
	private final static void init2(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r0R);
		routineMap.put("20", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		routineMap.put("21", list);

		list = new ArrayList<RotateAction>();
		routineMap.put("22", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0);
		routineMap.put("23", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r0R);
		list.add(r1);
		routineMap.put("24", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r0R);
		list.add(r1R);
		routineMap.put("25", list);
	}
	private final static void init3(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r0R);
		list.add(r0R);
		routineMap.put("30", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r0R);
		routineMap.put("31", list);

		list = new ArrayList<RotateAction>();
		list.add(r0R);
		routineMap.put("32", list);
		
		list = new ArrayList<RotateAction>();
		routineMap.put("33", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r0R);
		list.add(r0R);
		list.add(r1);
		routineMap.put("34", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r0R);
		list.add(r0R);
		list.add(r0R);
		list.add(r1R);
		routineMap.put("35", list);
	}
	private final static void init4(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		list.add(r1R);
		routineMap.put("40", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1R);
		list.add(r0);
		routineMap.put("41", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1R);
		list.add(r0);
		list.add(r0);
		routineMap.put("42", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1R);
		list.add(r0R);
		routineMap.put("43", list);
		
		list = new ArrayList<RotateAction>();
		routineMap.put("44", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1R);
		list.add(r1R);
		routineMap.put("45", list);
	}
	private final static void init5(){
		ArrayList<RotateAction> list = new ArrayList<RotateAction>();
		list.add(r1);
		routineMap.put("50", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1);
		list.add(r0);
		routineMap.put("51", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1);
		list.add(r0);
		list.add(r0);
		routineMap.put("52", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1);
		list.add(r0R);
		routineMap.put("53", list);
		
		list = new ArrayList<RotateAction>();
		list.add(r1);
		list.add(r1);
		routineMap.put("54", list);
		
		list = new ArrayList<RotateAction>();
		routineMap.put("55", list);
	}
	static {
		init0();
		init1();
		init2();
		init3();
		init4();
		init5();
	}
	@Override
	public int getWayType() {
		return ICameraPicture.TYPE0;
	}
}
