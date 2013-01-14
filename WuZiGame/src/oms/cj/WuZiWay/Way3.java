package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import android.os.Handler;
import android.util.Log;
import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.LogicConfig;
import oms.cj.WuZiLogic.VirtualWuZi;

public class Way3 extends Way implements ISuggest {
	private final static String TAG = "Way3";
	public final static int Live4=0;
	public final static int Chong4=1;
	public final static int Live3=2;
	public final static int Chong3=3;
	public final static int Live2=4;
	public final static int Chong2=5;
	public final static int XingCount=6;
	
	private int mStrategy;
	
	public Way3(VirtualWuZi v, Handler handler, int strategy) {
		super(v, handler);
		mStrategy = strategy;
	}

	private ArrayList<Integer> getCandidateEmptyPos(){
		int myType=mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		ArrayList<Integer> list1=getCandidateEmptyPos(myType);
		ArrayList<Integer> list2=getCandidateEmptyPos(hisType);
		
		for(int i=0;i<list2.size();i++){
			int element = list2.get(i);
			if(list1.indexOf(element)==-1){
				list1.add(element);
			}
		}
		return list1;
	}
	
	private void setZero(int[][] array){
		for(int j=0;j<2;j++)
			for(int i=0;i<array.length;i++)
				array[j][i]=0;
	}
	
	private HashMap<Integer, int[][]> initPosCountMap(ArrayList<Integer> posList){
		HashMap<Integer, int[][]> map=new HashMap<Integer, int[][]>();
		
		for(int i=0;i<posList.size();i++){
			int idx = posList.get(i);
			int[][] xings = new int[2][XingCount];
			setZero(xings);
			map.put(idx, xings);
		}
		return map;
	}

	private void startCount(int[][] xings, int type, int[] pos){
		mV.set(pos, type);
		for(int i=0;i<VirtualWuZi.LINEDIRTOTAL;i++){
			if(mV.isLiveSi(type, pos, i, null)){
					xings[type][0]++;continue;
			}
			int count=mV.getChongSiCount(type, pos, i, null, null);
			if(count>0){
				xings[type][1]+=count;continue;
			}
			if(mV.isLiveSan(type, pos, i, null, null)){
				xings[type][2]++;continue;
			}
			if(mV.isChongSan(type, pos, i)){
				xings[type][3]++;continue;
			}
			if(mV.isLive2(type, pos, i)){
				xings[type][4]++;continue;
			}
			if(mV.isChong2(type, pos, i)){
				xings[type][5]++;continue;
			}		
		}
		mV.set(pos, VirtualWuZi.EMPTY);
	}
	
	public final static int EQUAL=0;
	public final static int LESS=1;
	public final static int GREATER=2;
	private final static int[] comparePriority={
		Live4,
		Chong4,
		Live3,
		Chong3,
		Live2,
		Chong2
	};
	
	private int compareArray(int[] a1, int[]a2){
		int iCompareResult=EQUAL;
		
		//从高优先级开始
		for(int i=0;i<XingCount;i++){
			int whichXing = comparePriority[i];
			if(a1[whichXing]>a2[whichXing]){
				iCompareResult=GREATER;
				break;
			} else if(a1[whichXing]<a2[whichXing]){
				iCompareResult=LESS;
				break;
			} 
		}
		
		return iCompareResult;
	}
	
	private int[] getArray(int[][] xings, int strategy){
		int[] array = new int[XingCount];
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		

		if(strategy==LogicConfig.ADBALANCE){
			for(int i=0;i<array.length;i++){
				array[i] = xings[myType][i] + xings[hisType][i];
			}
		} else if(strategy==LogicConfig.DEFEND){
			for(int i=0;i<array.length;i++){
				array[i] = xings[hisType][i];
			}
		} else {	//default is ATTACK
			for(int i=0;i<array.length;i++){
				array[i] = xings[myType][i];
			}			
		}
		return array;
	}
	
	private ArrayList<Integer> sort(HashMap<Integer, int[][]> map, int strategy){
		ArrayList<Integer> candidateList = new ArrayList<Integer>();
		
		Iterator<Integer> itr = map.keySet().iterator();
		while(itr.hasNext()){
			int key = itr.next();
			int[][] value = map.get(key);
			int[] array = getArray(value, strategy);
			//insert testArray to candidateList
			int i;
			for(i=0;i<candidateList.size();i++){
				int iKey = candidateList.get(i);
				int[][] iValue = map.get(iKey);
				int[] iArray = getArray(iValue, strategy);
				int cResult = compareArray(array, iArray);
				if(cResult==GREATER||cResult==EQUAL){
					break;
				}
			}
			candidateList.add(i, key);
		}
		return candidateList;
	}
	 
