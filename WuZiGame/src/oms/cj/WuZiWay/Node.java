package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import oms.cj.WuZiLogic.IGenerateTypeMap;
import oms.cj.WuZiLogic.Result;
import oms.cj.WuZiLogic.VirtualWuZiBoard;
import android.util.Log;

public class Node implements IGameTree{
	private final static String TAG = "Node";
	
	public final static int MAXLAYER = 0;
	public final static int MINLAYER = 1;
	
	protected Way4 mWay4;
	protected VirtualWuZiBoard mBoard;	//board
	protected int mScore;					//evaluated score
	protected int mWhoMakeChoice;				//who made choice
	private int[] mTriedPos=null;	//which position is tried
	
	//sons
	protected ArrayList<Node> mNodeList;
	private int mIndex=-1;
	
	Node(Way4 way4, VirtualWuZiBoard board, int type){
		mWay4 = way4;
		
		mBoard = board;
		mScore = 0;
		mWhoMakeChoice = type;
					
		mNodeList = new ArrayList<Node>();
	}
	
    @Override
	public HashSet<Integer> getCandidatePosition(VirtualWuZiBoard b, 
			TypeMap myTypeMap, TypeMap hisTypeMap){
		int step = -1;
		
		HashSet<Integer> set=new HashSet<Integer>();
		
		//retrieve set in below order:
		//1. (LIVE4,CHONG4)(my)->(LIVE4,CHONG4)(his)->LIVE3(my)
		//2. SiSi->SiSan->->LIVE3(his)->SanSan
		//3. max intersections position considering (CHONG3,LIVE2)(my, his), (CHONG2,LIVE1)(my,his), CHONG1(my,his)
		//4. all positions around both my and his qizis 

		//Step0:
		if(set.size()==0){
			step = 0;
			set = getMustStep0(myTypeMap, hisTypeMap);
		}
		
		//Step1:
		if(set.size()==0){
			//coming here, it means that there is no my Valid LIVE3
			step = 1;
			set = getMustStep1(myTypeMap, hisTypeMap);
		}
				
		//Step2:
		//consider single CHONG3|LIVE2 in this sequence.
		//my CHONG3 and LIVE2
		if(set.size()==0){
			step = 2;
			set = getMaximumWithSingle(myTypeMap, hisTypeMap);
		}
		
		//Step3:
		if(set.size()==0){
			step = 3;
			set = Way.getCandidateEmptyPos(mBoard);
		}
		
		mWay4.incStepCounter(step);
		
		return set;
	}
	
	@Override
	public Node createSubNode(VirtualWuZiBoard b){
		Node n = new Node(mWay4, b, VirtualWuZiBoard.reverse(mWhoMakeChoice));
		return n;
	}
	
