package oms.cj.tube.flysymbol;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class FlySymbolGallery extends Gallery {
	private final static String TAG = "FlySymbolGallery";
	private final static float MIN = 0.5f;
	
	private int mCenterX;
	
	public FlySymbolGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setStaticTransformationsEnabled(true);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
		Log.i(TAG+".onFling", "being called!");
		
		return super.onFling(e1, e2, velocityX, velocityY);
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
		Log.i(TAG+".onScroll", "being called!");
		
		return super.onScroll(e1, e2, distanceX, distanceY);
	}
	
	//screen all touch events from Gallery.onTouchEvent.
	@Override
	public boolean onTouchEvent(MotionEvent e){
		return true;
	}
	
	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {		
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		
	    final Matrix m = t.getMatrix();;
	    final int imageHeight = child.getLayoutParams().height;;
	    final int imageWidth = child.getLayoutParams().width;
	    
	    final int childCenter = getCenterXOfChild(child);
	    final int distance = childCenter - mCenterX;
	    
	    float scale=1;
	    if(distance>=0){
	    	int rightmost = getWidth() - getPaddingRight();
	    	scale += distance*(MIN-1)/(rightmost-mCenterX);
	    } else {
	    	int leftmost = getPaddingLeft();
	    	scale += distance*(MIN-1)/(leftmost-mCenterX);
	    }
	    
	    m.setScale(scale, scale);
	    
	    m.preTranslate(-(imageWidth/2), -(imageHeight/2)); 
	    m.postTranslate((imageWidth/2), (imageHeight/2));
	      
		return true;
	}
	
	
    private static int getCenterXOfChild(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }  
    
    private int getCenterX() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }
    
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenterX = getCenterX();
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
