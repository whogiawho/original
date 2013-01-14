package oms.cj.WuZiWay;

import java.util.ArrayList;

import android.os.Handler;
import oms.cj.WuZiLogic.IStrategy;
import oms.cj.WuZiLogic.VirtualWuZi;

public class GetDoubleLive2 extends StrategyWay implements IStrategy {
	
	GetDoubleLive2(VirtualWuZi v, Handler handler, int strategy) {
		super(v, handler, strategy);
	}

	private int[] getDoubleLive2(int type, ArrayList<Integer> list){
		int[] suggestPos = null;
		
		for(int i=0;i<list.size();i++){
			int idx = list.get(i);
			int[] pos = mV.oneDim2twoDim(idx);
			mV.set(pos, type);
			int nLive2=0;
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				if(mV.isLive2(type, pos, j))
					nLive2++;
			}
			mV.set(pos, VirtualWuZi.EMPTY);
			if(nLive2>=2){
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
		
		suggestPos=getDoubleLive2(myType, list1);
		if(suggestPos!=null)
			return suggestPos;
		
		suggestPos=getDoubleLive2(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
				
		return suggestPos;
	}
	
	@Override
	public int[] execDefend(){
		int[] suggestPos = null;
		int hisType=mV.getHisQiZiType();
		ArrayList<Integer> list2=getCandidateEmptyPos(hisType);
		
		suggestPos=getDoubleLive2(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
	
	@Override
	public int[] execAttack(){
		int[] suggestPos = null;
		int myType = mV.getMyQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType);
		
		suggestPos=getDoubleLive2(myType, list1);
		if(suggestPos!=null)
			return suggestPos;

		return suggestPos;
	}
}
