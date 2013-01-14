package oms.cj.WuZiWay;

import java.util.Arrays;

import oms.cj.WuZiLogic.VirtualWuZiBoard;

public class EmptyTypeMap extends TypeMap {
	public  final static int INVALID = -1;
	
	int[][] mEmptyPositions;
	
	public EmptyTypeMap(int qiZiType, int dirNO, VirtualWuZiBoard b){
		super(qiZiType);
		
		int row = b.getRows(), col = b.getCols();
		mEmptyPositions = new int[row*col][dirNO];
		for(int i=0;i<mEmptyPositions.length;i++)
			Arrays.fill(mEmptyPositions[i], INVALID);
	}
	
	public int getEmptyPositionsType(int idx, int dir){
		
		return mEmptyPositions[idx][dir];
	}
	
	public void setEmptyPositionsType(int idx, int dir, int type){
		mEmptyPositions[idx][dir] = type;
	}
}
