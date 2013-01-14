package oms.cj.WuZiWay;

import java.util.HashSet;

import oms.cj.WuZiLogic.VirtualWuZiBoard;

public interface IGameTree {
	public HashSet<Integer> getCandidatePosition(VirtualWuZiBoard b, TypeMap myTypeMap, TypeMap hisTypeMap);
	public Node createSubNode(VirtualWuZiBoard b);
	public int evaluate(int depth, int alpha, int beta);
}
