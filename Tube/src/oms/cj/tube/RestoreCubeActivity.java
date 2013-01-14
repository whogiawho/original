package oms.cj.tube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class RestoreCubeActivity extends TubeBaseActivity {
	private final static String TAG = "RestoreCubeActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
    	Intent intent=new Intent();
    	intent.setClass(RestoreCubeActivity.this, ChooseFileActivity.class);
    	intent.putExtra(ChooseFileActivity.REQUESTCODE, ChooseFileActivity.CHOOSE_FILE_TO_OPEN);
    	startActivityForResult(intent, ChooseFileActivity.CHOOSE_FILE_TO_OPEN);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(resultCode == RESULT_OK){
    		final String fileName = data.getStringExtra(ChooseFileActivity.FILE2SERIALIZE);
    		
    		switch (requestCode) {
    		case ChooseFileActivity.CHOOSE_FILE_TO_OPEN:
    			// create TubeRenderer£¬TubeView
    			initTube(fileName, false, this);
    			break;
    		default:
    			super.onActivityResult(requestCode, resultCode, data);
    			break;
    		}
    	} else {
    		Log.i(TAG, "onActivityResult(...): " + "resultCode=" + resultCode);
    		finish();
    	}
    }
}