	@Override
	public int evaluate(int depth, int alpha, int beta){
		int layerType = getLayerType();
		int count = 0;
		int myType =  mBoard.getMyQiZiType();
		int hisType = mBoard.getHisQiZiType();
		IGenerateTypeMap mWayGen = mWay4.getIOfGenTypeMap();
		TypeMap tMap1 = mWayGen.generateTypeMap(mBoard, myType, mWay4.getInvalidMatrix(), Globals.TypeScore);
		TypeMap tMap2 = mWayGen.generateTypeMap(mBoard, hisType, mWay4.getInvalidMatrix(), Globals.TypeScore);
		
		//2 leaf node scenarios 
		if(tMap1.getMinSteps2Be5()==5){
			setScore(Integer.MAX_VALUE);
			return 1;
		} else if(tMap2.getMinSteps2Be5()==5){
			setScore(Integer.MIN_VALUE);
			return 1;
		}
		//another leaf node scenarios
		if(depth==0){
			IEvaluate eWay = new EWay1();
			int score1 = eWay.evaluate(tMap1);
			int score2 = eWay.evaluate(tMap2);
			setScore(score1-score2);
			return 1;
		}
		
		HashSet<Integer> set;
		if(mWhoMakeChoice==myType)
			set = getCandidatePosition(mBoard, tMap1, tMap2);
		else
			set = getCandidatePosition(mBoard, tMap2, tMap1);
		String out = String.format("level=%d, candidates=%d, set=%s", 
				Way4.Depth-depth, set.size(), mBoard.toString(set));
		Log.i(TAG+".Node.evaluate", out);
		/*
		Log.i(TAG+".evaluate", tMap1.toString());
		Log.i(TAG+".evaluate", tMap2.toString());
		*/
		int subnodeDepth;
		if(set.size()==1)
			subnodeDepth = 0;
		else
			subnodeDepth = depth - 1;
		
		int max=Integer.MIN_VALUE, min=Integer.MAX_VALUE;
		Iterator<Integer> itr = set.iterator();
		while(itr.hasNext()){
			Integer idx = itr.next();
			int[] pos = mBoard.oneDim2twoDim(idx);
			VirtualWuZiBoard b = new VirtualWuZiBoard(mBoard);
			b.set(pos, mWhoMakeChoice);
	
			Node n = createSubNode(b);
			
			mNodeList.add(n);
			n.setTriedPos(pos);
			count += n.evaluate(subnodeDepth, alpha, beta);
			
			int score=n.getScore();				
			switch(layerType){
			case MAXLAYER:
				if(score>=max){
					max = score;
					setIndex(mNodeList.size()-1);
					setScore(score);
				}
				break;
			case MINLAYER:
				if(score<=min){
					min = score;
					setIndex(mNodeList.size()-1);
					setScore(score);
				}
				break;
			}
			
			//do alpha-beta prune
			if(getLayerType()==MINLAYER){
				if(score<beta)
					beta = score;
				if(score<alpha)
					break;
			} else {
				if(score>alpha)
					alpha = score;
				if(score>beta)
					break;
			}
		}
		
		return count+1;
	}

	class SimpleSiSiPosition implements IPosition{

		@Override
		public HashSet<Integer> getPosition(TypeMap mapInfo, int qiZiType) {
			HashSet<Integer> sisiSet = new HashSet<Integer>();
			
			if(qiZiType==VirtualWuZiBoard.WHITE ||
					(qiZiType==VirtualWuZiBoard.BLACK&&!mBoard.getConfig().mSiSi)){
				sisiSet = mapInfo.getSimpleSiSi();
			}

			return sisiSet;
		}
		
	}
	
	class SiSanPosition implements IPosition{

		@Override
		public HashSet<Integer> getPosition(TypeMap mapInfo, int qiZiType) {				
			HashSet<Integer> sisanSet = mapInfo.getSiSan();

			return sisanSet;
		}
		
	}

	class SanSanPosition implements IPosition{

		@Override
		public HashSet<Integer> getPosition(TypeMap mapInfo, int qiZiType) {
			HashSet<Integer> sansanSet = new HashSet<Integer>();
			
			if(qiZiType==VirtualWuZiBoard.WHITE ||
					(qiZiType==VirtualWuZiBoard.BLACK&&!mBoard.getConfig().mSanSan)){
				sansanSet = mapInfo.getSanSan();
			}				
			
			return sansanSet;
		}
		
	}

	class HisLive3Position implements IPosition {

		@Override
		public HashSet<Integer> getPosition(TypeMap mapInfo, int qiZiType) {				
			HashSet<Integer> set = mapInfo.getCandidates(VirtualWuZiBoard.LIVE3);
			
			return set;
		}
		
	}

	public class L4C4L3Map {
		TypeMap mapInfo;
		int type;
		
		L4C4L3Map(TypeMap mapInfo, int type){
			this.mapInfo = mapInfo;
			this.type = type;
		}
	}
	public class D443D3Map {
		TypeMap mapInfo;
		IPosition position;
		int qiZiType;
		
		D443D3Map(TypeMap mapInfo, IPosition position, int qiZiType){
			this.mapInfo = mapInfo;
			this.position = position;
			this.qiZiType = qiZiType;
		}
	}

