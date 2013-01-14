package oms.cj.WuZiBoard;

import oms.cj.WuZiLogic.LogicResource;
import oms.cj.WuZiLogic.ScreenInfo;
import oms.cj.utils.Globals;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

/*
 * (1,1) 			border0
 * (1,mCol+2) 		border2
 * (mRow+2,mCol+2) 	border4
 * (mRow+2,1)		border6
 * (0,0)            border8
 * (0,1)            border9
 * (1,0)            border10
 * (0,mCol+2)       border11
 * (mRow+2,0)       border12
 * 
 *          (0,0) 		(0,1)		.............................................(0,mCol+2) 
 *          (1,0) 		border0		...........border1............................border2   
 *          (2,0) 		  .															.
 *          (3,0) 		  .															.
 *			  .			  .															.
 *			  .			  .															.
 * 			  .			border7													  border3
 * 			  .			  .															.
 * 			(mRow+1,0)	  .															.
 * 			(mRow+2,0)  border6		...........border5............................border4
 * 
 * 
 */

public class WuZiCoordPuzzleBoard extends WuZiPuzzleBoard{
	protected final static int paddings[][] = {
		{0, 0, 10},	//HVGA
		{80, 0, 10},	//WVGA
	};
	private final static String TAG="WuZiCoordPuzzleBoard";
	private final int[][] mBorderCellImageMap = new int[ScreenInfo.getSupportedScreens()][8+5];
	private final int[][] mCoordXMap = new int[ScreenInfo.getSupportedScreens()][15];
	private final int[][] mCoordYMap = new int[ScreenInfo.getSupportedScreens()][15];
	
	@Override
	public void getpaddings(int width, int height, int[] paddings){
		Log.i(TAG+".getpaddings", "delta[mResolution][0] = " + delta[mResolution][0]);
		Log.i(TAG+".getpaddings", "mCellSize = " + mCellSize);
		paddings[0]=WuZiCoordPuzzleBoard.paddings[mResolution][0];
		paddings[1]=WuZiCoordPuzzleBoard.paddings[mResolution][1];
		paddings[2]=WuZiCoordPuzzleBoard.paddings[mResolution][2];						
	}
	
	public WuZiCoordPuzzleBoard(int row, int col, int resolution, INewGameActivity iNewGameActivity, int whenHitBoard) {
		super(row, col, resolution, iNewGameActivity, whenHitBoard);
		setupBorderCellMap();
		setupCoordXMap();
		setupCoordYMap();
	}
	
	private void setupCoordXMap(){
		mCoordXMap[ScreenInfo.HVGA][0] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_a;
		mCoordXMap[ScreenInfo.HVGA][1] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_b;
		mCoordXMap[ScreenInfo.HVGA][2] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_c;
		mCoordXMap[ScreenInfo.HVGA][3] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_d;
		mCoordXMap[ScreenInfo.HVGA][4] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_e;
		mCoordXMap[ScreenInfo.HVGA][5] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_f;
		mCoordXMap[ScreenInfo.HVGA][6] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_g;
		mCoordXMap[ScreenInfo.HVGA][7] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_h;
		mCoordXMap[ScreenInfo.HVGA][8] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_i;
		mCoordXMap[ScreenInfo.HVGA][9] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_j;
		mCoordXMap[ScreenInfo.HVGA][10] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_k;
		mCoordXMap[ScreenInfo.HVGA][11] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_l;
		mCoordXMap[ScreenInfo.HVGA][12] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_m;
		mCoordXMap[ScreenInfo.HVGA][13] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_n;
		mCoordXMap[ScreenInfo.HVGA][14] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_X_o;

		mCoordXMap[ScreenInfo.WVGA][0] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_a;
		mCoordXMap[ScreenInfo.WVGA][1] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_b;
		mCoordXMap[ScreenInfo.WVGA][2] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_c;
		mCoordXMap[ScreenInfo.WVGA][3] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_d;
		mCoordXMap[ScreenInfo.WVGA][4] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_e;
		mCoordXMap[ScreenInfo.WVGA][5] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_f;
		mCoordXMap[ScreenInfo.WVGA][6] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_g;
		mCoordXMap[ScreenInfo.WVGA][7] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_h;
		mCoordXMap[ScreenInfo.WVGA][8] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_i;
		mCoordXMap[ScreenInfo.WVGA][9] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_j;
		mCoordXMap[ScreenInfo.WVGA][10] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_k;
		mCoordXMap[ScreenInfo.WVGA][11] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_l;
		mCoordXMap[ScreenInfo.WVGA][12] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_m;
		mCoordXMap[ScreenInfo.WVGA][13] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_n;
		mCoordXMap[ScreenInfo.WVGA][14] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_X_o;
	}

