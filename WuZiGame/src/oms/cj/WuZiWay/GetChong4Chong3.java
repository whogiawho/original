package oms.cj.WuZiWay;

import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;
import oms.cj.WuZiLogic.IStrategy;
import oms.cj.WuZiLogic.VirtualWuZi;

public class GetChong4Chong3 extends StrategyWay implements IStrategy{
	private final static String TAG = "GetChong4Chong3";
	
	GetChong4Chong3(VirtualWuZi v, Handler handler, int strategy) {
		super(v, handler, strategy);
	}

	private int[] getChong4Chong3(int type, ArrayList<Integer> list){
		int[] suggestPos = null;
		
		for(int i=0;i<list.size();i++){
			int idx = list.get(i);
			int[] pos = mV.oneDim2twoDim(idx);
			mV.set(pos, type);
			boolean isChong4=false, isChong3=false;
			Log.i(TAG, "getChong4Chogn3(...): " + "isChong4="+isChong4);
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				int count = mV.getChongSiCount(type, pos, j, null, null);
				if(count==1)
					isChong4=true;
				if(mV.isChongSan(type, pos, j))
					isChong3=true;
			}
			mV.set(pos, VirtualWuZi.EMPTY);
			if(isChong4&&isChong3){
				suggestPos=pos;
				break;
			}
		}
		return suggestPos;
	}
	
	@Override
	public int[] execADBalance(){
		int[] suggestPos = null;
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType), list2=getCandidateEmptyPos(hisType);
		
		suggestPos=getChong4Chong3(myType, list1);
		if(suggestPos!=null)
			return suggestPos;
		
		suggestPos=getChong4Chong3(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
				
		return suggestPos;
	}
	
	@Override
	public int[] execDefend(){
		int[] suggestPos = null;
		int hisType=mV.getHisQiZiType();
		ArrayList<Integer> list2=getCandidateEmptyPos(hisType);
		
		suggestPos=getChong4Chong3(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
	
	@Override
	public int[] execAttack(){
		int[] suggestPos = null;
		int myType = mV.getMyQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType);
		
		suggestPos=getChong4Chong3(myType, list1);
		if(suggestPos!=null)
			return suggestPos;

		return suggestPos;
	}
}
