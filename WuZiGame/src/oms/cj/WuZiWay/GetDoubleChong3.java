package oms.cj.WuZiWay;

import java.util.ArrayList;
import android.os.Handler;
import oms.cj.WuZiLogic.IStrategy;
import oms.cj.WuZiLogic.VirtualWuZi;

public class GetDoubleChong3 extends StrategyWay implements IStrategy {
	
	GetDoubleChong3(VirtualWuZi v, Handler handler, int strategy) {
		super(v, handler, strategy);
	}

	private int[] getDoubleChong3(int type, ArrayList<Integer> list){
		int[] suggestPos = null;
		
		for(int i=0;i<list.size();i++){
			int idx = list.get(i);
			int[] pos = mV.oneDim2twoDim(idx);
			mV.set(pos, type);
			int nChong3=0;
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				if(mV.isChongSan(type, pos, j))
					nChong3++;
			}
			mV.set(pos, VirtualWuZi.EMPTY);
			if(nChong3>=2){
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
		
		suggestPos=getDoubleChong3(myType, list1);
		if(suggestPos!=null)
			return suggestPos;
		
		suggestPos=getDoubleChong3(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
				
		return suggestPos;
	}
	
	@Override
	public int[] execDefend(){
		int[] suggestPos = null;
		int  hisType=mV.getHisQiZiType();
		ArrayList<Integer> list2=getCandidateEmptyPos(hisType);
		
		suggestPos=getDoubleChong3(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
	
	@Override
	public int[] execAttack(){
		int[] suggestPos = null;
		int myType = mV.getMyQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType);
		
		suggestPos=getDoubleChong3(myType, list1);
		if(suggestPos!=null)
			return suggestPos;

		return suggestPos;
	}
}