	private void setupCoordYMap(){
		mCoordYMap[ScreenInfo.HVGA][0] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_1;
		mCoordYMap[ScreenInfo.HVGA][1] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_2;
		mCoordYMap[ScreenInfo.HVGA][2] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_3;
		mCoordYMap[ScreenInfo.HVGA][3] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_4;
		mCoordYMap[ScreenInfo.HVGA][4] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_5;
		mCoordYMap[ScreenInfo.HVGA][5] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_6;
		mCoordYMap[ScreenInfo.HVGA][6] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_7;
		mCoordYMap[ScreenInfo.HVGA][7] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_8;
		mCoordYMap[ScreenInfo.HVGA][8] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_9;
		mCoordYMap[ScreenInfo.HVGA][9] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_10;
		mCoordYMap[ScreenInfo.HVGA][10] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_11;
		mCoordYMap[ScreenInfo.HVGA][11] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_12;
		mCoordYMap[ScreenInfo.HVGA][12] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_13;
		mCoordYMap[ScreenInfo.HVGA][13] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_14;
		mCoordYMap[ScreenInfo.HVGA][14] = LogicResource.WZCPB_HVGA_DRAWABLE_COORD_Y_15;

		mCoordYMap[ScreenInfo.WVGA][0] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_1;
		mCoordYMap[ScreenInfo.WVGA][1] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_2;
		mCoordYMap[ScreenInfo.WVGA][2] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_3;
		mCoordYMap[ScreenInfo.WVGA][3] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_4;
		mCoordYMap[ScreenInfo.WVGA][4] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_5;
		mCoordYMap[ScreenInfo.WVGA][5] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_6;
		mCoordYMap[ScreenInfo.WVGA][6] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_7;
		mCoordYMap[ScreenInfo.WVGA][7] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_8;
		mCoordYMap[ScreenInfo.WVGA][8] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_9;
		mCoordYMap[ScreenInfo.WVGA][9] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_10;
		mCoordYMap[ScreenInfo.WVGA][10] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_11;
		mCoordYMap[ScreenInfo.WVGA][11] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_12;
		mCoordYMap[ScreenInfo.WVGA][12] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_13;
		mCoordYMap[ScreenInfo.WVGA][13] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_14;
		mCoordYMap[ScreenInfo.WVGA][14] = LogicResource.WZCPB_WVGA_DRAWABLE_COORD_Y_15;
	}

	private void setupBorderCellMap(){
		mBorderCellImageMap[ScreenInfo.HVGA][0] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_1;
		mBorderCellImageMap[ScreenInfo.HVGA][1] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_2;
		mBorderCellImageMap[ScreenInfo.HVGA][2] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_3;
		mBorderCellImageMap[ScreenInfo.HVGA][3] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_4;
		mBorderCellImageMap[ScreenInfo.HVGA][4] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_5;
		mBorderCellImageMap[ScreenInfo.HVGA][5] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_6;
		mBorderCellImageMap[ScreenInfo.HVGA][6] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_7;
		mBorderCellImageMap[ScreenInfo.HVGA][7] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_8;
		mBorderCellImageMap[ScreenInfo.HVGA][8] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_9;
		mBorderCellImageMap[ScreenInfo.HVGA][9] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_10;
		mBorderCellImageMap[ScreenInfo.HVGA][10] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_11;
		mBorderCellImageMap[ScreenInfo.HVGA][11] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_12;
		mBorderCellImageMap[ScreenInfo.HVGA][12] = LogicResource.WZCPB_HVGA_DRAWABLE_BORDER_13;

		mBorderCellImageMap[ScreenInfo.WVGA][0] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_1;
		mBorderCellImageMap[ScreenInfo.WVGA][1] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_2;
		mBorderCellImageMap[ScreenInfo.WVGA][2] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_3;
		mBorderCellImageMap[ScreenInfo.WVGA][3] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_4;
		mBorderCellImageMap[ScreenInfo.WVGA][4] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_5;
		mBorderCellImageMap[ScreenInfo.WVGA][5] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_6;
		mBorderCellImageMap[ScreenInfo.WVGA][6] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_7;
		mBorderCellImageMap[ScreenInfo.WVGA][7] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_8;
		mBorderCellImageMap[ScreenInfo.WVGA][8] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_9;
		mBorderCellImageMap[ScreenInfo.WVGA][9] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_10;
		mBorderCellImageMap[ScreenInfo.WVGA][10] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_11;
		mBorderCellImageMap[ScreenInfo.WVGA][11] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_12;
		mBorderCellImageMap[ScreenInfo.WVGA][12] = LogicResource.WZCPB_WVGA_DRAWABLE_BORDER_13;
	}
	
