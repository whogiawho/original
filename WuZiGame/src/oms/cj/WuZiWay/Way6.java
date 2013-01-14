package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import oms.cj.WuZiLogic.ILiveOrChong;
import oms.cj.WuZiLogic.Result;
import oms.cj.WuZiLogic.VirtualWuZi;
import oms.cj.WuZiLogic.VirtualWuZiBoard;
import oms.cj.WuZiLogic.Way2ToLC;
import android.os.Handler;

public class Way6 extends Way {
	public final static String TAG = "Way6";
	
	private ILiveOrChong mILC;
	private ResultComparator mRComparator;
	Way6(VirtualWuZi v, Handler handler) {
		super(v, handler);
		
		mILC = new Way2ToLC();
		mRComparator = new ResultComparator();
	}

	private void dispatchResult(VirtualWuZiBoard b, int[] pos, int dir,
			ArrayList<Result> rList0, ArrayList<Result> rList1,
			ArrayList<Result> rList2, ArrayList<Result> rList3) {
		ArrayList<Result> rList=null;
		for(int i=0;i<VirtualWuZiBoard.LINEDIRTOTAL;i++){
			if(i==dir)
				continue;

			Result r = mILC.getLiveOrChong(b, pos, i);
			int rType = r.getType();
			switch(rType){
			case VirtualWuZiBoard.CHONG2:
			case VirtualWuZiBoard.LIVE1:
			case VirtualWuZiBoard.CHONG1:
				rList = rList3;
				break;
			case VirtualWuZiBoard.CHONG3:
			case VirtualWuZiBoard.LIVE2:
				rList = rList2;
				break;
			case VirtualWuZiBoard.CHONG4:
			case VirtualWuZiBoard.LIVE3:
				rList = rList1;
				break;
			case VirtualWuZiBoard.LIVE4:
			case VirtualWuZiBoard.CHONG5:
				rList = rList0;
				break;
			default:
				String out = String.format("invalid Result r=%d", r.toString());
				throw new IllegalStateException(out);
			}
			rList.add(r);
		}		
	}
	
	private void setCost(Cost c, int weight, int type){
		c.setWeight(weight);
		c.setType(type);
	}
	
	public Cost getCost(VirtualWuZiBoard b, int[] pos, int dir, int qiZiType){
		int idx = b.twoDim2oneDim(pos);
		Cost c = new Cost(b, idx);
		
		if(b.getQiZiType(pos)==VirtualWuZiBoard.EMPTY){
			boolean bFound = false;
			ArrayList<Result> rList0 = new ArrayList<Result>(); //C5L4
			ArrayList<Result> rList1 = new ArrayList<Result>(); //C4L3
			ArrayList<Result> rList2 = new ArrayList<Result>(); //C3L2
			ArrayList<Result> rList3 = new ArrayList<Result>(); //C2L1C1

			b.set(pos, qiZiType);	//set pos to qiZiType
			
			dispatchResult(b, pos, dir, rList0, rList1, rList2, rList3);
			if(rList3.size()==VirtualWuZiBoard.LINEDIRTOTAL-1){	//C2L1C1
				c.setWeight(Cost.COST1);
				bFound = true;
			} else if(rList0.size()!=0){						//C5L4
				c.setWeight(Cost.COST0);
				c.setType(Cost.TYPE0_OF_COST0);
				bFound = true;
			} else if(rList1.size()!=0){						//C4L3
				//sort rList1 firstly
				Collections.sort(rList1, mRComparator);
				for(int i=0;i<rList1.size();i++){
					Result r = rList1.get(i);
					int rType = r.getType();
					switch(rType){
					case VirtualWuZiBoard.CHONG4:
						bFound = handleC4(b, r, qiZiType);
						if(bFound)
							setCost(c, Cost.COST0, Cost.TYPE1_OF_COST0);
						break;
					case VirtualWuZiBoard.LIVE3:
						bFound = handleL3(b, r, qiZiType);
						if(bFound)
							setCost(c, Cost.COST0, Cost.TYPE2_OF_COST0);
						break;
					}
					if(bFound)
						break;
				}
			} 
			if(!bFound){
				if(rList2.size()!=0){							//C3L2
					//sort rList2 firstly
					Collections.sort(rList2, mRComparator);
					for(int i=0;i<rList2.size();i++){
						Result r = rList2.get(i);
						int cDir = r.getDir();
						HashSet<Integer> setEx = r.getFirstQiZiSetEx();
						Iterator<Integer> itr = setEx.iterator();
						while(itr.hasNext()){
							int cIdx = itr.next();
							int[] cPos = b.oneDim2twoDim(cIdx);
							Cost c0 = getCost(b, cPos,cDir, qiZiType);
						}
					}
				}				
			}
			
			b.set(pos, VirtualWuZiBoard.EMPTY);	//restore pos to EMPTY			
		} else {
			String out = String.format("Invalid position = %s", Way.posString(pos));
			throw new IllegalStateException(out);
		}
		
		return c;
	}

