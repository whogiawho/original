package oms.cj.tube.tutor;

import oms.cj.tube.Globals;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class FlexibleImageView extends ImageView {
	private static final String TAG="FlexibleImageView";
	public final static float DefaultHeightPercent = 0.80f;
	
	public FlexibleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
    @Override
    protected void onMeasure(int width, int height){
    	int modWidth=0, modHeight=0;
    	Log.i(TAG+".onMeasure", "width = " + View.MeasureSpec.toString(width));
    	Log.i(TAG+".onMeasure", "height = " + View.MeasureSpec.toString(height));
    	
    	if(View.MeasureSpec.getMode(width)!=View.MeasureSpec.UNSPECIFIED){
    		modWidth = View.MeasureSpec.getSize(width);
    	} else {
    		int sWidth = Globals.getWidth(this.getContext());
    		modWidth = sWidth;
    	}
    	
    	if(View.MeasureSpec.getMode(height)!=View.MeasureSpec.UNSPECIFIED){
    		modHeight = (int) (View.MeasureSpec.getSize(height)*DefaultHeightPercent);
    	} else {
    		int sHeight = Globals.getHeight(this.getContext());
    		modHeight = (int) (sHeight * DefaultHeightPercent);    		
    	}   
    	
    	Log.i(TAG+".onMeasure", "modWidth = " + modWidth);
    	Log.i(TAG+".onMeasure", "modHeight = " + modHeight);	
    	setMeasuredDimension(modWidth, modHeight);
    }
}
