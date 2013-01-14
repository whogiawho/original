package oms.cj.tube;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class RandomCube extends TubeBaseActivity{
	private final static String TAG="RandomCube";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		initTube(null, true, this);
        Log.i(TAG+"::onCreate", "being called!");
    }

}