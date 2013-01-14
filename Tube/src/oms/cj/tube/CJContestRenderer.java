package oms.cj.tube;

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import oms.cj.tube.component.RotateAction;
import oms.cj.tube.component.Tube;

public class CJContestRenderer extends PlayRenderer implements	ITubeRenderCallbacks {
	private final static String TAG = "CJContestRenderer";
	public final static int iconWidth = 48;
	public final static int iconHeight = 48;
	
	private Activity mAct;
	private boolean bCaptured = true;
	private Bitmap mSnapshot;
	private String mIconFilename;
	
	public CJContestRenderer(String fileName, boolean randomized, int IDSwitch,
			Activity act) {
		super(fileName, randomized, IDSwitch, act);
		
		mAct = act;
		getTube().setCallbacks(this);
	}

	private Rect calculateSnapshotArea(int[] projects){
		Rect area = new Rect();
		int w = projects[2], h = projects[3];
		
		int min = w;
		if(w>h){
			min = h;
		} 
		
		if(min==w){	// w<h
			int delta = (h-w)/2;
			area.top = delta;
			area.left = 0;
			area.right = w;
			area.bottom = delta + w;
		} else {	// w>h
			int delta = (w-h)/2;
			area.top = 0;
			area.left = delta;
			area.right = delta + h;
			area.bottom = h;
		}
		
		return area;
	}
	
    @Override
    public void onDrawFrame(GL10 gl) {
    	super.onDrawFrame(gl);
    	
    	int[] projects = getProjectionView();

       	if(!bCaptured){
       		Rect area = calculateSnapshotArea(projects);
       		Bitmap b = Globals.SavePixels(area.left, area.top, area.width(), area.height(), gl);
       		String out = String.format("left=%d, top=%d, width=%d, height=%d", 
       				area.left, area.top, b.getWidth(), b.getHeight());
       		Log.i(TAG+".onDrawFrame", out);
       		mSnapshot = Bitmap.createScaledBitmap(b, iconWidth, iconHeight, false);
       		Globals.saveBitmap2File(mSnapshot, mIconFilename, mAct);
       		Log.i(TAG+".onDrawFrame", "saving snapshot to disk!");
       		bCaptured = true;
       	}
    }
    
    public synchronized void doSnapshot(String iconFilename){
    	bCaptured = false;
    	mIconFilename = iconFilename;
    }
    
    public synchronized Bitmap getSnapshot(){
    	while(!bCaptured);
    	
    	return mSnapshot;
    }
    
	@Override
	public void onRotateFinish(RotateAction r) {
		Log.i(TAG+".onRotateFinish", "being called!");
		
		//1. mAct instanceof RandomCube
		//2. initial mBackwardBound>0
		//3. in origin state
		if(RandomCube.class.isInstance(mAct)){
			Tube t = getTube();
			int bound = getBackwardBound();
			if(bound>0&&t.inOriginState()){
				Log.i(TAG+".onRotateFinish", mAct.getString(R.string.recovercubesuccess));
				Handler h = getHandler();
				if(h!=null){
					enableFeature(FEATURE_ROTATEFACE, false);
					h.sendEmptyMessage(TubeBaseActivity.ORIGINTUBEREACHED);
				}
			}
		}
	}

	@Override
	public void onRotateStart(RotateAction r) {
		//nothing will be done
	}
}