	private int[] getMustStep(){
		int[] suggestPos=null;
		Way[] ways = { 
				new Get5(mV, null), 
		};
		
		for(int i=0;i<ways.length;i++){
			Way way=ways[i];
			suggestPos=way.suggestPosition();
			if(suggestPos!=null){
				Log.i(TAG, "getMustStep(...): " + "i = " + i);
				Log.i(TAG, "getMustStep(...): " + "suggestPos = " + Way.posString(suggestPos));
				return suggestPos;
			}
		}
		
		suggestPos = specialHandleForMust();
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}

	private int[] specialHandleForMust(){
		int[] suggestPos=null;
		Log.i(TAG, "specialHandleForAttackOrDefend(...): " + "Starting...");
		
		//如果计算机棋子有成Live4的可能，按Attack算
		GetLive4 way = new GetLive4(mV, null);
		int myType = mV.getMyQiZiType(), hisType=mV.getHisQiZiType();
		suggestPos = way.getLive4(myType);
		if(suggestPos!=null){
			Log.i(TAG, "specialHandleForAttackOrDefend(...): " + "myType Live 4");
			suggestPos = null;
			suggestPos = specialForWay3(LogicConfig.ATTACK);
			if(suggestPos!=null)
				return suggestPos;	
		}
		
		//如果计算机棋子有成Chong4Live3的可能，
		ArrayList<Integer> list1=getCandidateEmptyPos(myType); 
		Way2_1 yaway = new Way2_1(mV, null);
		suggestPos = yaway.getChong4andLive3(myType, list1);
		if(suggestPos!=null){
			Log.i(TAG, "specialHandleForAttackOrDefend(...): " + "myType Chong4Live3");
			return suggestPos;
		}
		
		//如果对方棋子有成Live4的可能，那么按“防守”算
		suggestPos = way.getLive4(hisType);
		if(suggestPos!=null){
			Log.i(TAG, "specialHandleForAttackOrDefend(...): " + "hisType Live4");
			suggestPos = null;
			suggestPos = specialForWay3(LogicConfig.DEFEND);
			if(suggestPos!=null)
				return suggestPos;
		}

		//如果对方棋子有成Chong4Live3的可能，
		ArrayList<Integer> list2=getCandidateEmptyPos(hisType);
		yaway = new Way2_1(mV, null);
		suggestPos = yaway.getChong4andLive3(hisType, list2);
		if(suggestPos!=null){
			Log.i(TAG, "specialHandleForAttackOrDefend(...): " + "hisType Chong4Live3");
			return suggestPos;
		}
		
		return suggestPos;
	}
	
	private int[] specialForWay3(int strategy){
		int[] suggestPos=null;
		
		int myType = mV.getMyQiZiType(), hisType = mV.getHisQiZiType();		
		ArrayList<Integer> list = getCandidateEmptyPos();
		HashMap<Integer, int[][]> map = initPosCountMap(list);
		//设置map的候选点的各项指标，即生成map
		Iterator<Integer> itr = map.keySet().iterator();
		while(itr.hasNext()){
			int key = itr.next();
			int[][] value = map.get(key);
			int[] pos = mV.oneDim2twoDim(key);
			startCount(value, myType, pos);
			startCount(value, hisType, pos);
		}
		//对map的各项item做排序
		ArrayList<Integer> candidateList = sort(map, strategy);
		
		//从candidateList中选出合适的position，即不是禁手的position（如何考虑黑棋禁手的话）
		for(int i=0;i<candidateList.size();i++){
			int[] pos = mV.oneDim2twoDim(candidateList.get(i));
			mV.set(pos, myType);
			int state = VirtualWuZi.CONTINUE;
			if(myType == VirtualWuZi.BLACK)
				state = mV.checkJinShou(myType, pos);
			if(state!=VirtualWuZi.CONTINUE){
				mV.set(pos, VirtualWuZi.EMPTY);
				continue;
			} else {
				mV.set(pos, VirtualWuZi.EMPTY);
				suggestPos = pos;
				break;
			}
		}

		return suggestPos;
	}
	
	@Override
	public int[] suggestPosition() {
		int[] suggestPos=null;
		suggestPos = getMustStep();
		if(suggestPos!=null)
			return suggestPos;

		//进入Way3的独有算法
		suggestPos = specialForWay3(mStrategy);
		if(suggestPos!=null)
			return suggestPos;
		
		return suggestPos;
	}	
}
