package oms.cj.tube.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {
	private final static int mWidth = 50;
	private final static int mHeight = 50;
	private final static int mStrokeWidth = 5;
	
	public ColorView(Context context){
		super(context);
	}
	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    @Override 
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
		Paint paint=new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		paint.setStrokeWidth(mStrokeWidth);
		
		canvas.drawLine(0, 0, mWidth, 0, paint);
		canvas.drawLine(0, 0, 0, mHeight, paint);
		canvas.drawLine(0, mHeight, mWidth, mHeight, paint);
		canvas.drawLine(mWidth, 0, mWidth, mHeight, paint);
    }

    @Override
    protected void onMeasure(int width, int height){
    	setMeasuredDimension(mWidth, mHeight);
    }
}
