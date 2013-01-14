package oms.cj.WuZiLogic;

import java.util.ArrayList;
import java.util.HashSet;
import android.util.Log;
import oms.cj.WuZiWay.EmptyTypeMap;
import oms.cj.WuZiWay.Way;

@SuppressWarnings("unused")
public class Way2ToLC implements ILiveOrChong {
	private final static String TAG = "Way2ToLC";
	
	//out: ret[0] = INVALID, LIVE, CHONG
	//     ret[1] = 1,2,3,4,5
	//if ret[0]==INVALID, ret[1] is meaningless
	@Override
	public Result getLiveOrChong(VirtualWuZiBoard b, int[] pos, int dir) {
		Result r = new Result(pos, dir, b);
		r.setAttr(VirtualWuZiBoard.INVALID);
		
		int qiZiType = b.getQiZiType(pos);
		if(qiZiType==VirtualWuZiBoard.EMPTY){
			String out = String.format("(%s, %s) is empty", Way.posString(pos), VirtualWuZiBoard.dirStrList[dir]);
			throw new IllegalStateException(out);
		}
		
		Params p = new Params();
		getLiveOrChong(b, qiZiType, pos, dir, false, p, r, null);
		
		return r;
	}

	public void getLiveOrChong(VirtualWuZiBoard b, int qiZiType, int[] pos, int dir, boolean bTwoCandidates,
			Params p, Result r, EmptyTypeMap tMap){
		int[] startP = new int[2];
		int[] endP = new int[2];
		int distance = b.getDistance(qiZiType, pos, dir, startP, endP);
		
		if(distance==6){
			setResult(b, pos, dir, bTwoCandidates, p, r, tMap);
		} else if(distance<6){
			boolean bTwoSidesEmpty = false;
			ArrayList<int[]> candidates = new ArrayList<int[]>();
			if(b.inBox(startP)&&b.getQiZiType(startP)==VirtualWuZiBoard.EMPTY)
				candidates.add(startP);
			if(b.inBox(endP)&&b.getQiZiType(endP)==VirtualWuZiBoard.EMPTY)
				candidates.add(endP);
			if(candidates.size()==2)
				bTwoSidesEmpty = true;
			for(int i=0;i<candidates.size();i++){
				int[] elementPos = candidates.get(i);
				b.set(elementPos, qiZiType);
				int idx = b.twoDim2oneDim(elementPos);
				p.steps.add(idx);
				p.distanceList.add(distance);
				getLiveOrChong(b, qiZiType, pos, dir, bTwoSidesEmpty, p, r, tMap);
				p.distanceList.remove(new Integer(distance));
				p.steps.remove(new Integer(idx));
				b.set(elementPos, VirtualWuZiBoard.EMPTY);
			}
		} else if(distance>6){
			if(qiZiType==VirtualWuZiBoard.WHITE ||
					(qiZiType==VirtualWuZiBoard.BLACK&&!b.getConfig().mChangLian)){
				setResult(b, pos, dir, bTwoCandidates, p, r, tMap); 		
			}
		}		
	}
	
	//in: pos, dir, bTwoCandidates
	//in,out: p
	//out: r, tMap
	private void setResult(VirtualWuZiBoard b, int[] pos, int dir, boolean bTwoCandidates, 
			Params p, Result r, EmptyTypeMap tMap){
		//calculate ret0
		int ret0;
		int size = p.distanceList.size();
		if(size>=1){
			int previousDistance = p.distanceList.get(size-1);
			if(previousDistance==5) {
				if(bTwoCandidates){
					ret0 = VirtualWuZiBoard.LIVE;
				}else
					ret0 = VirtualWuZiBoard.CHONG;
			} else {
				ret0 = VirtualWuZiBoard.CHONG;
			}
		} else {	//chong5
			ret0 = VirtualWuZiBoard.CHONG;
		}
		
		//calculate ret1
		int ret1 = 5 - p.steps.size();
		int level = r.getLevel();
		
		//calculate firstQiZiSet and firstQiZiSetEx
		HashSet<Integer> firstQiZiSetEx = new HashSet<Integer>();
		if(ret0==VirtualWuZiBoard.LIVE)
			b.handleEx4Live(pos, dir, firstQiZiSetEx, p.steps);
		else
			b.handleEx4Chong(pos, dir, firstQiZiSetEx, p.steps);
		HashSet<Integer> firstQiZiSet = new HashSet<Integer>();
		if(p.steps.size()>0)
			firstQiZiSet.add(p.steps.get(0));
		
		//make a copy of steps
		HashSet<Integer> copy = new HashSet<Integer>(p.steps);
		
		//set the Result r
		if(r.getSteps()==null||level<ret1){	//first time to set r
			r.set(ret0, ret1, copy, firstQiZiSet, firstQiZiSetEx);
		} else {
			if(level==ret1){
				int attr = r.getAttr();
				if((ret0==VirtualWuZiBoard.LIVE&&attr==VirtualWuZiBoard.LIVE) || 
						(ret0==VirtualWuZiBoard.CHONG&&attr==VirtualWuZiBoard.CHONG)){
					//add firstQiZiSet, firstQiZiSetEx
					HashSet<Integer> firstQiZiSet2 = r.getFirstQiZiSet();
					firstQiZiSet2.addAll(firstQiZiSet);
					HashSet<Integer> firstQiZiSetEx2 = r.getFirstQiZiSetEx();
					firstQiZiSetEx2.addAll(firstQiZiSetEx);
					r.set(ret0, ret1, copy, firstQiZiSet2, firstQiZiSetEx2);
				} else if(ret0==VirtualWuZiBoard.LIVE&&attr==VirtualWuZiBoard.CHONG){
					//override before
					r.set(ret0, ret1, copy, firstQiZiSet, firstQiZiSetEx);
				} else if(ret0==VirtualWuZiBoard.CHONG&&attr==VirtualWuZiBoard.LIVE){
					//nothing done
				} 
			}
		}	
		
		//calculate tMap for empty positions related with (pos, dir)
		if(tMap!=null){
			if(ret0==VirtualWuZiBoard.CHONG){	//handle chong: CHONG[1-5]
				for(int i=0;i<p.steps.size();i++){
					int idx = p.steps.get(i);
					int origRType = tMap.getEmptyPositionsType(idx, dir);
					int newType = VirtualWuZiBoard.getType(ret0, ret1+1);	//can only be CHONG[1-4]
					if(origRType==EmptyTypeMap.INVALID)
						tMap.setEmptyPositionsType(idx, dir, newType);
					else {
						int origIdx = VirtualWuZiBoard.priorityList.indexOf(origRType);
						int newIdx = VirtualWuZiBoard.priorityList.indexOf(newType);
						if(origIdx>newIdx)
							tMap.setEmptyPositionsType(idx, dir, newType);
					}
				}
			} else {	//handle live: LIVE[1-4]
				//LIVE1: LIVE2, CHONG2
				//LIVE2: LIVE3, CHONG3
				//LIVE3: LIVE4, CHONG4
				//LIVE4: CHONG5
			}
			
		}
	}
}
