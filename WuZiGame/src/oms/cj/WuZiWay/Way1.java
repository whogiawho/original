package oms.cj.WuZiWay;

import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;
import android.os.Handler;

//Way1的判断序列：
//1.  成5
//2.  成活4
//3.  成冲4活3
//4.  成双3
//5.  成冲4冲3
//6.  成冲4活2
//7.  成活3
//8.  成双冲3
//9.  成冲3活2
//10. 冲3

public class Way1 extends Way implements ISuggest {
	@SuppressWarnings("unused")
	private final static String TAG="Way1";
	private final int mStrategy;
	
	public Way1(VirtualWuZi v, Handler handler, int strategy) {
		super(v, handler);
		mStrategy = strategy;
	}
	
	private int[] getMustStep(){
		int[] suggestPos=null;
		Way[] ways = { 
				new Get5(mV, null), 
				new GetLive4(mV, null),
				new GetChong4Live3(mV, null),
				new GetDoubleLive3(mV, null),
				new GetChong4Chong3(mV, null, mStrategy),
				new GetChong4Live2(mV, null, mStrategy),
				new GetLive3(mV, null),
				new GetDoubleChong3(mV, null, mStrategy),
				new GetDoubleLive2(mV, null, mStrategy),
				new GetChong3Live2(mV, null, mStrategy),
				new GetChong3(mV, null),
				new GetLive2(mV, null),
		};
		
		for(int i=0;i<ways.length;i++){
			Way way=ways[i];
			suggestPos=way.suggestPosition();
			if(suggestPos!=null)
				return suggestPos;			
		}
		
		return suggestPos;
	}
	
	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;
		suggestPos = getMustStep();
		if(suggestPos!=null)
			return suggestPos;
		
		//随机返回可选空位中的一个
		suggestPos=this.getByCandidateEmptyPos();
		if(suggestPos!=null)
			return suggestPos;
		
		//最后的方法是用RandomWay随机返回一个空位
		suggestPos=getByRandomWay();
		return suggestPos;
	}

}