	protected IPosition d4Position = new SimpleSiSiPosition();
	protected IPosition sisanPosition = new SiSanPosition();
	protected IPosition hisl3Position = new HisLive3Position();
	protected IPosition d3Position = new SanSanPosition();
	public HashSet<Integer> getMustStep1(TypeMap myTypeMap, TypeMap hisTypeMap, D443D3Map[] d443d3maps){
		HashSet<Integer> set = new HashSet<Integer>();

		HashSet<Integer> hisD4Set = hisTypeMap.getSimpleSiSi();
		HashSet<Integer> hisD3Set = hisTypeMap.getSanSan();
		HashSet<Integer> myD3Set = myTypeMap.getSanSan();
		
		for(int i=0;i<d443d3maps.length;i++){
			D443D3Map d443d3map = d443d3maps[i];
			TypeMap info = d443d3map.mapInfo;
			IPosition position = d443d3map.position;
			int qiZiType = d443d3map.qiZiType;
			HashSet<Integer> retSet = position.getPosition(info, qiZiType);
			set.addAll(retSet);
			//remove the double3 positions when my type is black and sansan is checked;
			if(position==sisanPosition&&info==myTypeMap&&
					mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSanSan)
				set.removeAll(myD3Set);
			//remove the double4 positions when his type is black and sisi is checked; we needn't block such positions
			if(position==hisl3Position&&
					mWhoMakeChoice==VirtualWuZiBoard.WHITE&&mBoard.getConfig().mSiSi)
				set.removeAll(hisD4Set);
			//remove the double3 positions when his type is black and sansan is checked; we needn't block such positions
			if(position==d3Position&&info==hisTypeMap&&
					mWhoMakeChoice==VirtualWuZiBoard.WHITE&&mBoard.getConfig().mSanSan)
				set.removeAll(hisD3Set);
			if(set.size()!=0)
				break;
		}

		return set;
	}
	
