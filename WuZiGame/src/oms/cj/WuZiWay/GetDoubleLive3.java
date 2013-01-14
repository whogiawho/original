package oms.cj.WuZiWay;

import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;

import android.os.Handler;

public class GetDoubleLive3 extends Way implements ISuggest {

	GetDoubleLive3(VirtualWuZi v, Handler handler) {
		super(v, handler);
	}

	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;
		Way2_1 way = new Way2_1(mV, null);
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();

		suggestPos=way.getDoubleLive3(myType, hisType);
		if(suggestPos!=null)
			return suggestPos;

		return suggestPos;
	}
}
