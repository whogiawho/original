package oms.cj.WuZiLogic;

import java.util.ArrayList;
import java.util.HashSet;
import android.util.Log;
import oms.cj.WuZiWay.Way;

public class Way1ToLC implements ILiveOrChong {
	private final static String TAG = "Way1ToLC";
	
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
		
		ArrayList<ArrayList<Integer>> sets = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> steps = new ArrayList<Integer>();
		getMinSteps(b, qiZiType, pos, dir, sets, steps);
		if(sets.size()==0)	//return INVALID result
			return r;
		
		HashSet<Integer> firstQiZiSet = new HashSet<Integer>();
		HashSet<Integer> firstQiZiSetEx = new HashSet<Integer>();
		int min = Integer.MAX_VALUE;
		for(int i=0;i<sets.size();i++){
			steps = sets.get(i);
			int stepSize = steps.size();
			if(stepSize<=min){
				if(stepSize<min){		//means a new level should be considered, so discard previous set
					firstQiZiSet.clear();
					firstQiZiSetEx.clear();
				}
				
				//Log.i(TAG+".getLiveOrChong", boardToString());
				min = stepSize;
				fillToLC4(b, steps, qiZiType);
				//Log.i(TAG+".getLiveOrChong", boardToString());
				
				if(stepSize==0)//return CHONG5 for both CHONG5 and LIVE5
					r.set(VirtualWuZiBoard.CHONG, 5-min, new HashSet<Integer>(steps), firstQiZiSet, firstQiZiSetEx);
				else if(isLiveSiV2(b, qiZiType, pos, dir, null)){
					if(r.getAttr()==VirtualWuZiBoard.CHONG){
						firstQiZiSet.clear();
						firstQiZiSetEx.clear();
					}
					if(stepSize!=0)
						firstQiZiSet.add(steps.get(0));
					//special LIVE2 with adjacent QiZis
					b.handleEx4Live(pos, dir, firstQiZiSetEx, steps);
					
					r.set(VirtualWuZiBoard.LIVE, 5-min, new HashSet<Integer>(steps), firstQiZiSet, firstQiZiSetEx);
				} else {
					int sisiCnt = b.getChongSiCount(qiZiType, pos, dir, null, null);
					sisiCntExceptionCheck(b, sisiCnt, i, pos, dir, steps.size(), sets.size());
					
					if(qiZiType==VirtualWuZiBoard.WHITE||
							(qiZiType==VirtualWuZiBoard.BLACK&&!b.getConfig().mSiSi)||
							(qiZiType==VirtualWuZiBoard.BLACK&&b.getConfig().mSiSi&&sisiCnt==1)){
						if(r.getAttr()==VirtualWuZiBoard.LIVE&&r.getLevel()==5-min){
							//we should not override a previous Live(5-min)
						} else {
							if(stepSize!=0)
								firstQiZiSet.add(steps.get(0));
							b.handleEx4Chong(pos, dir, firstQiZiSetEx, steps);
							r.set(VirtualWuZiBoard.CHONG, 5-min, new HashSet<Integer>(steps), 
									firstQiZiSet, firstQiZiSetEx);
						}
					}
				}
				
				restoreFromLC4(b, steps);
			}
		}
		
		return r;
	}

	//in: b, qiZiType, pos, dir
	//in, out: steps
	//out: sets
	//1. if there already exists a CHENG5, then sets contains an EMPTY one, which means sets.size()=1
	//2. if (pos, dir) is INVALID, sets.size=0
	//3. otherwise sets contains all possible kinds of steps to be CHENG5
	public void getMinSteps(VirtualWuZiBoard b, int qiZiType, int[] pos, int dir, 
			ArrayList<ArrayList<Integer>> sets, ArrayList<Integer> steps){
		int[] startP = new int[2];
		int[] endP = new int[2];
		int distance = b.getDistance(qiZiType, pos, dir, startP, endP);

		if(distance==6){
			ArrayList<Integer> copy = new ArrayList<Integer>(steps);
			sets.add(copy); 
		} else if(distance<6){
			ArrayList<int[]> candidates = new ArrayList<int[]>();
			if(b.inBox(startP)&&b.getQiZiType(startP)==VirtualWuZiBoard.EMPTY)
				candidates.add(startP);
			if(b.inBox(endP)&&b.getQiZiType(endP)==VirtualWuZiBoard.EMPTY)
				candidates.add(endP);
			for(int i=0;i<candidates.size();i++){
				int[] elementPos = candidates.get(i);
				b.set(elementPos, qiZiType);
				int idx = b.twoDim2oneDim(elementPos);
				steps.add(idx);
				getMinSteps(b, qiZiType, pos, dir, sets, steps);
				steps.remove(new Integer(idx));
				b.set(elementPos, VirtualWuZiBoard.EMPTY);
			}
		} else if(distance>6){
			if(qiZiType==VirtualWuZiBoard.WHITE ||
					(qiZiType==VirtualWuZiBoard.BLACK&&!b.getConfig().mChangLian)){
				ArrayList<Integer> copy = new ArrayList<Integer>(steps);
				sets.add(copy); 		
			}
		}
	}
	
	private void fillToLC4(VirtualWuZiBoard b, ArrayList<Integer> steps, int qiZiType){
		int stepSize = steps.size();
		
		for(int j=0;j<stepSize-1;j++){
			int idx = steps.get(j);
			int[] p = b.oneDim2twoDim(idx);
			b.set(p, qiZiType);
		}		
	}
	private void restoreFromLC4(VirtualWuZiBoard b, ArrayList<Integer> steps){
		int stepSize = steps.size();
		
		for(int j=0;j<stepSize-1;j++){
			int idx = steps.get(j);
			int[] p = b.oneDim2twoDim(idx);
			b.set(p, VirtualWuZiBoard.EMPTY);
		}
	}
	private void sisiCntExceptionCheck(VirtualWuZiBoard b, int sisiCnt, int i, int[] pos, int dir, int stepSize, int setSize){
		if(sisiCnt<=0){
			Log.i(TAG+".sisiCntExceptionCheck", b.boardToString());
			String out = String.format("invalid sisiCnt=%d, i=%d, pos=%s, dir=%d, steps.size=%d, sets.size=%d", 
					sisiCnt, i, Way.posString(pos), dir, stepSize, setSize);
			throw new IllegalStateException(out);
		}
	}
	
	//version 2 to decide whether an unempty pos is live 4
	public boolean isLiveSiV2(VirtualWuZiBoard b, int qiZiType, int[] pos, int dir, ArrayList<Integer> list){
		boolean bLiveSi = false;
		int[] startP = new int[2];
		int[] endP = new int[2];
		
		int distance = b.getDistance(qiZiType, pos, dir, startP, endP);
		int[] startPMinus = VirtualWuZiBoard.delta(startP, dir, -1);
		boolean b1 = b.preHandleChangLian(startPMinus, qiZiType);
		int[] endPPlus = VirtualWuZiBoard.delta(endP, dir, 1);
		boolean b2 = b.preHandleChangLian(endPPlus, qiZiType);
		if(distance==5 &&
				b.inBox(startP)&&b.getQiZiType(startP)==VirtualWuZiBoard.EMPTY&&b1 &&
				b.inBox(endP)&&b.getQiZiType(endP)==VirtualWuZiBoard.EMPTY&&b2){
			bLiveSi = true;
			if(list!=null){
				list.clear();
				
				for(int i=1;i<5;i++){
					int[] checkPos = VirtualWuZi.delta(startP, dir, i);
					list.add(new Integer(b.twoDim2oneDim(checkPos)));
				}
			}
		}

		return bLiveSi;
	}
}