	private boolean handleC4(VirtualWuZiBoard b, Result r, int qiZiType){
		boolean bFound = true;
		
		HashSet<Integer> firstExSet = r.getFirstQiZiSetEx();
		Iterator<Integer> itr = firstExSet.iterator();

_L0:		
		while(itr.hasNext()){
			int tIdx = itr.next();
			int[] cPos = b.oneDim2twoDim(tIdx);
			b.set(tIdx, VirtualWuZiBoard.reverse(qiZiType));
			for(int k=0;k<VirtualWuZiBoard.LINEDIRTOTAL;k++){
				if(k==r.getDir())
					continue;
				Result cR = mILC.getLiveOrChong(b, cPos, k);
				int crType = cR.getType();
				if(crType!=VirtualWuZiBoard.CHONG4||
						crType!=VirtualWuZiBoard.LIVE4||
						crType!=VirtualWuZiBoard.CHONG5){
					bFound = false;
					b.set(tIdx, VirtualWuZiBoard.EMPTY);
					break _L0;
				}
			}
			b.set(tIdx, VirtualWuZiBoard.EMPTY);
		}
		
		return bFound;
	}

	private boolean handleL3(VirtualWuZiBoard b, Result r, int qiZiType){
		boolean bFound = true;
		
		HashSet<Integer> firstExSet = r.getFirstQiZiSetEx();
		Iterator<Integer> itr = firstExSet.iterator();

_L0:		
		while(itr.hasNext()){
			int tIdx = itr.next();
			int[] cPos = b.oneDim2twoDim(tIdx);
			b.set(tIdx, VirtualWuZiBoard.reverse(qiZiType));
			for(int k=0;k<VirtualWuZiBoard.LINEDIRTOTAL;k++){
				if(k==r.getDir())
					continue;
				Result cR = mILC.getLiveOrChong(b, cPos, k);
				int crType = cR.getType();
				if(crType==VirtualWuZiBoard.LIVE3||
						crType==VirtualWuZiBoard.CHONG4||
						crType==VirtualWuZiBoard.LIVE4||
						crType==VirtualWuZiBoard.CHONG5){
					bFound = false;
					b.set(tIdx, VirtualWuZiBoard.EMPTY);
					break _L0;
				}
			}
			b.set(tIdx, VirtualWuZiBoard.EMPTY);
		}

		return bFound;
	}

	@Override
	public int[] suggestPosition() {
		int[] retPos = null;
				
		return retPos;
	}
	
	class ResultComparator implements Comparator<Result> {

		@Override
		public int compare(Result object1, Result object2) {
			int rType1 = object1.getType();
			int rType2 = object2.getType();
			int idx1 = VirtualWuZiBoard.priorityList.indexOf(rType1);
			int idx2 = VirtualWuZiBoard.priorityList.indexOf(rType2);
			
			if(idx1<idx2)
				return -1;
			else if(idx1>idx2)
				return 1;
			else
				return 0;
		}
		
	}
	
	class Sequence {
		int pos;
		int type;
	}
	
	class Cost {
		public final static int COST0 = 0;
		public final static int COST1 = 1;
		public final static int TYPE_OF_INVALID = -1;
		public final static int TYPE0_OF_COST0 = 0;	//LIVE4, CHONG5
		public final static int TYPE1_OF_COST0 = 1;	//CHONG4
		public final static int TYPE2_OF_COST0 = 2;	//LIVE3
		
		private int mIdx;
		private int mWeight;
		private int mType;	//only valid if mCost==0
		private ArrayList<Sequence> mQiZiSequence;
		
		Cost(VirtualWuZiBoard board, int idx){
			mIdx = idx;
			
			mWeight = COST1;
			mType = TYPE_OF_INVALID;
			mQiZiSequence = new ArrayList<Sequence>();
		}
		
		public int[] getPosition(VirtualWuZiBoard b){
			return b.oneDim2twoDim(mIdx);
		}
		
		public int getPosition(){
			return mIdx;
		}
		
		public int getWeight() {
			return mWeight;
		}
		public void setWeight(int w){
			mWeight = w;
		}
		
		public int getType() {
			return mType;
		}
		public void setType(int type){
			mType = type;
		}
		
		public Sequence getNthSequence(int n){
			 return mQiZiSequence.get(n);
		}
		
		public void addSequence(Sequence s){
			mQiZiSequence.add(s);
		}
	}
}
