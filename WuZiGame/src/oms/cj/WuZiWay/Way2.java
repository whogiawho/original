package oms.cj.WuZiWay;

import java.util.ArrayList;
import java.util.Random;

import oms.cj.WuZiLogic.ISuggest;
import oms.cj.WuZiLogic.VirtualWuZi;

import android.os.Handler;
import android.util.Log;

//Way2的判断序列是:
//1. 能成5
//2. 能成活4
//3. 能成冲4
//4. 可选空位
//5. 随机位置

public class Way2 extends Way implements ISuggest{
	private final static String TAG="Way2";
	protected final static int[] IMPOSSIBLE={-1, -1};

	protected ArrayList<Integer> blackDeathQiZiList=new ArrayList<Integer>();
	protected ArrayList<Integer> whiteDeathQiZiList=new ArrayList<Integer>();
	
	public Way2(VirtualWuZi v, Handler handler){
		super(v, handler);
		
		updateDeathList();
	}

	public void updateDeathList(){
		updateDeathList(VirtualWuZi.BLACK);
		updateDeathList(VirtualWuZi.WHITE);		
	}
	
	public void updateDeathList(int type){
		ArrayList<Integer> deathList, qiZiList;;
		
		qiZiList=mV.getQiZiList(type);
		deathList=getDeathList(type);
		for(int i=0;i<qiZiList.size();i++){
			int idx=qiZiList.get(i);
			if(deathList.indexOf(idx)==-1){
				int[] pos=mV.oneDim2twoDim(idx);
				PosInfo info=new PosInfo(pos);
				if(info.isDead())
					deathList.add(idx);
			}
		}
	}
	
	@Override
	public int[] suggestPosition() {
		//先更新deathList
		updateDeathList();
		
		int[] suggestPos=null;
		int hisType=mV.getHisQiZiType();
		int myType=mV.getMyQiZiType();
		
		//如果计算机或对方有活4或冲4，那么返回该suggestPos
		suggestPos=getLive4orChong4Must(myType);
		if(suggestPos!=null)
			return suggestPos;
		suggestPos=getLive4orChong4Must(hisType);
		if(suggestPos!=null)
			return suggestPos;
		
		//如果计算机或对方有活3，那么返回该suggestPos
		suggestPos=getLive3Must(myType);
		if(suggestPos!=null)
			return suggestPos;
		suggestPos=getLive3Must(hisType);
		if(suggestPos!=null)
			return suggestPos;
				
		//如果计算机或对方有冲3，那么随机返回一个可能的冲三空位
		suggestPos=getChong3Opt(myType);
		if(suggestPos!=null)
			return suggestPos;
		suggestPos=getChong3Opt(hisType);
		if(suggestPos!=null)
			return suggestPos;

		//随机返回可选空位中的一个
		suggestPos=this.getByCandidateEmptyPos();
		if(suggestPos!=null)
			return suggestPos;
		
		//最后的方法是用RandomWay随机返回一个空位
		suggestPos=getByRandomWay();
		return suggestPos;
	}
	
	//取得所有可能的冲3的empty位置列表，随机返回一个
	protected int[] getChong3Opt(int qiZiType){
		int[] suggestPos=null;
		ArrayList<Integer> emptyPosList=getChong3EmptyPosList(qiZiType);
		int size=emptyPosList.size();
		
		if(size!=0){
			Log.i(TAG, "getChong3Opt(...): " + "emptyPosList=" + this.posString(emptyPosList));
			Random r=new Random();
			int randomNum=r.nextInt(size);
			int idx=emptyPosList.get(randomNum);
			suggestPos=mV.oneDim2twoDim(idx);
		}
		return suggestPos;
	}
	
	//返回能使冲3成冲4的其余几个空位
	private ArrayList<Integer> getChong3EmptyPosList(int qiZiType){
		ArrayList<Integer> list=new ArrayList<Integer>();
		ArrayList<Integer> deathList=getDeathList(qiZiType);
		ArrayList<Integer> qiZiList=mV.getQiZiList(qiZiType);
		
		for(int i=0;i<qiZiList.size();i++){
			Integer qiZiIdx=qiZiList.get(i);
			int[] pos = mV.oneDim2twoDim(qiZiIdx);	
			if(deathList.indexOf(qiZiIdx)!=-1)
				continue;
			else {
				Log.i(TAG, "getChong3EmptyPosList(...): " + "not a death position!");
				for(int j=0;j<VirtualWuZi.LINEDIRTOTAL;j++){
					ArrayList<Integer> emptyPosList=new ArrayList<Integer>();
					if(isChongSan(qiZiType, pos, j, emptyPosList)){
						Log.i(TAG, "getChong3EmptyPosList(...): " + "pos is a Chong3;" + "pos=" + Way.posString(pos));
						for(int k=0;k<emptyPosList.size();k++){
							int idx=emptyPosList.get(k);
							if(list.indexOf(idx)==-1)
								list.add(idx);
						}
					}
				}
			}
		}		
		return list;
	}
	
	private boolean isChongSan(int qiZiType, int[] pos, int dir, ArrayList<Integer> list){
		boolean bChongSan=false;

		if(list!=null)
			list.clear();
		for(int i=0;i<9;i++){
			int[] checkPos=VirtualWuZi.delta(pos, dir, i-4);
			if(!mV.inBox(checkPos)||mV.getQiZiType(checkPos)!=VirtualWuZi.EMPTY)
				continue;
			mV.set(checkPos, qiZiType);
			if(mV.getChongSiCount(qiZiType, pos, dir, null, null)>=1){
				bChongSan=true;
				mV.set(checkPos, VirtualWuZi.EMPTY);
				if(list!=null)
					list.add(mV.twoDim2oneDim(checkPos));
				continue;
			} else
				mV.set(checkPos, VirtualWuZi.EMPTY);
		}
		return bChongSan;
	}

