package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.HashSet;

import oms.cj.WuZiLogic.Result;
import oms.cj.WuZiLogic.VirtualWuZiBoard;
import android.util.Log;

public class EWay1 implements IEvaluate {
	private final static String TAG = "EWay1";
	
	@Override
	public int evaluate(TypeMap tMap) {
		int score = 0;
		int[] map = {
			0,
			tMap.getMaps(VirtualWuZiBoard.CHONG1).size(),
			tMap.getMaps(VirtualWuZiBoard.CHONG2).size(),
			tMap.getMaps(VirtualWuZiBoard.CHONG3).size(),
			tMap.getMaps(VirtualWuZiBoard.CHONG4).size(),
			tMap.getMaps(VirtualWuZiBoard.CHONG5).size(),
			tMap.getMaps(VirtualWuZiBoard.LIVE1).size(),
			tMap.getMaps(VirtualWuZiBoard.LIVE2).size(),
			tMap.getMaps(VirtualWuZiBoard.LIVE3).size(),
			tMap.getMaps(VirtualWuZiBoard.LIVE4).size(),
		};

		if(map[VirtualWuZiBoard.CHONG5]>0||map[VirtualWuZiBoard.LIVE4]>0){
			String out = String.format("map[CHONG5|LIVE4]=%d|%d", 
					map[VirtualWuZiBoard.CHONG5], map[VirtualWuZiBoard.LIVE4]);
			Log.i(TAG+".evaluate", out);
			return Integer.MAX_VALUE;
		}
		if(map[VirtualWuZiBoard.LIVE4]+map[VirtualWuZiBoard.CHONG4]>=2){
			String out = String.format("map[LIVE4]+map[CHONG4]=%d", 
					map[VirtualWuZiBoard.LIVE4]+map[VirtualWuZiBoard.CHONG4]);
			Log.i(TAG+".evaluate", out);
			return Integer.MAX_VALUE;
		}
		if(map[VirtualWuZiBoard.CHONG4]>=1&&map[VirtualWuZiBoard.LIVE3]>=1){
			String out = String.format("map[CHONG4|LIVE3]=%d|%d", 
					map[VirtualWuZiBoard.CHONG4], map[VirtualWuZiBoard.LIVE3]);
			Log.i(TAG+".evaluate", out);
			return Integer.MAX_VALUE;
		}
		if(map[VirtualWuZiBoard.LIVE3]>=2){
			String out = String.format("map[LIVE3]=%d", map[VirtualWuZiBoard.LIVE3]);
			Log.i(TAG+".evaluate", out);
			return Integer.MAX_VALUE;
		}
		int c3l2IntersectionNO = getC3L2Intersection(tMap);
		if(c3l2IntersectionNO>=2 ||
			(c3l2IntersectionNO==1&&(map[VirtualWuZiBoard.LIVE3]>=1||map[VirtualWuZiBoard.CHONG4]>=1))){
			String out = String.format("map[CHONG4|LIVE3]=%d|%d, map[CHONG3&LIVE2]=%d", 
					map[VirtualWuZiBoard.CHONG4], map[VirtualWuZiBoard.LIVE3], 
					c3l2IntersectionNO);
			Log.i(TAG+".evaluate", out);
			return Integer.MAX_VALUE;
		}
		int l2l2IntersectionNO = getL2Intersection(tMap);
		if(l2l2IntersectionNO>=2 || 
			(l2l2IntersectionNO==1&&(map[VirtualWuZiBoard.LIVE3]>=1||map[VirtualWuZiBoard.CHONG4]>=1))){
			String out = String.format("map[CHONG4|LIVE3]=%d|%d, map[LIVE2&LIVE2]=%d", 
					map[VirtualWuZiBoard.CHONG4], map[VirtualWuZiBoard.LIVE3], 
					l2l2IntersectionNO);
			Log.i(TAG+".evaluate", out);
			return Integer.MAX_VALUE;
		}
		
		for(int i=1;i<5;i++){
			score += map[i]*Globals.TypeScore[i];
		}
		for(int i=6;i<9;i++){
			score += map[i]*Globals.TypeScore[i];
		}

		return score;
	}

	private int getL2Intersection(TypeMap tMap){
		int isNO = 0;
		ArrayList<Result> rList = tMap.getMaps(VirtualWuZiBoard.LIVE2);
		
		for(int i=0;i<rList.size();i++){
			Result r1 = rList.get(i);
			for(int j=i+1;j<rList.size();j++){
				Result r2 = rList.get(j);
				HashSet<Integer> isSet = Result.intersectFirstQiZiSetEx(r1, r2);
				if(isSet.size()!=0)
					isNO += isSet.size();
			}
		}
		
		return isNO;
	}
	private int getC3L2Intersection(TypeMap tMap){		
		HashSet<Integer> c3Set = tMap.getCandidates(VirtualWuZiBoard.CHONG3);
		HashSet<Integer> l2Set = tMap.getCandidates(VirtualWuZiBoard.LIVE2);
		
		HashSet<Integer> iSet1 = Result.intersection(c3Set, l2Set);
		
		return iSet1.size();
	}
}