	public HashSet<Integer> getMustStep1(TypeMap myTypeMap, TypeMap hisTypeMap){
		HashSet<Integer> set = new HashSet<Integer>();
		
		//if there are SiSan, valid double4/3, only they should be considered in d443d3maps sequence:
		D443D3Map[] d443d3maps = {
				new D443D3Map(myTypeMap, d4Position, mWhoMakeChoice),	//
				new D443D3Map(myTypeMap, sisanPosition, mWhoMakeChoice),
				new D443D3Map(hisTypeMap, d4Position, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
				new D443D3Map(hisTypeMap, sisanPosition, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
				new D443D3Map(hisTypeMap, hisl3Position, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
				new D443D3Map(myTypeMap, d3Position, mWhoMakeChoice),
				new D443D3Map(hisTypeMap, d3Position, VirtualWuZiBoard.reverse(mWhoMakeChoice)),
		};
		
		set = getMustStep1(myTypeMap, hisTypeMap, d443d3maps);
		
		return set;
	}
	
	public HashSet<Integer> getMustStep0(TypeMap myTypeMap, TypeMap hisTypeMap){
		HashSet<Integer> set = new HashSet<Integer>();
		HashSet<Integer> myD4Set = myTypeMap.getSimpleSiSi();
		
		L4C4L3Map[] l4c4l3maps = {
				new L4C4L3Map(myTypeMap, VirtualWuZiBoard.LIVE4),
				new L4C4L3Map(myTypeMap, VirtualWuZiBoard.CHONG4),
				new L4C4L3Map(hisTypeMap, VirtualWuZiBoard.LIVE4),
				new L4C4L3Map(hisTypeMap, VirtualWuZiBoard.CHONG4),
				new L4C4L3Map(myTypeMap, VirtualWuZiBoard.LIVE3),
		};

		for(int i=0;i<l4c4l3maps.length;i++){
			L4C4L3Map l4c4l3map = l4c4l3maps[i];
			TypeMap info = l4c4l3map.mapInfo;
			int type = l4c4l3map.type;
			set = info.getCandidates(type);
			if(type==VirtualWuZiBoard.LIVE3&&
					mWhoMakeChoice==VirtualWuZiBoard.BLACK&&mBoard.getConfig().mSiSi)
				set.removeAll(myD4Set);
			if(set.size()!=0)
				break;
		}

		return set;
	}
	
	protected TypeMap[] decideTypeMaps(TypeMap myTypeMap, TypeMap hisTypeMap){
    	TypeMap[] tMaps0 = {myTypeMap};				//attack
    	TypeMap[] tMaps2 = {myTypeMap, hisTypeMap};	//balance
    	TypeMap[] tMaps;
    	
    	int size1 = getNOOfC3L2(myTypeMap);
    	int size2 = getNOOfC3L2(hisTypeMap);
    	if(size1-size2>=1)
    		tMaps = tMaps0;
    	else
    		tMaps = tMaps2;

    	return tMaps;
	}
	
	public HashSet<Integer> getMaximumWithSingle(TypeMap myTypeMap, TypeMap hisTypeMap){
		HashSet<Integer> set = new HashSet<Integer>();
		
		HashSet<Integer> myD3Set = myTypeMap.getSanSan();
		HashSet<Integer> myD4Set = myTypeMap.getSimpleSiSi();

    	TypeMap[] tMaps = decideTypeMaps(myTypeMap, hisTypeMap);
    	
		set = getCandidatesPer(Globals.mRTypes, Globals.rTypeScore, tMaps, mWhoMakeChoice, myD3Set, myD4Set);

		return set;
	}
		
	public void putElementToScoreMap(int idx, int score, HashMap<Integer, Integer> scoreMaps){
		if(scoreMaps.containsKey(idx)){
			int score1 = scoreMaps.get(idx);
			score1 += score;
			scoreMaps.put(idx, score1);
		} else {
			scoreMaps.put(idx, score);
		}
	}
	
	public int getNOOfC3L2(TypeMap tMap){
		int NO = 0;
		
		HashSet<Integer> c3 = tMap.getCandidates(VirtualWuZiBoard.CHONG3);
		HashSet<Integer> l2 = tMap.getCandidates(VirtualWuZiBoard.LIVE2);
		
		NO = c3.size()+l2.size();
		
		return NO;
	}

	public void getMaxScoreSet(HashMap<Integer, Integer> scoreMaps, HashSet<Integer> sets){
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
	
    private HashSet<Integer> getCandidatesPer(int[] rTypes, int[] rTypeScore, TypeMap[] tMaps, 
    		int mWhoMakeChoice, HashSet<Integer> myD3Set, HashSet<Integer> myD4Set){
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
    	
    	getMaxScoreSet(scoreMaps, sets);
    	
    	return sets;
    }
    
	public int getLayerType(){
		int type;
		if(mWhoMakeChoice==mBoard.getMyQiZiType())
			type = MAXLAYER;
		else if(mWhoMakeChoice==mBoard.getHisQiZiType())
			type = MINLAYER;
		else {
			String out = String.format("invalid mWhoMakeChoice=%s", mWhoMakeChoice);
			throw new IllegalStateException(out);
		}
		
		return type;
	}		
	
	public void print(){
		String out="";
		for(int i=0;i<mNodeList.size();i++){
			Node n = mNodeList.get(i);
			out += n.getScore() + " ";
		}
		Log.i(TAG+".Node.print", out);
	}
	
	public void setIndex(int idx){
		mIndex = idx;
	}
	public int getIndex(){
		return mIndex;
	}
	protected void setTriedPos(int[] pos){
		mTriedPos = pos;
	}
	private int[] getTriedPos(){
		return mTriedPos;
	}
	public int getScore(){
		return mScore;
	}
	public void setScore(int score){
		mScore = score;
	}
			
	public int[] getCalculatedPos(int idx){
		return mNodeList.get(idx).getTriedPos();
	}
}
