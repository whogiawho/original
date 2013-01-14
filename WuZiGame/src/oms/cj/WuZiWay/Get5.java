package oms.cj.WuZiWay;

import java.util.ArrayList;

import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;

import android.os.Handler;

public class Get5 extends Way implements ISuggest{

	Get5(VirtualWuZi v, Handler handler){
		super(v, handler);
	}
	
	private int[] getChengWu(int type){
		int[] suggestPos=null;
		
		ArrayList<Integer> avaiPosList = getCandidateEmptyPos(type);
		for(int i=0;i<avaiPosList.size();i++){
			int idx = avaiPosList.get(i);
			int[] pos = mV.oneDim2twoDim(idx);
			mV.set(pos, type);
			if(mV.checkWuZiIdentical(type, pos)==VirtualWuZi.WIN){
				suggestPos=pos;
				mV.set(pos, VirtualWuZi.EMPTY);
				break;
			}
			mV.set(pos, VirtualWuZi.EMPTY);
		}
		
		return suggestPos;
	}

	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		
		suggestPos = getChengWu(myType);
		if(suggestPos!=null)
			return suggestPos;
		suggestPos = getChengWu(hisType);
		if(suggestPos!=null)
			return suggestPos;

		return suggestPos;
	}	
}