	@Override
	protected int[] map(int row, int col){
		int[] newcoord = new int[2];
		
		newcoord[0]=row+2;
		newcoord[1]=col+2;
		
		return newcoord;		
	}
	
	@Override
	protected int[] rmap(int row, int col){
		int[] newcoord = new int[2];
		
		newcoord[0]=row-2;
		newcoord[1]=col-2;
		
		return newcoord;
	}
	
	@Override
    public void show(TableLayout layout){
		int resId;
		
		Resources rs = mINewGameActivity.getContextResources();	
		for(int i=0;i<mRow+3;i++){
			TableRow row=(TableRow)layout.getChildAt(i);
			for(int j=0;j<mCol+3;j++){
				ImageView image=(ImageView)row.getChildAt(j);
				Bitmap d;
				if(i==0&&j==0){ 	//special cell
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][8]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==0&&j==1){
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][9]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==1&&j==0){
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][10]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==0&&j==mCol+2){
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][11]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==mRow+2&&j==0){
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][12]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				}else if(i==1&&j==1){  //border0
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][0]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==1&&j==mCol+2){ //border2
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][2]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==mRow+2&&j==mCol+2){ //border4
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][4]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==mRow+2&&j==1){ //border6
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][6]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==1){ //border1
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][1]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(j==mCol+2){ //border3
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][3]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==mRow+2){ //border5
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][5]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(j==1){ //border7
					resId = mINewGameActivity.map2DrawableResId(mBorderCellImageMap[mResolution][7]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(j==0){	//标注列坐标
					resId = mINewGameActivity.map2DrawableResId(mCoordYMap[mResolution][i-2]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else if(i==0){	//标注横坐标
					resId = mINewGameActivity.map2DrawableResId(mCoordXMap[mResolution][j-2]);
					d = Globals.loadRes2Bitmap(rs, resId, false);
				} else {
					int[] coord = rmap(i,j);
            		d = calculateDrawable(coord[0],coord[1]);
				}
				if(d!=null){
					TableRow.LayoutParams params = new TableRow.LayoutParams(d.getWidth(), d.getHeight());
					image.setLayoutParams(params);
					image.getParent().requestLayout();
					image.setImageBitmap(d); 
				}
			}
		}
	}
	
	@Override
	public void createTemplate(TableLayout layout){
		mClickListener = realizeOnClickListener(layout);
		
		for(int i=0;i<mRow+3;i++){
        	TableRow row=new TableRow(mINewGameActivity.getContext());
        	row.setClipChildren(false); 
        	
        	layout.addView(row, new TableLayout.LayoutParams(WC,WC));

        	for(int j=0;j<mCol+3;j++){
        		ImageView image=new ImageView(mINewGameActivity.getContext());
        		row.addView(image);
        		if(i!=1&&i!=mRow+2&&j!=1&&j!=mCol+2&&i!=0&&j!=0){
        			image.setOnClickListener(mClickListener);
        		}
        	}
        }							
	}
	
	@Override
	public void enableInput(TableLayout layout, boolean inputSwitch){
//		Log.d(TAG, "enableInput(...): " + "NO. of rows=" + layout.getChildCount());
		for(int i=0;i<layout.getChildCount();i++){
			TableRow r = (TableRow) layout.getChildAt(i);
//			Log.d(TAG, "row=" + i);
//			Log.d(TAG, "enableInput(...): " + "NO. of columns=" + r.getChildCount());
			for(int j=0;j<r.getChildCount();j++){
				ImageView view = (ImageView)r.getChildAt(j);
				if(i!=1&&i!=mRow+2&&j!=1&&j!=mCol+2&&i!=0&&j!=0)
					view.setClickable(inputSwitch);
			}
		}
	}
}