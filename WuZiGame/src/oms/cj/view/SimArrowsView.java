package oms.cj.view;

import oms.cj.WuZiGame.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class SimArrowsView extends View {	//R.drawable.key must be set before usage
	public final static int STILL = 0;
	public final static int UP = 1;
	public final static int UPRIGHT = 2;
	public final static int RIGHT = 3;
	public final static int DOWNRIGHT = 4;
	public final static int DOWN = 5;
	public final static int DOWNLEFT = 6;
	public final static int LEFT = 7;
	public final static int UPLEFT = 8;
	private final static String TAG = "SimArrowsView";
    private int[] map2DirIdx = {
    		UPLEFT,
    		UP,
    		UPRIGHT,
    		LEFT,
    		STILL,
    		RIGHT,
    		DOWNLEFT,
    		DOWN,
    		DOWNRIGHT,
    };
    
	private int mUnitHeight;
	private int mUnitWidth;
	private Way mWay;
	private Drawable[] mKeys = new Drawable[9];
	private int mDirIdx = 0;
	private IOnArrowDownListener mIOnArrowDownListener = null;
	
	public void setOnArrowDownListener(IOnArrowDownListener listener){
		mIOnArrowDownListener = listener;
	}
	
	public interface IOnArrowDownListener {
		public void onArrwoDown(int direction);
	}
	
	public int getUnitWidth(){
		return mUnitWidth;
	}
	
	private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);

        mWay = new Way2();
        mUnitHeight = mWay.getUnitHeight();
        mUnitWidth = mWay.getUnitWidth();
        Log.i(TAG+".init", "mUnitHeight= " + mUnitHeight);
        Log.i(TAG+".init", "mUnitWidth = " + mUnitWidth);
        
    	Bitmap bmKey = mWay.loadRes2Bitmap();
    	split2DrawableList(bmKey, mKeys);
	}
	
	public SimArrowsView(Context context) {
		super(context);
		init();
	}
	
    public SimArrowsView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

	private void split2DrawableList(Bitmap bmKey, Drawable[] keys){
		int cols = 4;
		
		for(int i=0;i<9;i++){
			int x = i%cols;
			int y = i/cols;
			Log.i(TAG+".split2DrawableList", "x = " + x);
			Log.i(TAG+".split2DrawableList", "y = " + y);
			Bitmap b = Bitmap.createBitmap(bmKey, x*mUnitWidth, y*mUnitHeight, mUnitWidth, mUnitHeight, null, false);
			keys[i] = new BitmapDrawable(b);
		}
	}
	
    @Override 
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	mKeys[mDirIdx].setBounds(0, 0, mUnitWidth, mUnitHeight);
    	mKeys[mDirIdx].draw(canvas);
    }

    @Override
    protected void onMeasure(int width, int height){
    	setMeasuredDimension(mUnitWidth, mUnitHeight);
    }
    
    public static Bitmap loadRes2Bitmap(Resources rs, int id, boolean scale) {
        Bitmap bitmap;
        if (scale) {
            bitmap = BitmapFactory.decodeResource(rs, id);
        } else {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            bitmap = BitmapFactory.decodeResource(rs, id, opts);
        }
        return bitmap;
    }

	@Override
	public boolean onKeyDown(int keycode, KeyEvent event){
		boolean bParent = super.onKeyDown(keycode, event);
		if(keycode == KeyEvent.KEYCODE_DPAD_CENTER)
			return false;
		return bParent;
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event){
/*    	
    	Log.i(TAG+".onTouchEvent", "before super.onTouchEvent(event), isPressed() = " + isPressed());
    	Log.i(TAG+".onTouchEvent", "before super.onTouchEvent(event), isClickable() = " + this.isClickable());
    	Log.i(TAG+".onTouchEvent", "before super.onTouchEvent(event), isLongClickable() = " + this.isLongClickable());
*/    	
    	boolean bRet = super.onTouchEvent(event);
    	
//    	Log.i(TAG+".onTouchEvent", "after super.onTouchEvent(event), isPressed() = " + isPressed());
    	
    	int action = event.getAction();
    	switch(action){
    	case MotionEvent.ACTION_DOWN:
    		//decide which arrow key is pressed
    		float x =  event.getX();
    		float y =  event.getY();
    		Log.i(TAG+".onTouchEvent", "ACTION_DOWN:" + "x = " + x);
    		Log.i(TAG+".onTouchEvent", "ACTION_DOWN:" + "y = " + y);
    		mDirIdx = mWay.getDirIdx(x, y);
    		Log.i(TAG+".onTouchEvent", "mDirIdx = " + mDirIdx);
    		invalidate();
    		
    		//call the IOnArrowDown callback
    		mIOnArrowDownListener.onArrwoDown(mDirIdx);
    		
    		//post the arrow down repeat message
    		NextRepeatClick arrowDown = new NextRepeatClick();
    		arrowDown.rememberWindowAttachCount();
    		postDelayed(arrowDown, ViewConfiguration.getLongPressTimeout());
    		
    		bRet = true;
    		break;
    	case MotionEvent.ACTION_UP:
    		mDirIdx = STILL;
    		Log.i(TAG+".onTouchEvent", "ACTION_UP:" + "mDirIdx = " + mDirIdx);
    		invalidate();
    		bRet = true;
    	default:
    		break;
    	}
    	
    	return bRet;
    }
    
    class NextRepeatClick implements Runnable {
        private int mOriginalWindowAttachCount;

        public void rememberWindowAttachCount() {	
            mOriginalWindowAttachCount = SimArrowsView.this.getWindowAttachCount();
        }

		@Override
		public void run() {
/*			
			Log.i("NextRepeatClick.run", "run starting ...");
			Log.i("NextRepeatClick.run", "isPressed() = " + isPressed());
			Log.i("NextRepeatClick.run", "MyView.this.getParent() = " + MyView.this.getParent());
			Log.i("NextRepeatClick.run", "mOriginalWindowAttachCount = " + mOriginalWindowAttachCount);
			Log.i("NextRepeatClick.run", "MyView.this.getWindowAttachCount() = " + MyView.this.getWindowAttachCount());
*/
			
            if (isPressed() && (SimArrowsView.this.getParent() != null)
                    && mOriginalWindowAttachCount == SimArrowsView.this.getWindowAttachCount()) {
            	//call the IOnArrowDown callback
            	mIOnArrowDownListener.onArrwoDown(mDirIdx);
            	
            	SimArrowsView.this.postDelayed(this, ViewConfiguration.getLongPressTimeout());
			}
		}    	
    }
    
    abstract class Way {
    	protected Bitmap mBitmap;
    	
    	Way(int drawableID){
    		mBitmap = SimArrowsView.loadRes2Bitmap(getResources(), drawableID, false);
    	}
    	
		public int getUnitHeight() {
			return mBitmap.getHeight()/3;
		}

		public int getUnitWidth() {
			return mBitmap.getWidth()/4;
		}

		public Bitmap loadRes2Bitmap() {
			return mBitmap;
		}
		
    	public abstract int getDirIdx(float x, float y);
    }
    
    //Both Way1 and Way2's idx is defined as below:
    //    0  1  2
    //    3  4  5
    //    6  7  8
    class Way2 extends Way {
    	private float[] mColBoundary = {
    			0, 54.75f, 101.4f, 159f
    	};
    	private float[] mRowBoundary = {
    			0, 54.75f, 102.75f, 159f
    	};
    	
    	Way2(){
			super(R.drawable.key2);
    	}
    	
		@Override
		public int getDirIdx(float x, float y) {
			int idx = 0;
			int i, j;
			
			for(i=0;i<mRowBoundary.length-1;i++){
				if(y>=mRowBoundary[i] && y<mRowBoundary[i+1])
					break;
			}
			for(j=0;j<mColBoundary.length-1;j++){
				if(x>=mColBoundary[j] && x<mColBoundary[j+1])
					break;
			}
			if(i==3||j==3)
				idx = 4;
			else
				idx = i*3+j;
			
			//skip UL, UR, DL, DR
			if(idx == 0 || idx == 2 || idx == 6 || idx == 8)
				idx = 4;
			
			return map2DirIdx[idx];
		}
    }
    
    class Way1 extends Way {	
        private float[][] mColZone = {
        		{0,	36.5f},
        		{36.5f, 67.6f},
        		{67.6f, 106f}
        };
        
        private float[][] mRowZone = {
        		{0, 36.5f},
        		{36.5f, 68.5f},
        		{68.5f, 106f}
        };
        
    	Way1(){
    		super(R.drawable.key1);
    	}
    	
        public int getDirIdx(float x, float y){
        	int idx = 0;
        	int i=0, j=0;
        	
    _L0:    	
        	for(j=0;j<3;j++) {	//col
        		Log.i(TAG+".getIdx", "j=" + j);
        		for(i=0;i<3;i++) {	//row
        			Log.i(TAG+".getIdx", "i=" + i);
        			if(x>=mColZone[j][0] && x<mColZone[j][1] && 
        				y>=mRowZone[i][0] && y<mRowZone[i][1])
        				break _L0;                              
        		}
        	}
        	
        	if(j==3)
        		idx = 4;
        	else 
        		idx = i*3 + j;

        	//skip UL, UR, DL, DR
			if(idx == 0 || idx == 2 || idx == 6 || idx == 8)
				idx = 4;
        	
        	return map2DirIdx[idx];
        }
    }
}
