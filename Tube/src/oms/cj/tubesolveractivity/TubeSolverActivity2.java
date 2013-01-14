package oms.cj.tubesolveractivity;

import com.wooboo.adlib_android.ImpressionAdView;
import oms.cj.ads.AdGlobals;
import oms.cj.tube.R;
import oms.cj.tube.solver.TubeSolver;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class TubeSolverActivity2 extends Activity {
	private final static String TAG = "TubeSolverActivity2";
	private final static int DefaultDelay = 100;
	TubeSolver mTubeSolver;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
        setContentView(R.layout.tubesolveractivity2);
        
        mTubeSolver = (TubeSolver) findViewById(R.id.tubesolver);
        if(!TubeSolver.bTest) {
        	Intent intent = getIntent();
        	Bundle bundle = intent.getExtras();
        	int[] colors = bundle.getIntArray("tubecolors");
        	for(int i=0;i<colors.length;i++){
        		Log.i(TAG+".onCreate", "i color = " + colors[i]);
        	}
        	mTubeSolver.setColor(colors);
        }
       	mTubeSolver.postDelayed(new Runnable(){
       		public void run(){
       			mTubeSolver.solveIt();
       		}
       	}, DefaultDelay);
       	
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        AdGlobals.getInstance().adDynamicFlow(mTubeSolver, this, Gravity.BOTTOM|Gravity.LEFT);
    }
    
	@Override
	public void onPause(){
		super.onPause();
		
		if(mTubeSolver!=null)
			mTubeSolver.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		if(mTubeSolver!=null)
			mTubeSolver.onResume();
	}
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭 渐入式 广告
		ImpressionAdView.close();
	}
}
