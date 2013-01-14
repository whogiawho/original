package oms.cj.WuZiWay;

import oms.cj.WuZiLogic.VirtualWuZiBoard;

public class Globals {
	
	//used in EWay1.evaluate(TypeMap)
	//1. score table
	//    Chong   Live
	// 1   0      5
	// 2   5      20
	// 3   25     50
	// 4   60     +inf
	// 5  +inf    +inf
	// INVALID = 0
	public final static int[] TypeScore = {
		0,
		0,	5,	25,	60, 				Integer.MAX_VALUE,
		5,	20,	50,	Integer.MAX_VALUE,	Integer.MAX_VALUE,
	};
	
	//used in Node.getMaximumWithSingle
	//rTypeScore, mRTypes are for get empty candidates which get max scores
	public final static int[] rTypeScore = {
		20, 10, 10, 0,	
	};
	public final static int[] mRTypes = {
		VirtualWuZiBoard.LIVE2,
		VirtualWuZiBoard.CHONG2,
		VirtualWuZiBoard.LIVE1,
		VirtualWuZiBoard.CHONG1,
	};
}
