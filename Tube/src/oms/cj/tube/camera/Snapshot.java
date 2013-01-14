package oms.cj.tube.camera;

import java.util.ArrayList;
import com.wooboo.adlib_android.ImpressionAdView;
import oms.cj.tube.R;
import oms.cj.tube.component.Color;
import oms.cj.tube.component.Cube;
import oms.cj.tube.component.Tube;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public abstract class Snapshot extends Activity {
	private final static String TAG = "Snapshot";
	public final static String TUBECOLORS = "tubecolors";
	public final static String REQUESTCODE = "requestCode";
	public final static int FRONTSIDE = 0;
	public final static int RIGHTSIDE = 1;
	public final static int BACKSIDE = 2;
	public final static int LEFTSIDE = 3;
	public final static int TOPSIDE = 4;
	public final static int BOTTOMSIDE = 5;
	public final static int DefaultCellSize = 13;
	protected final static int[] conv2TubeSide = {
		Tube.front, Tube.right,
		Tube.back,  Tube.left, 
		Tube.top,   Tube.bottom, 
	};
	
	private int mState;
	protected int getState(){
		return mState;
	}
	protected void setState(int state){
		mState = state;
	}

	private int[] mColors = new int[Tube.CubesEachSide*Cube.FacesEachCube];
	protected int[] getTubeColors(){
		return mColors;
	}
	protected void setTubeColor(int idx, Color c){
		mColors[idx] = c.toInt();
	}
	protected void setTubeColor(int idx, int c){
		mColors[idx] = c;
	}
	private VisibleSide[] visibleSides = new VisibleSide[6];
	protected VisibleSide getVisibleSide(int sSide){
		return visibleSides[sSide];
	}
	
	protected TextView mHintTV;
	protected String[] mHintString;
	
	protected abstract String getHintString(int requestCode);
	
	protected void initVisibleSides(View v, View.OnClickListener listener){
    	visibleSides[FRONTSIDE] = (VisibleSide) v.findViewById(R.id.frontside);
    	visibleSides[RIGHTSIDE] = (VisibleSide) v.findViewById(R.id.rightside);
    	visibleSides[BACKSIDE] = (VisibleSide) v.findViewById(R.id.backside);
    	visibleSides[LEFTSIDE] = (VisibleSide) v.findViewById(R.id.leftside);
    	visibleSides[TOPSIDE] = (VisibleSide) v.findViewById(R.id.topside);
    	visibleSides[BOTTOMSIDE] = (VisibleSide) v.findViewById(R.id.bottomside);
    	for(int i=0;i<6;i++){
    		if(listener!=null)
    			visibleSides[i].setOnClickListener(listener);
    		visibleSides[i].setDefaultImage(R.drawable.transparenttotal);
    		visibleSides[i].setCellSize(DefaultCellSize);
    	}
    	for(int i=0;i<6;i++){
    		for(int j=0;j<3;j++){
    			for(int k=0;k<3;k++){
    				visibleSides[i].setCellColor(j, k, Color.gray);
    			}
    		}
    	}
    	
    	visibleSides[FRONTSIDE].setImage(1, 1, R.drawable.transparentfront);
    	visibleSides[BACKSIDE].setImage(1, 1, R.drawable.transparentback);
    	visibleSides[RIGHTSIDE].setImage(1, 1, R.drawable.transparentright);
    	visibleSides[LEFTSIDE].setImage(1, 1, R.drawable.transparentleft);
    	visibleSides[TOPSIDE].setImage(1, 1, R.drawable.transparenttop);
    	visibleSides[BOTTOMSIDE].setImage(1, 1, R.drawable.transparentbottom);
	}
	
	private final static int[][] faceMappings ={
		{6, 7, 8, 3, 4, 5, 0, 1, 2},		//front
		{8, 5, 2, 7, 4, 1, 6, 3, 0}, 		//right
		{8, 7, 6, 5, 4, 3, 2, 1, 0},		//back
		{6, 3, 0, 7, 4, 1, 8, 5, 2},		//left
		{0, 1, 2, 3, 4, 5, 6, 7, 8},		//top
		{6, 7, 8, 3, 4, 5, 0, 1, 2},		//bottom
	};
	protected void setColorsVisibleSide(int sSide){
		int[] sideMapping = null;
		
		if(sSide>=0&&sSide<6){
			sideMapping = faceMappings[sSide];
		} else {
			Log.e(TAG+".setColor", "invalid sSide = "+sSide);
			return;
		}
		
		int tSide = conv2TubeSide[sSide];
		if(sideMapping!=null){
			VisibleSide vSide = visibleSides[sSide];
			for(int i=0;i<Tube.CubesEachSide;i++){
				int dstCube = sideMapping[i];
				int c = mColors[tSide*Tube.CubesEachSide+i];
				vSide.setCellColor(dstCube, c);
			}
		}
	}
	protected void setColor2VisibleSide(int[] colors, int sSide){
		int[] faceMapping = null;
		
		if(sSide>=0&&sSide<6){
			faceMapping = faceMappings[sSide];
		} else {
			Log.e(TAG+".setColor", "invalid sSide = "+sSide);
			return;
		}
		
		if(faceMapping!=null){
			VisibleSide vSide = visibleSides[sSide];
			for(int i=0;i<Tube.CubesEachSide;i++){
				int dstCube = faceMapping[i];
				vSide.setCellColor(dstCube, colors[i]);
			}
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        mState = i.getIntExtra(REQUESTCODE, 0);
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        mHintString = getResources().getStringArray(R.array.sides);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	Log.i(TAG+".onConfigurationChanged", "being called!");
    	super.onConfigurationChanged(newConfig);
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();

		ImpressionAdView.close();
	}
	
    //given 2 parmas:
    //1. sequences,           (an Integer ArrayList)
    //2. s,                   (an index of element at sequences)
    //nextSequence return next postion to s
	protected static int nextSequence(ArrayList<Integer>sequences, int s){
		int next=0;
		
		if(s>=0&&s<sequences.size()){
			int idx = sequences.indexOf(s);
			idx = (idx+1)%sequences.size();
			next = sequences.get(idx);
		}
			
		return next;
	};

	//given 3 input params:
	//1. coords[]   (x,y) 
	//2. w          (width)
	//3. bs         (a byte array which contains RGB info for w*h bitmap)
	//getColor return the RGB info at position (x,y)
	public static int getRGBColor(byte[] bs, float[] coords, int w){
		int color=0;
		float[][] deltas = {
				{0,  -1},		//top
				{-1,  0},		//left
				{0,   0},		//middle
				{1,   0},		//right
				{0,   1},		//bottom
//				{-1, -1},		//topleft
//				{1,  -1},		//topright
//				{-1,  1},		//bottomleft
//				{1,   1},		//bottomright
		};
		int sumR=0, sumG=0, sumB=0;
		
		for(int i=0;i<deltas.length;i++){
			int x = (int) (deltas[i][0] + coords[0]);
			int y = (int) (deltas[i][1] + coords[1]);
			int offset = (int) ((y*w+x)*3);  
			//Log.i(TAG+".getColor", "offset="+offset);
			int r = bs[offset]&0xff;
			int g = bs[offset+1]&0xff;
			int b = bs[offset+2]&0xff;
			sumR+=r; sumG+=g; sumB+=b;
		}

		sumR=sumR/deltas.length; sumG=sumG/deltas.length; sumB=sumB/deltas.length; 
		int a = 0xff;
		Log.i(TAG+".getColor", "r="+sumR+";g="+sumG+";b="+sumB);
		
		color = sumB|(sumG<<8)|(sumR<<16)|(a<<24);
		
		return color;
	}
	
	protected static void setFlag(boolean[] bFlags, int side, boolean value){
		bFlags[side] = value;
	}
	protected static boolean getFlag(boolean[] bFlags, int side){
		return bFlags[side];
	}
	protected static boolean allFlagsSet(boolean[] bFlags){
		boolean bAllSidesTaken = true;
		
		for(int i=0;i<bFlags.length;i++){
			if(!bFlags[i]){
				bAllSidesTaken = false;
				break;
			}
		}
		return bAllSidesTaken;
	}
	protected static void resetFlags(boolean[] bFlags){
		for(int i=0;i<bFlags.length;i++){
			bFlags[i] = false;
		}
	}
	
	protected static boolean typeIsEX(int type){
		return type==ICameraPicture.TYPE0||type==ICameraPicture.TYPE1;
	}
	
	protected static boolean typeIsSelf(int type){
		return type==ICameraPicture.TYPE2||type==ICameraPicture.TYPE3;
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		super.onPause();
	}
}
