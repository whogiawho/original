package oms.cj.WuZiBoard;

import oms.cj.WuZiLogic.LogicResource;
import oms.cj.WuZiLogic.ScreenInfo;
import oms.cj.utils.Globals;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class NormalPuzzleBoard extends PuzzleBoard{
	private final static String TAG = "NormalPuzzleBoard";
	
	NormalPuzzleBoard(int row, int col, int resolution, INewGameActivity iNewGameActivity){
		super(row, col, resolution, iNewGameActivity);
	}

	@Override
	public void getpaddings(int width, int height, int[] paddings){			
		paddings[0]=(width - mCellSize*mCol - mExternalBorderSize*2 - delta[mResolution][0])/2;
		paddings[1]=(height - mCellSize*mRow - mExternalBorderSize*2 - delta[mResolution][1])/2;
		paddings[2]=paddings[0];			
	}
	
	@Override
	public boolean isCrossed(){
		return false;
	}
	
	@Override
	public void createTemplate(TableLayout layout){
		mClickListener = realizeOnClickListener(layout);
		
        for(int i=0;i<mRow;i++){
        	TableRow row=new TableRow(mINewGameActivity.getContext());
        	row.setClipChildren(false);
	        	
        	layout.addView(row, new TableLayout.LayoutParams(WC,WC));

        	for(int j=0;j<mCol;j++){
	        	ImageView image=new ImageView(mINewGameActivity.getContext());

	        	image.setOnClickListener(mClickListener);
	        		
	        	row.addView(image);
	        }
	    }			
	}
	    
    @Override
    public void show(TableLayout layout){
    	for(int i=0;i<mRow;i++){
          	TableRow row=(TableRow)layout.getChildAt(i);
           	for(int j=0;j<mCol;j++){
           		ImageView image=(ImageView)row.getChildAt(j);
           		Bitmap d = calculateDrawable(i,j);
				TableRow.LayoutParams params = new TableRow.LayoutParams(d.getWidth(), d.getHeight());
				image.setLayoutParams(params);
				image.getParent().requestLayout();
           		image.setImageBitmap(d);
           	}
        }	
    }

	@Override
	public void realizeCellImageMap() {
		//build the relationship "resolution-->Cell's Drawable" in mCellImageMap
//		mCellImageMap[ScreenInfo.HVGA] = R.drawable.grid24x24;
		mCellImageMap[ScreenInfo.HVGA] = LogicResource.NPB_HVGA_DRAWABLE_BASE;
		mCellImageMap[ScreenInfo.WVGA] = LogicResource.NPB_WVGA_DRAWABLE_BASE;
	}

	private class NormalPuzzleBoardOnClickListener implements View.OnClickListener{
		private final static String TAG2 = ".NormalPuzzleBoardOnClickListener";
		TableLayout mLayout;
		
		NormalPuzzleBoardOnClickListener(TableLayout layout){
			mLayout = layout;
		}
		
		private int[] getViewLocation(View view){
			int[] coord = {-1, -1};
			int rows=mLayout.getChildCount();
			int i,j;
			TableRow row;
			
			for(i=0;i<rows;i++){
				row=(TableRow)mLayout.getChildAt(i);
				j=row.indexOfChild(view);
				if(j==-1)
					continue;
				else{
					coord[0] = i;
					coord[1] = j;
					break;
				}
			}
			
			return coord;
		}
		
		@Override
		public void onClick(View v) {
			int[] coord = getViewLocation(v);
			
			Log.i(TAG+TAG2, "onClick(...)" + "logical row=" + coord[0]);
			Log.i(TAG+TAG2, "onClick(...)" + "logical col=" + coord[1]);
		}		
	}
	
	@Override
	public View.OnClickListener realizeOnClickListener(TableLayout layout) {
		NormalPuzzleBoardOnClickListener listener = new NormalPuzzleBoardOnClickListener(layout);
		
		return listener;		
	}
	
	@Override
	public Bitmap calculateDrawable(int i, int j){
		Bitmap d;
		int resId;
		
		Resources rs = mINewGameActivity.getContextResources();	
		resId = mINewGameActivity.map2DrawableResId(getCellDrawable(mResolution));
		d = Globals.loadRes2Bitmap(rs, resId, false);
	
		return d;
	}
}