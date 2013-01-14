package oms.cj.WuZiWay;

import java.util.ArrayList;

import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;

import android.os.Handler;

public class GetLive4 extends Way implements ISuggest {

	GetLive4(VirtualWuZi v, Handler handler) {
		super(v, handler);
	}
	
	private boolean isLive4(int type, int[] pos){
		for(int i=0;i<VirtualWuZi.LINEDIRTOTAL;i++){
			if(mV.isLiveSi(type, pos, i, null))
				return true;
		}
		return false;
	}
	
	public int[] getLive4(int type){
		int[] suggestPos=null;
		
		ArrayList<Integer> avaiPosList = getCandidateEmptyPos(type);
		for(int i=0;i<avaiPosList.size();i++){
			int idx = avaiPosList.get(i);
			int[] pos = mV.oneDim2twoDim(idx);
			mV.set(pos, type);
			if(isLive4(type, pos)){
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
		
		suggestPos = getLive4(myType);
		if(suggestPos!=null)
			return suggestPos;
		suggestPos = getLive4(hisType);
		if(suggestPos!=null)
			return suggestPos;
	
		return suggestPos;
	}
}
