package oms.cj.WuZiBoard;

import oms.cj.WuZiLogic.BoardPositionEvent;
import oms.cj.WuZiLogic.IWuZiQiBetweenRealAndVirtual;
import oms.cj.WuZiLogic.LogicConfig;
import oms.cj.WuZiLogic.LogicResource;
import oms.cj.WuZiLogic.ScreenInfo;
import oms.cj.WuZiLogic.VirtualWuZi;
import oms.cj.utils.Globals;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class WuZiPuzzleBoard extends CrossPuzzleBoard{
	private final static String TAG = "WuZiPuzzleBoard";
	private final static int[][][] QiZiMap = new int[ScreenInfo.getSupportedScreens()][2][2];
	private final static int[] mFocusCellImageMap = new int[ScreenInfo.getSupportedScreens()];
	private IWuZiQiBetweenRealAndVirtual mInterface;
	public final static int NORMAL=0;
	public final static int FOCUS=1;
	private int mWhenHitBoard;
	
	private void setupBlackAndWhiteQi(){
		QiZiMap[ScreenInfo.HVGA][VirtualWuZi.BLACK][NORMAL] = LogicResource.WZPB_HVGA_DRAWABLE_QIZI_BLACK;
		QiZiMap[ScreenInfo.HVGA][VirtualWuZi.WHITE][NORMAL] = LogicResource.WZPB_HVGA_DRAWABLE_QIZI_WHITE;
		QiZiMap[ScreenInfo.HVGA][VirtualWuZi.BLACK][FOCUS] = LogicResource.WZPB_HVGA_DRAWABLE_QIZI_BLACK_FOCUS;
		QiZiMap[ScreenInfo.HVGA][VirtualWuZi.WHITE][FOCUS] = LogicResource.WZPB_HVGA_DRAWABLE_QIZI_WHITE_FOCUS;

		QiZiMap[ScreenInfo.WVGA][VirtualWuZi.BLACK][NORMAL] = LogicResource.WZPB_WVGA_DRAWABLE_QIZI_BLACK;
		QiZiMap[ScreenInfo.WVGA][VirtualWuZi.WHITE][NORMAL] = LogicResource.WZPB_WVGA_DRAWABLE_QIZI_WHITE;
		QiZiMap[ScreenInfo.WVGA][VirtualWuZi.BLACK][FOCUS] = LogicResource.WZPB_WVGA_DRAWABLE_QIZI_BLACK_FOCUS;
		QiZiMap[ScreenInfo.WVGA][VirtualWuZi.WHITE][FOCUS] = LogicResource.WZPB_WVGA_DRAWABLE_QIZI_WHITE_FOCUS;
	}
	
	public WuZiPuzzleBoard(int row, int col, int resolution, INewGameActivity iNewGameActivity, int whenHitBoard) {
		super(row, col, resolution, iNewGameActivity);
		
		mWhenHitBoard = whenHitBoard;
		mFocusCellImageMap[ScreenInfo.HVGA] = LogicResource.WZPB_HVGA_DRAWABLE_BLANK_FOCUS;
		mFocusCellImageMap[ScreenInfo.WVGA] = LogicResource.WZPB_WVGA_DRAWABLE_BLANK_FOCUS;
		
		setupBlackAndWhiteQi();
	}
	
	public void attachTo(IWuZiQiBetweenRealAndVirtual listener){
		mInterface = listener;
	}
	
	public void setQiZi(TableLayout layout, int[] pos, int type, int focus){
		int[] coord = map(pos[0], pos[1]);
		
		TableRow r = (TableRow)layout.getChildAt(coord[0]);
		if(r==null){
			Log.e(TAG, "setQiZi(...): !!!Not Expected!!!" + "row["+coord[0]+"]" + "is null");
			return;
		}
		ImageView view = (ImageView)r.getChildAt(coord[1]);
		if(view==null){
			Log.e(TAG, "setQiZi(...): !!!Not Expected!!!" + "imageview["+coord[0]+"]"+"["+coord[1]+"]" + "is null");
			return;
		}
		
		Bitmap d = getDrawableByTypeAndFocus(type, focus);
		view.setImageBitmap(d);			
	}
	
	private Bitmap getDrawableByTypeAndFocus(int type, int focus){
		Bitmap d = null;
		int resId;
		
		Resources rs = mINewGameActivity.getContextResources();	
		if(type == VirtualWuZi.EMPTY){
			if(focus==NORMAL){
				resId = mINewGameActivity.map2DrawableResId(getCellDrawable(mResolution));
				d = Globals.loadRes2Bitmap(rs, resId, false);
			} else { 
				resId = mINewGameActivity.map2DrawableResId(getFocusCellDrawable(mResolution));
				d = Globals.loadRes2Bitmap(rs, resId, false);
			}
		} else if(type == VirtualWuZi.BLACK || type == VirtualWuZi.WHITE){
			resId = mINewGameActivity.map2DrawableResId(QiZiMap[mResolution][type][focus]);
			d = Globals.loadRes2Bitmap(rs, resId, false);
		} else {
			Log.e(TAG, "getDrawable(...): !!!Not Expected!!!" + "qiziType=" + type);
		}
		
		return d;
	}
	
	public void gameOver(final TableLayout layout, int side, int state){
		String subject = VirtualWuZi.strQiZiList[side];
		String outStr="";
		
		if(state==VirtualWuZi.TIE){
			outStr="和了！";
		} else if(state==VirtualWuZi.WIN){
			outStr=subject+"赢";
		} else if(state == VirtualWuZi.LOSE_SANSAN){
			outStr=subject+"输;"+"三三禁手";
		} else if(state == VirtualWuZi.LOSE_CHAHNGLIAN){
			outStr=subject+"输;"+"长连禁手";
		} else if(state == VirtualWuZi.LOSE_SISI) {
			outStr=subject+"输;"+"四四禁手";
		} else {
			outStr=subject;
		}
		
        AlertDialog dlg = new AlertDialog.Builder(mINewGameActivity.getContext())
        .setTitle(outStr)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	WuZiPuzzleBoard.this.enableInput(layout, false);
            }
        })
        .create();
        dlg.show();
        
        mINewGameActivity.onGameOver();
	}
	
	private class WuZiPuzzleBoardOnClickListener implements View.OnClickListener {
		private final static String TAG2 = ".WuZiPuzzleBoardOnClickListener";
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

		WuZiPuzzleBoardOnClickListener(TableLayout layout){
			mLayout = layout;
		}
		
		@Override
		public void onClick(View v) {
			int[] currentFocus = getViewLocation(v); 
			Log.i(TAG+TAG2, "onClick(...)" + "logical row=" + currentFocus[0]);
			Log.i(TAG+TAG2, "onClick(...)" + "logical col=" + currentFocus[1]);
			
			if(mWhenHitBoard==LogicConfig.MOVEFOCUSONLY){
				//设置新的focus
				int[] prevFocus = mInterface.getFocus();
				mInterface.setFocus(currentFocus);
				int prevType = mInterface.getQiZiType(prevFocus), currentType = mInterface.getQiZiType(currentFocus);
				setQiZi(mLayout, prevFocus, prevType, WuZiPuzzleBoard.NORMAL);
				setQiZi(mLayout, currentFocus, currentType, WuZiPuzzleBoard.FOCUS);
			} else {
				BoardPositionEvent event = new BoardPositionEvent(WuZiPuzzleBoard.this, currentFocus[0], currentFocus[1], mLayout);
				mInterface.onQiZi2Position(event);
			}
		}
	}
	
	@Override
	public View.OnClickListener realizeOnClickListener(final TableLayout layout) {
		WuZiPuzzleBoardOnClickListener listener = new WuZiPuzzleBoardOnClickListener(layout);
		
		return listener;
	}
	
	private int getFocusCellDrawable(int resolution){
		return mFocusCellImageMap[resolution];
	}
	
	@Override
	public Bitmap calculateDrawable(int i, int j){
		Bitmap d=null;
		
		//get focus
		int qiZiType = mInterface.getQiZiType(i, j);
		int focus=NORMAL;
		int[] focusPos=mInterface.getFocus();
		if(focusPos!=null&&focusPos[0]==i&&focusPos[1]==j){
			focus=FOCUS;
		}
		
		//get drawable by (type, focus, resolution)
		d = getDrawableByTypeAndFocus(qiZiType, focus);
		
		return d;
	}
}