package oms.cj.WuZiWay;

import java.util.ArrayList;
import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;
import android.os.Handler;

public class GetChong3 extends Way implements ISuggest {

	GetChong3(VirtualWuZi v, Handler handler) {
		super(v, handler);
	}
	
	private int[] getChong3(int type, ArrayList<Integer> list){
		int[] suggestPos=null;

		for(int i=0;i<list.size();i++){
			int[] pos = mV.oneDim2twoDim(list.get(i));
			mV.set(pos, type);
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				if(mV.isChongSan(type, pos, j)){
					suggestPos=pos;
					break;
				}
			}
			mV.set(pos, VirtualWuZi.EMPTY);
			if(suggestPos!=null)
				break;
		}

		return suggestPos;
	}
	
	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType), list2=getCandidateEmptyPos(hisType);
		
		suggestPos=getChong3(myType, list1);
		if(suggestPos!=null)
			return suggestPos;

		suggestPos=getChong3(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
}
