package oms.cj.WuZiLogic;

import java.util.EventObject;

import android.widget.TableLayout;

public class BoardPositionEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -592719980286671755L;
	private int mRow;
	private int mCol;
	private TableLayout mLayout;
	
	public BoardPositionEvent(Object source, int row, int col, TableLayout layout) {
		super(source);
		setPosition(row, col);
		mLayout = layout;
	}

	private void setPosition(int row, int col){
		mRow = row;
		mCol = col;
	}
	
	public int[] getPosition(){
		int[] coord = new int[2];
		coord[0]= mRow;
		coord[1]= mCol;
		
		return coord;
	}
	
	public TableLayout getLayout(){
		return mLayout;
	}
}
