package oms.cj.WuZiWay;

import java.util.ArrayList;
import android.os.Handler;
import oms.cj.WuZiLogic.IStrategy;
import oms.cj.WuZiLogic.VirtualWuZi;

public class GetChong3Live2 extends StrategyWay implements IStrategy {
	
	GetChong3Live2(VirtualWuZi v, Handler handler, int strategy) {
		super(v, handler, strategy);
	}

	private int[] getChong3Live2(int type, ArrayList<Integer> list){
		int[] suggestPos = null;
		boolean isChong3, isLive2;
		
		for(int i=0;i<list.size();i++){
			int idx = list.get(i);
			int[] pos = mV.oneDim2twoDim(idx);
			mV.set(pos, type);
			isChong3=false; isLive2=false;
			for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
				if(mV.isChongSan(type, pos, j))
					isChong3=true;
				if(mV.isLive2(type, pos, j))
					isLive2=true;
			}
			mV.set(pos, VirtualWuZi.EMPTY);
			if(isChong3&&isLive2){
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
		
		suggestPos=getChong3Live2(myType, list1);
		if(suggestPos!=null)
			return suggestPos;
		
		suggestPos=getChong3Live2(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
				
		return suggestPos;
	}
	
	@Override
	public int[] execDefend(){
		int[] suggestPos = null;
		int hisType=mV.getHisQiZiType();
		ArrayList<Integer> list2=getCandidateEmptyPos(hisType);
		
		suggestPos=getChong3Live2(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
	
	@Override
	public int[] execAttack(){
		int[] suggestPos = null;
		int myType = mV.getMyQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType);
		
		suggestPos=getChong3Live2(myType, list1);
		if(suggestPos!=null)
			return suggestPos;

		return suggestPos;
	}

	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType), list2=getCandidateEmptyPos(hisType);

		suggestPos=getChong3Live2(myType, list1);
		if(suggestPos!=null)
			return suggestPos;
		
		suggestPos=getChong3Live2(hisType, list2);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}
}
