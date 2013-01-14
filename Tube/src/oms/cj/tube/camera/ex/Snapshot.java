package oms.cj.tube.camera.ex;

import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.camera.CamLayer;
import oms.cj.tube.camera.TubeCamRenderer;
import oms.cj.tube.camera.TubeCamView;
import oms.cj.tube.component.Face;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class Snapshot extends oms.cj.tube.camera.Snapshot implements OnClickListener {
	private final static String TAG = "ex.Snapshot";
    
	private CamLayer mPreview;	

	protected TubeCamView _tubeview;
    protected TubeCamRenderer _renderer;
	protected boolean bTakingPicture=false;
	
	protected abstract void initTube(String fileName, boolean randomized, Activity act, int requestCode);
	
	@Override
	protected void onResume(){
    	super.onResume();
    	Log.i(TAG+".onResume", "being called!");
		
		initTube(null, false, this, getState());
		
		FrameLayout frame = new FrameLayout(this);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.snapshot, frame);
		mPreview = (CamLayer) v.findViewById(R.id.camerapreview);
		mHintTV = (TextView) v.findViewById(R.id.texthint);
		mHintTV.setBackgroundColor(R.color.transparentDark);
		mHintTV.setText(getHintString(getState()));
		RelativeLayout foreLayout = (RelativeLayout) v.findViewById(R.id.forelayout);
		foreLayout.setBackgroundColor(R.color.transparentDark);
		ImageButton ib = (ImageButton)v.findViewById(R.id.clickcamera);
		ib.setBackgroundColor(R.color.transparentDark);
		ib.setOnClickListener(this);
		
		initVisibleSides(v, this);
    	
		addContentView(frame, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		AdGlobals.getInstance().displayWoobooDynamicAd(frame, this);
    }
	
	protected void restartPreview(){
		mPreview.restartPreview();
	}
	protected void takePicture(){
		mPreview.takePicture();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		Log.i(TAG+".onKeyDown", "keyCode" + keyCode);
		
		switch(keyCode){
		case KeyEvent.KEYCODE_CAMERA:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		Log.i(TAG+".onKeyUp", "keyCode" + keyCode);
		
		switch(keyCode){
		case KeyEvent.KEYCODE_CAMERA:
			if(!bTakingPicture){
				bTakingPicture = true;
				takePicture();
			}
			return true;		
		}
		return super.onKeyUp(keyCode, event);
	}
	
	protected float[] getTubeCenters(int side, int idx){
		float[][] cs = _renderer.getTubeCenters(side);
		
		return cs[idx];
	}

	private void modify(float[] projectCenter, int width, int height){
		int[] projectionView = _renderer.getProjectionView();
		Log.i(TAG+".getColor", "projectionWidth="+projectionView[2]);
		Log.i(TAG+".getColor", "projectionHeight="+projectionView[3]);
		
		// mProjectionView[2]/mProjectionView[3] is the project view's w/h
		// width/height is the camera preview's w/h
		// obj[] is the source/target point in project view
		Matrix m = new Matrix();
        final float sx = width  / (float)projectionView[2];
        final float sy = height / (float)projectionView[3];
        m.setScale(sx, sy);
		Log.i(TAG+".getColor", "before: x="+projectCenter[0]+";y="+projectCenter[1]);
        m.mapPoints(projectCenter);
		Log.i(TAG+".getColor", "after: x="+projectCenter[0]+";y="+projectCenter[1]);
		
	}
	
	protected int getRGBColor(byte[] b, float[] projectCenter, int width, int height){
		int color = Face.defaultcolor.toInt();

		modify(projectCenter, width, height);
		color = getRGBColor(b, projectCenter, width);
		
		return color;
	}
	
	protected int[] getYUVColor(byte[] b, float[] projectCenter, int width, int height){
		modify(projectCenter, width, height);
		
		int x = (int) projectCenter[0];
		int y = (int) projectCenter[1];
		
		int[] yuv = new int[3];
			
		int total = width * height;
		yuv[0] = b[y * width + x];
		yuv[1] = b[(y / 2) * (width / 2) + (x / 2) + total];
		yuv[2] = b[(y / 2) * (width / 2) + (x / 2) + total + (total / 4)];

		return yuv;
	}
}
