package oms.cj.WuZiWay;

import oms.cj.WuZiLogic.VirtualWuZiBoard;

public class EWay2 implements IEvaluate {

	@Override
	public int evaluate(TypeMap tMap) {
		int value;
		
		if(tMap.getMaps(VirtualWuZiBoard.CHONG5).size()!=0)
			value = Integer.MAX_VALUE;
		else
			value = tMap.getMinSteps2Be5();
		
		return value;
	}

}
