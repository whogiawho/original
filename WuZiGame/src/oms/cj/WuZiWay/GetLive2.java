package oms.cj.WuZiWay;

import java.util.ArrayList;
import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;
import android.os.Handler;

public class GetLive2 extends Way implements ISuggest {

	GetLive2(VirtualWuZi v, Handler handler) {
		super(v, handler);
	}
	
	private int[] getLive2(int type, ArrayList<Integer> list){
		int[] suggestPos=null;

		for(int i=0;i<list.size();i++){
			int[] pos = mV.oneDim2twoDim(list.get(i));
			mV.set(pos, type);
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				if(mV.isLive2(type, pos, j)){
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
		
		suggestPos=getLive2(myType, list1);
		if(suggestPos!=null)
			return suggestPos;

		suggestPos=getLive2(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
}
