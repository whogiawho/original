package oms.cj.WuZiBoard;

import oms.cj.WuZiLogic.ScreenInfo;
import oms.cj.utils.Globals;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;


public abstract class PuzzleBoard{
	private final static String TAG = "PuzzleBoard";
	
	protected static int WC=TableLayout.LayoutParams.WRAP_CONTENT;
	protected final static int mExternalBorderSize = 1;
	protected final static int delta[][]={
		{0, 150},  		//HVGA
		{0, 260}		//WVGA
    };
	//mCellImageMap's evaluation is delayed to PuzzleBoard's sub classes
	protected final int[] mCellImageMap = new int[ScreenInfo.getSupportedScreens()];
	
	protected int mRow;
	protected int mCol;
	protected int mResolution;
	protected INewGameActivity mINewGameActivity;
	
	protected int mCellSize;
	protected View.OnClickListener mClickListener = new View.OnClickListener(){
		public void onClick(View v) {	
		}
	};
	
	//paddings[0] - left
	//paddings[1] - top
	//paddings[2] - right
	public abstract void getpaddings(int width, int height, int[] paddings);
	public abstract void realizeCellImageMap();
	public abstract void createTemplate(TableLayout layout);
	public abstract void show(TableLayout layout);
	public abstract View.OnClickListener realizeOnClickListener(TableLayout layout);
	public abstract boolean isCrossed();
    public abstract Bitmap calculateDrawable(int i, int j);
	
	protected PuzzleBoard(int row, int col, int resolution, INewGameActivity iNewGameActivity){
		mRow = row;
		mCol = col;
		mResolution = resolution;
		mINewGameActivity = iNewGameActivity;
		
		realizeCellImageMap();
		setupCellSize();
	} 
	 
	//Can be called only if mCellImageMap is set
	protected void setupCellSize(){ 
		int resId;
		//build the relationship "resolution-->Cell's Size" in mCellSize
		int[] cellSizeMap = new int[mCellImageMap.length];
		for(int i=0;i<cellSizeMap.length;i++){
			resId = mINewGameActivity.map2DrawableResId(getCellDrawable(i));
			Resources rs = mINewGameActivity.getContextResources();
			Bitmap b = Globals.loadRes2Bitmap(rs, resId, false);
			int width = b.getWidth();
			int height = b.getHeight();
			Log.i(TAG+".setupCellSize", "height= " + height);
			Log.i(TAG+".setupCellSize", "width = " + width);
			cellSizeMap[i] = Math.max(width, height);
		}
		mCellSize = cellSizeMap[mResolution];	
		Log.i(TAG, "setupCellSize(...): " + "mCellSize=" + mCellSize);
	}
	
	protected int getCellDrawable(int resolution){
		return mCellImageMap[resolution];
	}
	
	public int getRow(){
		return mRow;
	}
	
	public int getCol(){
		return mCol;
	}	
}