package oms.cj.tube.camera;

import oms.cj.tube.R;
import oms.cj.tube.component.Face;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;


public class DefineColor extends Activity implements ICameraPicture, View.OnClickListener{
	private final static String TAG = "DefineColor";
	public final static String colorStudy = "colorStudy";
	
	private CamLayer mPreview = null;
	private ColorView mColorView = null;
	private boolean bTakingPicture=false;
	private RelativeLayout mRelativeLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		Log.i(TAG+".onCreate", "before calling setContentView");
		setContentView(R.layout.definecolor);
		Log.i(TAG+".onCreate", "setContentView completed!");
		
		mPreview = (CamLayer)findViewById(R.id.camerapreview);
		mColorView = (ColorView)findViewById(R.id.colorview);
		mRelativeLayout = (RelativeLayout)findViewById(R.id.definecolor);
		
		ImageButton ib = (ImageButton)findViewById(R.id.clickcamera);
		ib.setOnClickListener(this);
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
	
	private void takePicture(){
		if(!bTakingPicture){
			bTakingPicture = true;
			mPreview.takePicture();
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event){
		Log.i(TAG+".onKeyUp", "keyCode" + keyCode);
		
		switch(keyCode){
		case KeyEvent.KEYCODE_CAMERA:
			takePicture();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public void onPictureTaken(byte[] yuvData, byte[] rgbData, int previewWidth, int previewHeight) {
		int color = 0;
		
		// calculate color from rgbData, and mColorView
		int left = mColorView.getLeft();
		int top = mColorView.getTop();
		int w = mColorView.getWidth();
		int h = mColorView.getHeight();
		float[] center = new float[2];
		center[0] = left + w/2;
		center[1] = top + h/2;
		color = getColor(rgbData, center, previewWidth, previewHeight);
		
		//return color to calling activity
    	Intent i = new Intent();
    	i.putExtra(colorStudy, color);
    	setResult(RESULT_OK, i);
    	finish();
    	
		bTakingPicture = false;
	}
	
	private int getColor(byte[] b, float[] coords, int previewWidth, int previewHeight){
		int color = Face.defaultcolor.toInt();
		
		Log.i(TAG+".getColor", "Camera previewWidth="+previewWidth);
		Log.i(TAG+".getColor", "Camera previewHeight="+previewHeight);
		Log.i(TAG+".getColor", "Screen width = "+ mRelativeLayout.getWidth());
		Log.i(TAG+".getColor", "Screen height = "+ mRelativeLayout.getHeight());
		
		Matrix m = new Matrix();
        final float sx = previewWidth  / (float)mRelativeLayout.getWidth();
        final float sy = previewHeight / (float)mRelativeLayout.getHeight();
        m.setScale(sx, sy);
		Log.i(TAG+".getColor", "before: x="+coords[0]+";y="+coords[1]);
        m.mapPoints(coords);
		Log.i(TAG+".getColor", "after: x="+coords[0]+";y="+coords[1]);
		
		color = Snapshot.getRGBColor(b, coords, previewWidth);
		
		return color;
	}

	@Override
	public void onClick(View v) {
		takePicture();
	}

	@Override
	public int getWayType() {
		return ICameraPicture.INVALID;
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onPause();
	}
}
