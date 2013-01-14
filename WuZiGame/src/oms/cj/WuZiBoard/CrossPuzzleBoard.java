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

public class CrossPuzzleBoard extends PuzzleBoard {	
	private final static String TAG = "NormalPuzzleBoard";
	private final int[][] mBorderCellImageMap = new int[ScreenInfo.getSupportedScreens()][8];
	
	private void setupBorderCellMap(){
/*		
		mBorderCellImageMap[ScreenInfo.HVGA][0] = R.drawable.hvga_cross_0_grid12x12;
		mBorderCellImageMap[ScreenInfo.HVGA][1] = R.drawable.hvga_cross_1_grid24x12;
		mBorderCellImageMap[ScreenInfo.HVGA][2] = R.drawable.hvga_cross_2_grid12x12;
		mBorderCellImageMap[ScreenInfo.HVGA][3] = R.drawable.hvga_cross_3_grid12x24;
		mBorderCellImageMap[ScreenInfo.HVGA][4] = R.drawable.hvga_cross_4_grid12x12;
		mBorderCellImageMap[ScreenInfo.HVGA][5] = R.drawable.hvga_cross_5_grid24x12;
		mBorderCellImageMap[ScreenInfo.HVGA][6] = R.drawable.hvga_cross_6_grid12x12;
		mBorderCellImageMap[ScreenInfo.HVGA][7] = R.drawable.hvga_cross_7_grid12x24;
*/
		mBorderCellImageMap[ScreenInfo.HVGA][0] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_1;
		mBorderCellImageMap[ScreenInfo.HVGA][1] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_2;
		mBorderCellImageMap[ScreenInfo.HVGA][2] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_3;
		mBorderCellImageMap[ScreenInfo.HVGA][3] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_4;
		mBorderCellImageMap[ScreenInfo.HVGA][4] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_5;
		mBorderCellImageMap[ScreenInfo.HVGA][5] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_6;
		mBorderCellImageMap[ScreenInfo.HVGA][6] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_7;
		mBorderCellImageMap[ScreenInfo.HVGA][7] = LogicResource.CPB_HVGA_DRAWABLE_BORDER_8;

		mBorderCellImageMap[ScreenInfo.WVGA][0] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_1;
		mBorderCellImageMap[ScreenInfo.WVGA][1] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_2;
		mBorderCellImageMap[ScreenInfo.WVGA][2] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_3;
		mBorderCellImageMap[ScreenInfo.WVGA][3] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_4;
		mBorderCellImageMap[ScreenInfo.WVGA][4] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_5;
		mBorderCellImageMap[ScreenInfo.WVGA][5] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_6;
		mBorderCellImageMap[ScreenInfo.WVGA][6] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_7;
		mBorderCellImageMap[ScreenInfo.WVGA][7] = LogicResource.CPB_WVGA_DRAWABLE_BORDER_8;
	}

	CrossPuzzleBoard(int row, int col, int resolution, INewGameActivity iNewGameActivity){
		super(row, col, resolution, iNewGameActivity);
				
		setupBorderCellMap();
	}

	@Override
	public void getpaddings(int width, int height, int[] paddings){
		paddings[0]=(width - mCellSize*(mCol+1) - mExternalBorderSize*2 - delta[mResolution][0])/2;
		paddings[1]=(height - mCellSize*(mRow+1) - mExternalBorderSize*2 - delta[mResolution][1])/2;
		paddings[2]=paddings[0];						
	}
	
	@Override
	public boolean isCrossed(){
		return true;
	}
	
	@Override
	public void realizeCellImageMap() {
		//build the relationship "resolution-->Cell's Drawable" in mCellImageMap
//		mCellImageMap[ScreenInfo.HVGA] = R.drawable.cross_grid24x24;
		mCellImageMap[ScreenInfo.HVGA] = LogicResource.CPB_HVGA_DRAWABLE_BASE;
		mCellImageMap[ScreenInfo.WVGA] = LogicResource.CPB_WVGA_DRAWABLE_BASE;
	}
	
	protected int[] map(int row, int col){
		int[] newcoord = new int[2];
		
		newcoord[0]=row+1;
		newcoord[1]=col+1;
		
		return newcoord;		
	}
	
