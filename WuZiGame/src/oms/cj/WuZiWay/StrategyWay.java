package oms.cj.WuZiWay;


import android.os.Handler;
import oms.cj.WuZiLogic.IStrategy;
import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.LogicConfig;
import oms.cj.WuZiLogic.VirtualWuZi;

public class StrategyWay extends Way implements ISuggest, IStrategy{
	@SuppressWarnings("unused")
	private final static String TAG = "StrategyWay";
	protected int mStrategy;
	
	StrategyWay(VirtualWuZi v, Handler handler, int strategy) {
		super(v, handler);
		mStrategy = strategy;
	}

	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;

		switch(mStrategy){
		case LogicConfig.DEFEND:
			suggestPos = execDefend();
			break;
		case LogicConfig.ADBALANCE:
			suggestPos = execADBalance();
			break;
		case LogicConfig.ATTACK:
			suggestPos = execAttack();
		default:
			break;

		}
		return suggestPos;
	}

	@Override
	public int[] execADBalance() {
		return null;
	}

	@Override
	public int[] execAttack() {
		return null;
	}

	@Override
	public int[] execDefend() {
		return null;
	}
}
