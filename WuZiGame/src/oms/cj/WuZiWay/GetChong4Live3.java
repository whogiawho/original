package oms.cj.WuZiWay;

import java.util.ArrayList;

import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;

import android.os.Handler;

public class GetChong4Live3 extends Way implements ISuggest {

	GetChong4Live3(VirtualWuZi v, Handler handler) {
		super(v, handler);
	}

	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;
		Way2_1 way = new Way2_1(mV, null);
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType), list2=getCandidateEmptyPos(hisType);
		
		suggestPos=way.getChong4andLive3(myType, list1);
		if(suggestPos!=null)
			return suggestPos;

		suggestPos=way.getChong4andLive3(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;

		return suggestPos;
	}
}
