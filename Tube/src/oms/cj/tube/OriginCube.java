package oms.cj.tube;


import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class OriginCube extends TubeBaseActivity {	
	private final static String TAG="OriginCube";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		initTube(null, false, this);
        Log.i(TAG+"::onCreate", "being called!");
    }   
}