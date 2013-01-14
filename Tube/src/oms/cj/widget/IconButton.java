package oms.cj.widget;

import oms.cj.tube.Globals;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class IconButton extends Button {
	private final static String TAG = "IconButton";
	private final static double ratio = (double)1/2;

	private int mIconID;
	
	public IconButton(Context context, int iconID) {
		super(context);
		
		mIconID = iconID;
	}

	public IconButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				
		int w = this.getMeasuredWidth();
		int h = this.getMeasuredHeight();
		int scale = w;
		if(w>h)
			scale = h;
		String out = String.format("w=%d, h=%d, scale=%d", w, h, scale);
		Log.i(TAG+".onMeasure", out);
			
		Resources rs = this.getContext().getResources();  
		Bitmap b = Globals.loadRes2Bitmap(rs, mIconID, false); 
		if(b==null)
			Log.i(TAG+".onDraw", "b==null");
		
		Bitmap scaleB = Bitmap.createScaledBitmap(b, (int)(scale*ratio), (int)(scale*ratio), true);
		Drawable d = new BitmapDrawable(scaleB); 
		d.setBounds(0, 0, (int)(scale*ratio), (int)(scale*ratio));
		
		if(d==null)
			Log.i(TAG+".onDraw", "d==null");
		this.setCompoundDrawables(d, null, null, null);
		
		w += scale*ratio;
		
		this.setMeasuredDimension(w, h);	
	}
	
}