	protected int[] rmap(int row, int col){
		int[] newcoord = new int[2];
		
		newcoord[0]=row-1;
		newcoord[1]=col-1;
		
		return newcoord;
	}
	
	@Override
    public void show(TableLayout layout){
		int resId; 
		
		Resources rs = mINewGameActivity.getContextResources();		
		for(int i=0;i<mRow+2;i++){
			TableRow row=(TableRow)layout.getChildAt(i);
			for(int j=0;j<mCol+2;j++){
				ImageView image=(ImageView)row.getChildAt(j);

				Bitmap d;
				if(i==0&&j==0){  //border0
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][0]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==0&&j==mCol+1){ //border2
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][2]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==mRow+1&&j==mCol+1){ //border4
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][4]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==mRow+1&&j==0){ //border6
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][6]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==0){ //border1
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][1]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(j==mCol+1){ //border3
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][3]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==mRow+1){ //border5
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][5]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(j==0){ //border7
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][7]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else {
					int[] coord = rmap(i,j);
            		d = calculateDrawable(coord[0],coord[1]);
				}
				Log.i(TAG+".show", "width="+d.getWidth());
				Log.i(TAG+".show", "height="+d.getHeight());
				if(d!=null){
					TableRow.LayoutParams params = new TableRow.LayoutParams(d.getWidth(), d.getHeight());
					image.setLayoutParams(params);
					image.getParent().requestLayout();
					image.setImageBitmap(d);
				}
			}
		}
    }
	
	public void enableInput(TableLayout layout, boolean inputSwitch){
		Log.d(TAG, "enableInput(...): " + "NO. of rows=" + layout.getChildCount());
		for(int i=0;i<layout.getChildCount();i++){
			TableRow r = (TableRow) layout.getChildAt(i);
			Log.d(TAG, "row=" + i);
			Log.d(TAG, "enableInput(...): " + "NO. of columns=" + r.getChildCount());
			for(int j=0;j<r.getChildCount();j++){
				ImageView view = (ImageView)r.getChildAt(j);
				if(i!=0&&i!=mRow+1&&j!=0&&j!=mCol+1)
					view.setClickable(inputSwitch);
			}
		}
	}
	
	@Override
	public void createTemplate(TableLayout layout){
		mClickListener = realizeOnClickListener(layout);
		
		for(int i=0;i<mRow+2;i++){
        	TableRow row=new TableRow(mINewGameActivity.getContext());
        	row.setClipChildren(false);
        	
        	layout.addView(row, new TableLayout.LayoutParams(WC,WC));

        	for(int j=0;j<mCol+2;j++){
        		ImageView image=new ImageView(mINewGameActivity.getContext());
        		row.addView(image);
        		if(i!=0&&i!=mRow+1&&j!=0&&j!=mCol+1)
        			image.setOnClickListener(mClickListener);
        	}
        }							
	}

	private class CrossPuzzleBoardOnClickListener implements View.OnClickListener {
		private final static String TAG2 = ".CrossPuzzleBoardOnClickListener";
		private TableLayout mLayout;

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
					coord = rmap(i,j);
					break;
				}
			}
			
			return coord;
		}

		CrossPuzzleBoardOnClickListener(TableLayout layout){
			mLayout = layout;
		}
		@Override
		public void onClick(View v) {
			int[] coord = getViewLocation(v); 
			
			Log.i(TAG+TAG2, "onClick(...)" + "logical row=" + coord[0]);
			Log.i(TAG+TAG2, "onClick(...)" + "logical col=" + coord[1]);
		}
		
	}
	
	@Override
	public View.OnClickListener realizeOnClickListener(final TableLayout layout) {
		CrossPuzzleBoardOnClickListener listener = new CrossPuzzleBoardOnClickListener(layout);
		
		return listener;
	}

	@Override
	public Bitmap calculateDrawable(int i, int j){
		Bitmap d;
		int resId;
		
		resId = mINewGameActivity.map2DrawableResId(getCellDrawable(mResolution));
		Resources rs = mINewGameActivity.getContextResources();	
		d = Globals.loadRes2Bitmap(rs, resId, false);
	
		return d;
	}
}