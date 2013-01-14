package android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

public class TableLayoutWithBorder extends TableLayout {

	private final static String TAG = "TableLayoutWithBorder";
	
	public TableLayoutWithBorder(Context context) {
		super(context);
		setWillNotDraw(false);
	}
	
	public TableLayoutWithBorder(Context context, AttributeSet attrs){
		super(context,attrs);
		setWillNotDraw(false);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int pl = this.getPaddingLeft()-1;
		int pr = this.getPaddingRight()-1;
		int pt = this.getPaddingTop()-1;
		int pb = this.getPaddingBottom()-1;
		Log.i(TAG+".onDraw", "pl = " + pl);
		Log.i(TAG+".onDraw", "pr = " + pr);
		Log.i(TAG+".onDraw", "pt = " + pt);
		Log.i(TAG+".onDraw", "pb = " + pb);
		
		int left,top,right,bottom;

		left=0;
		top=0;
		right=this.getWidth();
		bottom=this.getHeight();
		Paint paint=new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(new Rect(left+pl,top+pt,right-1-pr,bottom-1-pb), paint);
	}
}
