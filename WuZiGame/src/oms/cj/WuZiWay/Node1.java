package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import android.util.Log;
import oms.cj.WuZiLogic.Result;
import oms.cj.WuZiLogic.VirtualWuZiBoard;



public class Node1 extends Node {
	private final static String TAG = "Node1";
	
	private WayToGetMustStep mWayT1, mWayT2;
	
	Node1(Way4 way4, VirtualWuZiBoard board, int type) {
		super(way4, board, type);
		
		mWayT1 = new Way2T1ToGetMustStep();
		mWayT2 = new Way2T2ToGetMustStep();
	}

	private final static int SET1IDX = 0;
	private final static int SET2IDX = 1;
	private final static int SET3IDX = 2;
	private final int[][] MustStep3MapNT1 = {
		{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.CHONG2},
		{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.CHONG2},
		{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.CHONG2},
	};
	private final int[][] MustStep3MapNT2 = {
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.LIVE1},
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.LIVE1},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.LIVE1},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.CHONG2},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.LIVE1},
		};
	
	private HashSet<Integer> getTwoSteps2WinNext(TypeMap myTypeMap, int[][] MustStep3MapN){
		HashSet<Integer> set = new HashSet<Integer>();
		
		for(int i=0;i<MustStep3MapN.length;i++){
			int rType1 = MustStep3MapN[i][SET1IDX];
			int rType2 = MustStep3MapN[i][SET2IDX];
			int rType3 = MustStep3MapN[i][SET3IDX];
			ArrayList<Result> rList1 = myTypeMap.getMaps(rType1);
			ArrayList<Result> rList2 = myTypeMap.getMaps(rType2);
			
			//consider JinShou here
			if(rType1==VirtualWuZiBoard.CHONG3&&rType2==VirtualWuZiBoard.CHONG2){
				if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSiSi)	//do not consider Double4 for black
					continue;
			}
			if(rType1==VirtualWuZiBoard.LIVE2&&rType2==VirtualWuZiBoard.LIVE1){
				if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSanSan)	//do not consider Double3 for black
					continue;
			}
			
			for(int j=0;j<rList1.size();j++){
				Result r1 = rList1.get(j);
				for(int k=0;k<rList2.size();k++){
					Result r2 = rList2.get(k); 
					HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2);
					if(is.size()!=0){
						HashSet<Integer> others = r2.getFirstQiZiSetEx();
						others.removeAll(is);
						int r2Dir = r2.getDir();
						getCandidates(myTypeMap, others, r2Dir, rType3, set);
					}
				}
			}
		}
		
		return set;
	}
	
	private void getCandidates(TypeMap tMap, HashSet<Integer> others, int dir, int rType, HashSet<Integer> candidates){
		HashSet<Integer> tSet = new HashSet<Integer>();
		
		Iterator<Integer> itr = others.iterator();
		while(itr.hasNext()){
			int idx = itr.next();

			tSet.clear();
			boolean bFound1 = tMap.searchIn(mBoard, idx, dir, rType, tSet);
			if(bFound1)
				candidates.addAll(tSet);
		}	
	}

	public HashSet<Integer> getMustStep1T1(TypeMap myTypeMap, TypeMap hisTypeMap){
		HashSet<Integer> set = new HashSet<Integer>();
		
		//if there are SiSan, valid double4/3, only they should be considered in d443d3maps sequence:
		D443D3Map[] d443d3maps = {
				new D443D3Map(myTypeMap, d4Position, mWhoMakeChoice),	
				new D443D3Map(myTypeMap, sisanPosition, mWhoMakeChoice),
				new D443D3Map(hisTypeMap, d4Position, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
				new D443D3Map(hisTypeMap, sisanPosition, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
		};
		
		set = getMustStep1(myTypeMap, hisTypeMap, d443d3maps);
		
		return set;
	}

	public HashSet<Integer> getMustStep1T2(TypeMap myTypeMap, TypeMap hisTypeMap){
		HashSet<Integer> set = new HashSet<Integer>();
		
		//if there are SiSan, valid double4/3, only they should be considered in d443d3maps sequence:
		D443D3Map[] d443d3maps = {
				new D443D3Map(hisTypeMap, hisl3Position, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
		};
		
		set = getMustStep1(myTypeMap, hisTypeMap, d443d3maps);
		
		return set;
	}

	public HashSet<Integer> getMustStep1T3(TypeMap myTypeMap, TypeMap hisTypeMap){
		HashSet<Integer> set = new HashSet<Integer>();
		
		//if there are SiSan, valid double4/3, only they should be considered in d443d3maps sequence:
		D443D3Map[] d443d3maps = {
				new D443D3Map(myTypeMap, d3Position, mWhoMakeChoice),
				new D443D3Map(hisTypeMap, d3Position, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
		};
		
		set = getMustStep1(myTypeMap, hisTypeMap, d443d3maps);
		
		return set;
	}

	@Override
	public HashSet<Integer> getCandidatePosition(VirtualWuZiBoard b, 
			TypeMap myTypeMap, TypeMap hisTypeMap){
		int step = -1;
		int subStep = 0;
		HashSet<Integer> set=new HashSet<Integer>();
				
		//Step0:
		//priority:
		//1. my l4, my c4
		//2. his l4, his c4
		//3. my l3
		if(set.size()==0){
			step = 0;
			set = getMustStep0(myTypeMap, hisTypeMap);
		}
		
		//Step1:
		//4. my double c4(level 1)
		//5. my c4l3(level 1)
		//6. his double c4(level 1)
		//7. his c4l3(level 1)
		if(set.size()==0){
			//coming here, it means that there is no my Valid LIVE3
			step = 1;
			set = getMustStep1T1(myTypeMap, hisTypeMap);
		}
		
		//Step2:
		if(set.size()==0){
			step = 2;
			//8. my c3c2c3, that is my double c4(level 2)
			//9. my c3l1c3, that is my c4l3(level 2)
			//10. my l2c2c3, that is my c4l3(level 2)
			set = mWayT1.getMustStep(myTypeMap);
			
			//11. his l3
			if(set.size()==0)
				set = getMustStep1T2(myTypeMap, hisTypeMap);
			
			//12. his c3c2c3
			//13. his c3l1c3
			//14. his l2c2c3
			if(set.size()==0)
				set = mWayT1.getMustStep(hisTypeMap);
		}
		
		//Step3:
		//15. my double l3
		//16. his double l3
		if(set.size()==0){
			step = 3;
			set = getMustStep1T3(myTypeMap, hisTypeMap);
		}

		//Step4:
		if(set.size()==0){
			step = 4;
			//17. my c3c2l2
			//18. my c3l1l2
			//19. my l2c2l2
			//20. my l2l1c3
			//21. my l2l1l2
			set = mWayT2.getMustStep(myTypeMap);
			
			//22. his c3c2l2
			//23. his c3l1l2
			//24. his l2c2l2
			//25. his l2l1c3
			//26. his l2l1l2
			if(set.size()==0)
				set = mWayT2.getMustStep(hisTypeMap);
		}

		//Step5:
		if(set.size()==0){
			step = 5;
			set = getTwoSteps2WinNext(myTypeMap, MustStep3MapNT1);
			if(set.size()==0)
				set = getTwoSteps2WinNext(myTypeMap, MustStep3MapNT2);
		}
		
		//Step6:
		//consider single CHONG3|LIVE2 in this sequence.
		//my CHONG3 and LIVE2
		if(set.size()==0){
			step = 6;
			set = getMaximumWithSingle(myTypeMap, hisTypeMap);
		} else if(step==5&&set.size()!=0){
			subStep = 1;
			filterSetInStep5(myTypeMap, hisTypeMap, set);
		}
		
		//Step6:
		if(set.size()==0){
			step = 7;
			set = Way.getCandidateEmptyPos(mBoard);
		}
		
		mWay4.incStepCounter(step);
		Log.i(TAG+".getCandidatePosition", "step.subStep="+step+"."+subStep);
		
		return set;
	}
	
	public HashSet<Integer> filterSetInStep5(TypeMap myTypeMap, TypeMap hisTypeMap, HashSet<Integer> filterSet){
		HashSet<Integer> set = new HashSet<Integer>();
		
		HashSet<Integer> myD3Set = myTypeMap.getSanSan();
		HashSet<Integer> myD4Set = myTypeMap.getSimpleSiSi();
    	
		TypeMap[] tMaps = decideTypeMaps(myTypeMap, hisTypeMap);
    	
		set = getCandidatesPer(Globals.mRTypes, Globals.rTypeScore, tMaps, mWhoMakeChoice, 
				myD3Set, myD4Set, filterSet);

		return set;
	}
	
    private HashSet<Integer> getCandidatesPer(int[] rTypes, int[] rTypeScore, TypeMap[] tMaps, 
    		int mWhoMakeChoice, HashSet<Integer> myD3Set, HashSet<Integer> myD4Set, HashSet<Integer> filterSet){
    	HashSet<Integer> sets = new HashSet<Integer>();
    	
    	HashMap<Integer, Integer> scoreMaps = new HashMap<Integer, Integer>();
    	for(int i=0;i<rTypes.length;i++){
    		int rType = rTypes[i];
    		int score = rTypeScore[i];
    		for(int k=0;k<tMaps.length;k++){
    			TypeMap tMap = tMaps[k];
	    		ArrayList<Result> rList = tMap.getMaps(rType);	
	    		for(int j=0;j<rList.size();j++){
	    			Result r = rList.get(j);
	    			HashSet<Integer> exSets;
	    			if(tMap.C4L3C3L2C2Empty()&&rType==VirtualWuZiBoard.LIVE1)
	    				exSets = r.getFirstQiZiSet();
	    			else
	    				exSets = r.getFirstQiZiSetEx();
	    			Iterator<Integer> itr = exSets.iterator();
	    			while(itr.hasNext()){
	    				int idx = itr.next();
	    				if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSanSan&&myD3Set.contains(idx))
	    					continue;
	    				if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSiSi&&myD4Set.contains(idx))
	    					continue;	    				
	    				putElementToScoreMap(idx, score, scoreMaps);
	    			}
	    		}	    			
    		}
    	}
    	
    	getMaxScoreSet(scoreMaps, sets, filterSet);
    	
    	return sets;
    }

	public void getMaxScoreSet(HashMap<Integer, Integer> scoreMaps, 
			HashSet<Integer> sets, HashSet<Integer> filterSet){
    	//decide sets of idx from scoreMaps
    	int max = Integer.MIN_VALUE;
    	Set<Integer> keys = scoreMaps.keySet();
    	/*
    	String out = String.format("keys=%s", mV.toString(keys));
    	Log.i(TAG+".getCandidates", out);
    	*/
    	Iterator<Integer> itr = keys.iterator();
    	while(itr.hasNext()){
    		int key = itr.next();
    		if(!filterSet.contains(key))
    			continue;
    		
    		int value = scoreMaps.get(key);
    		if(value>max){
    			sets.clear();
    			sets.add(key);
    			max=value;
    		} else if(value==max){
    			sets.add(key);
    		}
    	}
	}
	
	@Override
	public Node createSubNode(VirtualWuZiBoard b){
		Node n = new Node1(mWay4, b, VirtualWuZiBoard.reverse(mWhoMakeChoice));
		return n;
	}
	
	
	interface WayToGetMustStep {
		HashSet<Integer> getMustStep(TypeMap myTypeMap);
	}
	
	public class Way2T1ToGetMustStep extends Way2ToGetMustStep implements WayToGetMustStep {
		private final int[][] MustStepMap = {
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.CHONG3},
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.CHONG3},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.CHONG3},
		};

		@Override
		public HashSet<Integer> getMustStep(TypeMap myTypeMap) {
			return getMustStep(myTypeMap, MustStepMap);
		}
	}

	public class Way2T2ToGetMustStep extends Way2ToGetMustStep implements WayToGetMustStep {
		private final int[][] MustStepMap = {
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.LIVE2},
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.LIVE2},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.CHONG2, VirtualWuZiBoard.LIVE2},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.CHONG3},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.LIVE1, VirtualWuZiBoard.LIVE2},
		};

		@Override
		public HashSet<Integer> getMustStep(TypeMap myTypeMap) {
			return getMustStep(myTypeMap, MustStepMap);
		}
	}
	
	public class Way2ToGetMustStep {
		private HashSet<Integer> getMustStep(TypeMap myTypeMap, int rType1, int rType2, int rType3){
			HashSet<Integer> set = new HashSet<Integer>();

			ArrayList<Result> rList1 = myTypeMap.getMaps(rType1);
			ArrayList<Result> rList2 = myTypeMap.getMaps(rType2);
			
			//consider JinShou here
			if(rType1==VirtualWuZiBoard.CHONG3&&rType2==VirtualWuZiBoard.CHONG2){
				if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSiSi)	//do not consider Double4 for black
					return set;
			}
			if(rType1==VirtualWuZiBoard.LIVE2&&rType2==VirtualWuZiBoard.LIVE1){
				if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSanSan)	//do not consider Double3 for black
					return set;
			}
			
			for(int j=0;j<rList1.size();j++){
				Result r1 = rList1.get(j);
				for(int k=0;k<rList2.size();k++){
					Result r2 = rList2.get(k); 
					HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2);
					if(is.size()!=0){
						HashSet<Integer> others = r2.getFirstQiZiSetEx();
						others.removeAll(is);
						int r2Dir = r2.getDir();
						filterOthers(myTypeMap, others, r2Dir, rType3);
						set.addAll(others);
					}
				}
			}				

			return set;
		}

		public HashSet<Integer> getMustStep(TypeMap myTypeMap, int[][] MustStepMap){
			HashSet<Integer> set = new HashSet<Integer>();
			
			for(int i=0;i<MustStepMap.length;i++){
				int rType1 = MustStepMap[i][0];
				int rType2 = MustStepMap[i][1];
				int rType3 = MustStepMap[i][2];
				set = getMustStep(myTypeMap, rType1, rType2, rType3);
				if(set.size()!=0)
					break;
			}
			
			return set;
		}
		
		private void filterOthers(TypeMap tMap, HashSet<Integer> others, int dir, int rType){
			HashSet<Integer> selectSet = new HashSet<Integer>();

			Iterator<Integer> itr = others.iterator();
			while(itr.hasNext()){
				int idx = itr.next();
				boolean bFound = tMap.searchIn(idx, dir, rType, null);
				if(bFound)
					selectSet.add(idx);
			}

			others.clear();
			others.addAll(selectSet);
		}
	}
	
	public class Way1ToGetMustStep implements WayToGetMustStep {
		private final int SET1IDX = 0;
		private final int SET2IDX = 1;
		private final int SCOREIDX = 2;
		private final int[][] MustStep3Map = {
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.CHONG2, 15},
			{VirtualWuZiBoard.CHONG3, VirtualWuZiBoard.LIVE1, 10},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.CHONG2, 10},
			{VirtualWuZiBoard.LIVE2, VirtualWuZiBoard.LIVE1, 5},
		};
		
		public HashSet<Integer> getMustStep(TypeMap myTypeMap){
			HashSet<Integer> set = new HashSet<Integer>();
			HashMap<Integer, Integer> scoreMaps = new HashMap<Integer, Integer>();
			
			for(int i=0;i<MustStep3Map.length;i++){
				int rType1 = MustStep3Map[i][SET1IDX];
				int rType2 = MustStep3Map[i][SET2IDX];
				ArrayList<Result> rList1 = myTypeMap.getMaps(rType1);
				ArrayList<Result> rList2 = myTypeMap.getMaps(rType2);
				int score = MustStep3Map[i][SCOREIDX];
				
				//consider JinShou here
				if(rType1==VirtualWuZiBoard.CHONG3&&rType2==VirtualWuZiBoard.CHONG2){
					if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSiSi)	//do not consider Double4 for black
						continue;
				}
				if(rType1==VirtualWuZiBoard.LIVE2&&rType2==VirtualWuZiBoard.LIVE1){
					if(mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSanSan)	//do not consider Double3 for black
						continue;
				}
				
				for(int j=0;j<rList1.size();j++){
					Result r1 = rList1.get(j);
					for(int k=0;k<rList2.size();k++){
						Result r2 = rList2.get(k); 
						HashSet<Integer> is = Result.intersectFirstQiZiSetEx(r1, r2);
						if(is.size()!=0){
							HashSet<Integer> others = r2.getFirstQiZiSetEx();
							others.removeAll(is);
							int r2Dir = r2.getDir();
							filterOthers(myTypeMap, others, r2Dir);
							putSetToScoreMap(others, score, scoreMaps);						
						}
					}
				}				
			}
			
			getMaxScoreSet(scoreMaps, set);
			
			return set;
		}
		
		
		private void filterOthers(TypeMap tMap, HashSet<Integer> others, int dir){
			HashSet<Integer> removedSet = new HashSet<Integer>();
			
			Iterator<Integer> itr = others.iterator();
			while(itr.hasNext()){
				int idx = itr.next();
				boolean bFound1 = tMap.searchIn(idx, dir, VirtualWuZiBoard.CHONG3, null);
				boolean bFound2 = tMap.searchIn(idx, dir, VirtualWuZiBoard.LIVE2, null);
				if(!bFound1&&!bFound2)
					removedSet.add(idx);
			}
			
			others.removeAll(removedSet);
		}
		
		private void putSetToScoreMap(HashSet<Integer> is, int score, HashMap<Integer, Integer> scoreMaps){
			Iterator<Integer> itr = is.iterator();
			
			while(itr.hasNext()){
				Integer idx = itr.next();
				putElementToScoreMap(idx, score, scoreMaps);
			}
		}
	}
}
