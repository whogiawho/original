package oms.cj.tubeview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class FlexibleImageView extends ImageView {
	private static final String TAG="FlexibleImageView";
	
	public FlexibleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
    @Override
    protected void onMeasure(int width, int height){
    	Log.i(TAG+".onMeasure", "width = " + View.MeasureSpec.toString(width));
    	Log.i(TAG+".onMeasure", "height = " + View.MeasureSpec.toString(height));
    	setMeasuredDimension(View.MeasureSpec.getSize(width), (int) (View.MeasureSpec.getSize(height)*0.80));
    }
}
