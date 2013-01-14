package oms.cj.tube.camera.self;

import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.camera.CamLayer;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public abstract class Snapshot extends oms.cj.tube.camera.Snapshot implements View.OnClickListener {
	private final static String TAG = "self.Snapshot";
	public final static int SCANINPROGRESS = 0;
	public final static int SCANCOMPLETE = 1;
	public final static int SCANMATCHOK = 0;
	public final static int SCANMATCHFAIL = 1;
	
	protected CamLayer mPreview;
	private int mScanState;
	protected TextView mScanTV;
	private String[] mScanStringArray;
	protected ImageButton mPreviewSwitch;
	
	public int getScanState(){
		return mScanState;
	}
	public void setScanState(int state){
		mScanState = state;
	}
	
	@Override 
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	Log.i(TAG+".onCreate", "being called!");
		
		FrameLayout frame = new FrameLayout(this);
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.snapshotself, frame);
		mPreview = (CamLayer) v.findViewById(R.id.camerapreview);
		mHintTV = (TextView) v.findViewById(R.id.texthint);
		mHintTV.setBackgroundColor(R.color.transparentDark);
		mHintTV.setText(getHintString(getState()));
		RelativeLayout foreLayout = (RelativeLayout) v.findViewById(R.id.forelayout);
		foreLayout.setBackgroundColor(R.color.transparentDark);
		ImageButton ib = (ImageButton)v.findViewById(R.id.clickcamera);
		ib.setBackgroundColor(R.color.transparentDark);
		ib.setVisibility(View.INVISIBLE);
		ib.setOnClickListener(this);
		mPreviewSwitch = ib;
		
		mScanStringArray = getResources().getStringArray(R.array.scan);
		setScanState(SCANINPROGRESS);
		mScanTV = (TextView)v.findViewById(R.id.scanhint);
		mScanTV.setBackgroundColor(R.color.transparentDark);
		mScanTV.setText(getScanHintString(getScanState()));
		
		initVisibleSides(v, null);
    	
		setContentView(frame);
		
		AdGlobals.getInstance().displayWoobooDynamicAd(frame, this);
    }
	
	protected String getScanHintString(int state){
		String hint2 = "";
		
		if(state>=0&&state<mScanStringArray.length)
			hint2 = mScanStringArray[state];
		
		return hint2;
	}
}
