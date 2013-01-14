package oms.cj.WuZiLogic;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class ScreenInfo {
	//------------------- Attention!!!! -------------------
	//each time adding a new screen, make sure to increment the returned value of getSupportedScreen()
	private static final String TAG = "ScreenInfo";
	
	public final static int HVGA = 0;
	public final static int WVGA = 1;
	//define more screens in following ...
    
	private Display mdisplay;
    
	public ScreenInfo(Activity activity){
		mdisplay = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}
	
	public int getResolution(){
		int resolution = HVGA;
		int height = mdisplay.getHeight();
		int width = mdisplay.getWidth();
		
		Log.i(TAG, "getResolution(...): " + "height=" + height +";width=" + width);
		if(height == 320 && width == 480 || height ==480 && width == 320)
			resolution = HVGA;
		else if(((height<900||height >= 800) && width ==480 || height == 480 && (width<900||width >= 800)))
			resolution = WVGA;
		else 
			resolution = HVGA;
		
		return resolution;
	}
	
	public static int getSupportedScreens(){
		return 2;
	}
	
	public int getHeight(){
		return mdisplay.getHeight();
	}
	
	public int getWidth(){
		return mdisplay.getWidth();
	}
}