	private ArrayList<Integer> getDeathList(int qiZiType){
		if(qiZiType==VirtualWuZi.BLACK)
			return blackDeathQiZiList;
		else if(qiZiType==VirtualWuZi.WHITE)
			return whiteDeathQiZiList;
		else {
			Log.e(TAG, "getDeathList(...)" + "invalid type: " + "qiZiType=" + qiZiType);
			return null;
		}
	}
	
	private int[] getLive4orChong4Must(int qiZiType, int[] pos){
		int[] newPos=null;
		ArrayList<Integer> list=new ArrayList<Integer>();
		ArrayList<Integer> emptyList=new ArrayList<Integer>();
		
		for(int i=0;i<VirtualWuZi.LINEDIRTOTAL;i++){
			//检查是否有活4
			list.clear();
			if(mV.isLiveSi(qiZiType, pos, i, list)){
				Integer firstPos=list.get(0);
				int[] start=mV.oneDim2twoDim(firstPos);
				newPos=VirtualWuZi.delta(start, i, -1);	
				break;
			}
			//检查是否有冲4
			emptyList.clear();
			if(mV.getChongSiCount(qiZiType, pos, i, null, emptyList)>=1){
				Integer firstPos=emptyList.get(0);
				newPos=mV.oneDim2twoDim(firstPos);
				break;
			}
		}	
		return newPos;
	}
	
	protected int[] getLive4orChong4Must(int qiZiType){
		int[] suggestPos=null;
		ArrayList<Integer> deathList=getDeathList(qiZiType);
		ArrayList<Integer> qiZiList=mV.getQiZiList(qiZiType);
		
		for(int i=0;i<qiZiList.size();i++){
			Integer qiZiIdx=qiZiList.get(i);
			int[] pos = mV.oneDim2twoDim(qiZiIdx);	
			if(deathList.indexOf(qiZiIdx)!=-1)
				continue;
			else {
				suggestPos=getLive4orChong4Must(qiZiType, pos);  
				if(suggestPos!=null){
					break;
				} 
			}
		}		
		return suggestPos;
	}
	
	protected int[] getLive3Must(int qiZiType){
		int[] suggestPos=null;
		ArrayList<Integer> deathList=getDeathList(qiZiType);
		ArrayList<Integer> qiZiList=mV.getQiZiList(qiZiType);
		
		for(int i=0;i<qiZiList.size();i++){
			Integer qiZiIdx=qiZiList.get(i);
			int[] pos = mV.oneDim2twoDim(qiZiIdx);	
			if(deathList.indexOf(qiZiIdx)!=-1)
				continue;
			else {
				suggestPos=getLive3Must(qiZiType, pos);
				if(suggestPos!=null){
					break;
				} 
			}
		}
		return suggestPos;
	}
	
	private int[] getLive3Must(int qiZiType, int[]pos){
		int[] newPos=null;
		int[] emptyPos=IMPOSSIBLE.clone();
		
		for(int i=0;i<VirtualWuZi.LINEDIRTOTAL;i++){ 
			if(mV.isLiveSan(qiZiType, pos, i, null, emptyPos)){
				newPos=emptyPos;
				break;
			}						
		}
		return newPos;
	}
	
	class PosInfo{
		private int[] mPos;
		private int[] mMaxSpace=new int[VirtualWuZi.LINEDIRTOTAL];
		private int qiZiType;
		
		PosInfo(int[] pos){	
			mPos=pos;
			qiZiType=mV.getQiZiType(mPos);
			if(qiZiType==VirtualWuZi.EMPTY){//如pos处没有棋子，那么取myQiZiType
				qiZiType=mV.getMyQiZiType();
			}
			for(int i=0;i<VirtualWuZi.LINEDIRTOTAL;i++){
				mMaxSpace[i]=0;
			}

			getMaxSpace(VirtualWuZi.L2R);
			getMaxSpace(VirtualWuZi.T2B);
			getMaxSpace(VirtualWuZi.LB2RT);
			getMaxSpace(VirtualWuZi.LT2RB);
		}
		
		public boolean isDead(int dir){
			boolean bDead=false;
			
			if(mMaxSpace[dir]>=5)
				bDead=true;
			return bDead;
		}
		
		public boolean isDead(){
			boolean bDead=true;
			
			for(int i=0;i<VirtualWuZi.LINEDIRTOTAL;i++){
				if(mMaxSpace[i]>=5){
					bDead=false;
					break;
				}
			}
			return bDead;
		}
		
		private void getMaxSpace(int dir){
			int[] next;
			
			//先往负向走
			next=VirtualWuZi.delta(mPos, dir, -1);
			while(mV.inBox(next)){
				int type=mV.getQiZiType(next);
				if(type==qiZiType){ 
					mMaxSpace[dir]++;
					next=VirtualWuZi.delta(next, dir, -1);
				} else if(type==VirtualWuZi.EMPTY) {
					mMaxSpace[dir]++;
					next=VirtualWuZi.delta(next, dir, -1);
				} else 
					break;
			}
			
			//再往正向走,从自己开始
			next=VirtualWuZi.delta(mPos, dir, 0);
			while(mV.inBox(next)){
				int type=mV.getQiZiType(next);
				if(type==qiZiType){
					mMaxSpace[dir]++;
					next=VirtualWuZi.delta(next, dir, 1);
				} else if(type==VirtualWuZi.EMPTY) {
					mMaxSpace[dir]++;
					next=VirtualWuZi.delta(next, dir, 1);
				} else 
					break;
			}	
		}
	}
}